package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AboutMeEntity
import com.example.data.local.AchievementEntity
import com.example.data.local.AppSettings
import com.example.data.local.ContactLinkEntity
import com.example.data.local.DreamGoalEntity
import com.example.data.local.EducationEntity
import com.example.data.local.FamilyInfoEntity
import com.example.data.local.FamilyMemberEntity
import com.example.data.local.GoalStatus
import com.example.data.local.MemoryEntity
import com.example.data.local.PersonalProfileEntity
import com.example.data.repository.MyLifeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MyLifeUiState(
    val isLoading: Boolean = true,
    val settings: AppSettings = AppSettings(),
    val personalProfile: PersonalProfileEntity = PersonalProfileEntity(),
    val aboutMe: AboutMeEntity = AboutMeEntity(),
    val achievements: List<AchievementEntity> = emptyList(),
    val familyInfo: FamilyInfoEntity = FamilyInfoEntity(),
    val familyMembers: List<FamilyMemberEntity> = emptyList(),
    val educationList: List<EducationEntity> = emptyList(),
    val memories: List<MemoryEntity> = emptyList(),
    val dreamsGoals: List<DreamGoalEntity> = emptyList(),
    val contactLinks: List<ContactLinkEntity> = emptyList()
) {
    val profileCompletenessPercent: Int
        get() {
            var score = 0
            val total = 7
            if (personalProfile.fullName.isNotBlank()) score++
            if (familyInfo.fatherName.isNotBlank() || familyInfo.motherName.isNotBlank() || familyMembers.isNotEmpty()) score++
            if (educationList.isNotEmpty()) score++
            if (aboutMe.likes.isNotBlank() || aboutMe.hobbies.isNotBlank() || achievements.isNotEmpty()) score++
            if (memories.isNotEmpty()) score++
            if (dreamsGoals.isNotEmpty()) score++
            if (contactLinks.isNotEmpty() || personalProfile.mobile.isNotBlank() || personalProfile.email.isNotBlank()) score++
            return (score * 100) / total
        }

    val completedGoalsCount: Int
        get() = dreamsGoals.count { it.status == GoalStatus.COMPLETED }
}

sealed interface UiEvent {
    data class ShowMessage(val message: String) : UiEvent
    data class ShareBackupText(val jsonContent: String) : UiEvent
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MyLifeRepository(application.applicationContext)

    private val _isUnlocked = MutableStateFlow(true)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _lockError = MutableStateFlow(false)
    val lockError: StateFlow<Boolean> = _lockError.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    private var initializedLockCheck = false

    private val coreDataFlow = combine(
        repository.settingsFlow,
        repository.personalProfileFlow,
        repository.aboutMeFlow,
        repository.achievementsFlow,
        repository.familyInfoFlow
    ) { settings, profile, about, achievements, familyInfo ->
        CoreBundle(settings, profile, about, achievements, familyInfo)
    }

    private val listDataFlow = combine(
        repository.familyMembersFlow,
        repository.educationFlow,
        repository.memoriesFlow,
        repository.dreamsGoalsFlow,
        repository.contactLinksFlow
    ) { members, education, memories, dreams, contacts ->
        ListBundle(members, education, memories, dreams, contacts)
    }

