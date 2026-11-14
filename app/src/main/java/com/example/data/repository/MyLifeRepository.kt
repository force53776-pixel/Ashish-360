package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.AboutMeEntity
import com.example.data.local.AchievementEntity
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettings
import com.example.data.local.BackupManager
import com.example.data.local.ContactLinkEntity
import com.example.data.local.DreamGoalEntity
import com.example.data.local.EducationEntity
import com.example.data.local.FamilyInfoEntity
import com.example.data.local.FamilyMemberEntity
import com.example.data.local.MediaStorageHelper
import com.example.data.local.MemoryEntity
import com.example.data.local.PersonalProfileEntity
import com.example.data.local.SettingsDataStore
import kotlinx.coroutines.flow.Flow

class MyLifeRepository(private val context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val dao = database.profileDao()
    private val settingsStore = SettingsDataStore(context)
    private val mediaHelper = MediaStorageHelper(context)
    private val backupManager = BackupManager(context, dao)

    val settingsFlow: Flow<AppSettings> = settingsStore.settingsFlow
    val personalProfileFlow: Flow<PersonalProfileEntity?> = dao.observePersonalProfile()
    val aboutMeFlow: Flow<AboutMeEntity?> = dao.observeAboutMe()
    val achievementsFlow: Flow<List<AchievementEntity>> = dao.observeAchievements()
    val familyInfoFlow: Flow<FamilyInfoEntity?> = dao.observeFamilyInfo()
    val familyMembersFlow: Flow<List<FamilyMemberEntity>> = dao.observeFamilyMembers()
    val educationFlow: Flow<List<EducationEntity>> = dao.observeEducation()
    val memoriesFlow: Flow<List<MemoryEntity>> = dao.observeMemories()
    val dreamsGoalsFlow: Flow<List<DreamGoalEntity>> = dao.observeDreamsGoals()
    val contactLinksFlow: Flow<List<ContactLinkEntity>> = dao.observeContactLinks()

    // Settings & Security
    suspend fun setLanguage(lang: String) = settingsStore.setLanguage(lang)
    suspend fun setThemeMode(mode: String) = settingsStore.setThemeMode(mode)
    suspend fun setLockCredential(type: String, secret: String) =
        settingsStore.setLockCredential(type, secret)

    suspend fun disableLock() = settingsStore.disableLock()
    fun verifyLockCredential(secret: String, settings: AppSettings): Boolean =
        settingsStore.verifyCredential(secret, settings)

    // Media helpers
    suspend fun savePickedMedia(uri: Uri, defaultExt: String = "jpg"): String =
        mediaHelper.copyUriToInternalStorage(uri, defaultExt)

    suspend fun getVideoThumbnail(videoPath: String): String? =
        mediaHelper.getOrCreateVideoThumbnail(videoPath)

    // Personal Profile
    suspend fun savePersonalProfile(profile: PersonalProfileEntity) {
        val existing = dao.getPersonalProfileOnce()
        if (existing != null && existing.photoPath.isNotBlank() && existing.photoPath != profile.photoPath) {
            mediaHelper.deleteMediaFile(existing.photoPath)
        }
        dao.upsertPersonalProfile(profile.copy(id = 1, updatedAt = System.currentTimeMillis()))
    }

    suspend fun clearPersonalProfile() {
        val existing = dao.getPersonalProfileOnce()
        if (existing != null && existing.photoPath.isNotBlank()) {
            mediaHelper.deleteMediaFile(existing.photoPath)
        }
        dao.clearPersonalProfile()
    }

    // About Me & Achievements
    suspend fun saveAboutMe(aboutMe: AboutMeEntity) {
        dao.upsertAboutMe(aboutMe.copy(id = 1, updatedAt = System.currentTimeMillis()))
    }

    suspend fun clearAboutMe() = dao.clearAboutMe()

    suspend fun saveAchievement(achievement: AchievementEntity) =
        dao.upsertAchievement(achievement)

    suspend fun deleteAchievement(id: Long) = dao.deleteAchievement(id)

    // Family Info & Members
    suspend fun saveFamilyInfo(familyInfo: FamilyInfoEntity) {
        val existing = dao.getFamilyInfoOnce()
        if (existing != null && existing.familyPhotoPath.isNotBlank() && existing.familyPhotoPath != familyInfo.familyPhotoPath) {
            mediaHelper.deleteMediaFile(existing.familyPhotoPath)
        }
        dao.upsertFamilyInfo(familyInfo.copy(id = 1, updatedAt = System.currentTimeMillis()))
    }

    suspend fun clearFamilyInfo() {
        val existing = dao.getFamilyInfoOnce()
        if (existing != null && existing.familyPhotoPath.isNotBlank()) {
            mediaHelper.deleteMediaFile(existing.familyPhotoPath)
        }
        dao.clearFamilyInfo()
    }

    suspend fun saveFamilyMember(member: FamilyMemberEntity) =
        dao.upsertFamilyMember(member)

    suspend fun deleteFamilyMember(member: FamilyMemberEntity) {
        if (member.photoPath.isNotBlank()) {
            mediaHelper.deleteMediaFile(member.photoPath)
        }
        dao.deleteFamilyMember(member.id)
    }

    // Education
    suspend fun saveEducation(education: EducationEntity) = dao.upsertEducation(education)
    suspend fun deleteEducation(id: Long) = dao.deleteEducation(id)

    // Memories
    suspend fun saveMemory(memory: MemoryEntity) = dao.upsertMemory(memory)
    suspend fun deleteMemory(memory: MemoryEntity) {
        if (memory.mediaPath.isNotBlank()) {
            mediaHelper.deleteMediaFile(memory.mediaPath)
        }
        dao.deleteMemory(memory.id)
    }

    // Dreams & Goals
    suspend fun saveDreamGoal(item: DreamGoalEntity) = dao.upsertDreamGoal(item)
    suspend fun deleteDreamGoal(id: Long) = dao.deleteDreamGoal(id)

    // Contact Links
    suspend fun saveContactLink(link: ContactLinkEntity) = dao.upsertContactLink(link)
    suspend fun deleteContactLink(id: Long) = dao.deleteContactLink(id)

    // Backup & Restore
    suspend fun exportBackupToUri(uri: Uri) = backupManager.writeBackupToUri(uri)
    suspend fun exportBackupJsonString(): String = backupManager.exportJsonString()
    suspend fun restoreBackupFromUri(uri: Uri) = backupManager.restoreBackupFromUri(uri)
}