    val uiState: StateFlow<MyLifeUiState> = combine(coreDataFlow, listDataFlow) { core, lists ->
        if (!initializedLockCheck) {
            initializedLockCheck = true
            if (core.settings.lockEnabled && core.settings.lockHash.isNotBlank()) {
                _isUnlocked.value = false
            }
        }
        MyLifeUiState(
            isLoading = false,
            settings = core.settings,
            personalProfile = core.profile ?: PersonalProfileEntity(),
            aboutMe = core.about ?: AboutMeEntity(),
            achievements = core.achievements,
            familyInfo = core.familyInfo ?: FamilyInfoEntity(),
            familyMembers = lists.members,
            educationList = lists.education,
            memories = lists.memories,
            dreamsGoals = lists.dreams,
            contactLinks = lists.contacts
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MyLifeUiState()
    )

    init {
        viewModelScope.launch {
            val initialSettings = repository.settingsFlow.first()
            if (!initializedLockCheck) {
                initializedLockCheck = true
                _isUnlocked.value = !(initialSettings.lockEnabled && initialSettings.lockHash.isNotBlank())
            }
        }
    }

    // Security Lock
    fun attemptUnlock(secret: String) {
        val currentSettings = uiState.value.settings
        if (repository.verifyLockCredential(secret, currentSettings)) {
            _lockError.value = false
            _isUnlocked.value = true
        } else {
            _lockError.value = true
        }
    }

    fun clearLockError() {
        _lockError.value = false
    }

    fun lockNow() {
        val settings = uiState.value.settings
        if (settings.lockEnabled && settings.lockHash.isNotBlank()) {
            _lockError.value = false
            _isUnlocked.value = false
        }
    }

    fun setLockCredential(type: String, secret: String, successMsg: String) {
        viewModelScope.launch {
            repository.setLockCredential(type, secret)
            _isUnlocked.value = true
            _events.emit(UiEvent.ShowMessage(successMsg))
        }
    }

    fun disableLock(successMsg: String) {
        viewModelScope.launch {
            repository.disableLock()
            _isUnlocked.value = true
            _events.emit(UiEvent.ShowMessage(successMsg))
        }
    }

    // Language & Theme
    fun toggleLanguage() {
        val next = if (uiState.value.settings.language == "hi") "en" else "hi"
        viewModelScope.launch { repository.setLanguage(next) }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { repository.setLanguage(lang) }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    // Media helpers
    suspend fun savePickedMedia(uri: Uri, defaultExt: String = "jpg"): String =
        repository.savePickedMedia(uri, defaultExt)

    suspend fun getVideoThumbnail(videoPath: String): String? =
        repository.getVideoThumbnail(videoPath)

    // Personal Profile CRUD
    fun savePersonalProfile(profile: PersonalProfileEntity) {
        viewModelScope.launch { repository.savePersonalProfile(profile) }
    }

    fun updateProfilePhoto(uri: Uri) {
        viewModelScope.launch {
            val savedPath = repository.savePickedMedia(uri, "jpg")
            val current = uiState.value.personalProfile
            repository.savePersonalProfile(current.copy(photoPath = savedPath))
        }
    }

    fun removeProfilePhoto() {
        viewModelScope.launch {
            val current = uiState.value.personalProfile
            repository.savePersonalProfile(current.copy(photoPath = ""))
        }
    }

    fun clearPersonalProfile() {
        viewModelScope.launch { repository.clearPersonalProfile() }
    }

    // About Me & Achievements CRUD
    fun saveAboutMe(aboutMe: AboutMeEntity) {
        viewModelScope.launch { repository.saveAboutMe(aboutMe) }
    }

    fun saveAchievement(achievement: AchievementEntity) {
        viewModelScope.launch { repository.saveAchievement(achievement) }
    }

    fun deleteAchievement(id: Long) {
        viewModelScope.launch { repository.deleteAchievement(id) }
    }

    // Family Info & Members CRUD
    fun saveFamilyInfo(familyInfo: FamilyInfoEntity) {
        viewModelScope.launch { repository.saveFamilyInfo(familyInfo) }
    }

    fun updateFamilyPhoto(uri: Uri) {
        viewModelScope.launch {
            val savedPath = repository.savePickedMedia(uri, "jpg")
            val current = uiState.value.familyInfo
            repository.saveFamilyInfo(current.copy(familyPhotoPath = savedPath))
        }
    }

    fun saveFamilyMember(member: FamilyMemberEntity) {
        viewModelScope.launch { repository.saveFamilyMember(member) }
    }

    fun deleteFamilyMember(member: FamilyMemberEntity) {
        viewModelScope.launch { repository.deleteFamilyMember(member) }
    }

    // Education CRUD
    fun saveEducation(education: EducationEntity) {
        viewModelScope.launch { repository.saveEducation(education) }
    }

    fun deleteEducation(id: Long) {
        viewModelScope.launch { repository.deleteEducation(id) }
    }

    // Memories CRUD
    fun saveMemory(memory: MemoryEntity) {
        viewModelScope.launch { repository.saveMemory(memory) }
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch { repository.deleteMemory(memory) }
    }

    // Dreams & Goals CRUD
    fun saveDreamGoal(item: DreamGoalEntity) {
        viewModelScope.launch { repository.saveDreamGoal(item) }
    }

    fun cycleGoalStatus(item: DreamGoalEntity) {
        val nextStatus = when (item.status) {
            GoalStatus.NOT_STARTED -> GoalStatus.IN_PROGRESS
            GoalStatus.IN_PROGRESS -> GoalStatus.COMPLETED
            else -> GoalStatus.NOT_STARTED
        }
        val nextProgress = when (nextStatus) {
            GoalStatus.NOT_STARTED -> 0
            GoalStatus.IN_PROGRESS -> if (item.progressPercent in 1..99) item.progressPercent else 50
            else -> 100
        }
        viewModelScope.launch {
            repository.saveDreamGoal(item.copy(status = nextStatus, progressPercent = nextProgress))
        }
    }

    fun deleteDreamGoal(id: Long) {
        viewModelScope.launch { repository.deleteDreamGoal(id) }
    }

    // Contact Links CRUD
    fun saveContactLink(link: ContactLinkEntity) {
        viewModelScope.launch { repository.saveContactLink(link) }
    }

    fun deleteContactLink(id: Long) {
        viewModelScope.launch { repository.deleteContactLink(id) }
    }

    // Backup & Restore
    fun exportBackupToUri(uri: Uri, successMsg: String, errorMsg: String) {
        viewModelScope.launch {
            try {
                repository.exportBackupToUri(uri)
                _events.emit(UiEvent.ShowMessage(successMsg))
            } catch (e: Exception) {
                _events.emit(UiEvent.ShowMessage("$errorMsg: ${e.localizedMessage ?: ""}"))
            }
        }
    }

    fun shareBackup(errorMsg: String) {
        viewModelScope.launch {
            try {
                val json = repository.exportBackupJsonString()
                _events.emit(UiEvent.ShareBackupText(json))
            } catch (e: Exception) {
                _events.emit(UiEvent.ShowMessage("$errorMsg: ${e.localizedMessage ?: ""}"))
            }
        }
    }

    fun restoreBackupFromUri(uri: Uri, successMsg: String, errorMsg: String) {
        viewModelScope.launch {
            try {
                repository.restoreBackupFromUri(uri)
                _events.emit(UiEvent.ShowMessage(successMsg))
            } catch (e: Exception) {
                _events.emit(UiEvent.ShowMessage("$errorMsg: ${e.localizedMessage ?: ""}"))
            }
        }
    }

    private data class CoreBundle(
        val settings: AppSettings,
        val profile: PersonalProfileEntity?,
        val about: AboutMeEntity?,
        val achievements: List<AchievementEntity>,
        val familyInfo: FamilyInfoEntity?
    )

    private data class ListBundle(
        val members: List<FamilyMemberEntity>,
        val education: List<EducationEntity>,
        val memories: List<MemoryEntity>,
        val dreams: List<DreamGoalEntity>,
        val contacts: List<ContactLinkEntity>
    )
}
