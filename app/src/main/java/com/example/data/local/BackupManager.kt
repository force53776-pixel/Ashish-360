package com.example.data.local

import android.content.Context
import android.net.Uri
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class BackupPersonalProfile(
    val fullName: String = "",
    val tagline: String = "",
    val photoPath: String = "",
    val dateOfBirth: String = "",
    val address: String = "",
    val mobile: String = "",
    val email: String = ""
)

@JsonClass(generateAdapter = true)
data class BackupAboutMe(
    val bioSummary: String = "",
    val likes: String = "",
    val interests: String = "",
    val hobbies: String = "",
    val lifeGoal: String = ""
)

@JsonClass(generateAdapter = true)
data class BackupAchievement(
    val title: String = "",
    val year: String = "",
    val category: String = "",
    val description: String = ""
)

@JsonClass(generateAdapter = true)
data class BackupFamilyInfo(
    val fatherName: String = "",
    val motherName: String = "",
    val familyPhotoPath: String = "",
    val familyQuote: String = ""
)

@JsonClass(generateAdapter = true)
data class BackupFamilyMember(
    val name: String = "",
    val relation: String = "",
    val phone: String = "",
    val notes: String = "",
    val photoPath: String = ""
)

@JsonClass(generateAdapter = true)
data class BackupEducation(
    val institutionName: String = "",
    val level: String = "",
    val fieldOfStudy: String = "",
    val qualification: String = "",
    val startYear: String = "",
    val endYear: String = "",
    val gradeOrScore: String = "",
    val isCurrent: Boolean = false
)

@JsonClass(generateAdapter = true)
data class BackupMemory(
    val title: String = "",
    val description: String = "",
    val memoryDate: String = "",
    val location: String = "",
    val mediaPath: String = "",
    val mediaType: String = MemoryMediaType.PHOTO
)

@JsonClass(generateAdapter = true)
data class BackupDreamGoal(
    val title: String = "",
    val description: String = "",
    val category: String = DreamCategory.GOAL,
    val targetDate: String = "",
    val status: String = GoalStatus.IN_PROGRESS,
    val progressPercent: Int = 0
)

@JsonClass(generateAdapter = true)
data class BackupContactLink(
    val label: String = "",
    val platform: String = ContactPlatform.OTHER,
    val value: String = "",
    val isPrimary: Boolean = false
)

@JsonClass(generateAdapter = true)
data class MyLifeBackupPayload(
    val appName: String = "My Life – मेरी पहचान",
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val personalProfile: BackupPersonalProfile? = null,
    val aboutMe: BackupAboutMe? = null,
    val achievements: List<BackupAchievement> = emptyList(),
    val familyInfo: BackupFamilyInfo? = null,
    val familyMembers: List<BackupFamilyMember> = emptyList(),
    val education: List<BackupEducation> = emptyList(),
    val memories: List<BackupMemory> = emptyList(),
    val dreamsGoals: List<BackupDreamGoal> = emptyList(),
    val contactLinks: List<BackupContactLink> = emptyList()
)

class BackupManager(
    private val context: Context,
    private val dao: ProfileDao
) {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(MyLifeBackupPayload::class.java).indent("  ")

    suspend fun exportJsonString(): String = withContext(Dispatchers.IO) {
        val profile = dao.getPersonalProfileOnce()
        val aboutMe = dao.getAboutMeOnce()
        val achievements = dao.getAchievementsOnce()
        val familyInfo = dao.getFamilyInfoOnce()
        val familyMembers = dao.getFamilyMembersOnce()
        val education = dao.getEducationOnce()
        val memories = dao.getMemoriesOnce()
        val dreamsGoals = dao.getDreamsGoalsOnce()
        val contactLinks = dao.getContactLinksOnce()

        val payload = MyLifeBackupPayload(
            personalProfile = profile?.let {
                BackupPersonalProfile(
                    fullName = it.fullName,
                    tagline = it.tagline,
                    photoPath = it.photoPath,
                    dateOfBirth = it.dateOfBirth,
                    address = it.address,
                    mobile = it.mobile,
                    email = it.email
                )
            },
            aboutMe = aboutMe?.let {
                BackupAboutMe(
                    bioSummary = it.bioSummary,
                    likes = it.likes,
                    interests = it.interests,
                    hobbies = it.hobbies,
                    lifeGoal = it.lifeGoal
                )
            },
            achievements = achievements.map {
                BackupAchievement(
                    title = it.title,
                    year = it.year,
                    category = it.category,
                    description = it.description
                )
            },
            familyInfo = familyInfo?.let {
                BackupFamilyInfo(
                    fatherName = it.fatherName,
                    motherName = it.motherName,
                    familyPhotoPath = it.familyPhotoPath,
                    familyQuote = it.familyQuote
                )
            },
            familyMembers = familyMembers.map {
                BackupFamilyMember(
                    name = it.name,
                    relation = it.relation,
                    phone = it.phone,
                    notes = it.notes,
                    photoPath = it.photoPath
                )
            },
            education = education.map {
                BackupEducation(
                    institutionName = it.institutionName,
                    level = it.level,
                    fieldOfStudy = it.fieldOfStudy,
                    qualification = it.qualification,
                    startYear = it.startYear,
                    endYear = it.endYear,
                    gradeOrScore = it.gradeOrScore,
                    isCurrent = it.isCurrent
                )
            },
            memories = memories.map {
                BackupMemory(
                    title = it.title,
                    description = it.description,
                    memoryDate = it.memoryDate,
                    location = it.location,
                    mediaPath = it.mediaPath,
                    mediaType = it.mediaType
                )
            },
            dreamsGoals = dreamsGoals.map {
                BackupDreamGoal(
                    title = it.title,
                    description = it.description,
                    category = it.category,
                    targetDate = it.targetDate,
                    status = it.status,
                    progressPercent = it.progressPercent
                )
            },
            contactLinks = contactLinks.map {
                BackupContactLink(
                    label = it.label,
                    platform = it.platform,
                    value = it.value,
                    isPrimary = it.isPrimary
                )
            }
        )
        adapter.toJson(payload)
    }

    suspend fun writeBackupToUri(uri: Uri) = withContext(Dispatchers.IO) {
        val json = exportJsonString()
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(json.toByteArray(Charsets.UTF_8))
            stream.flush()
        } ?: throw IllegalStateException("Unable to open backup destination")
    }

    suspend fun restoreBackupFromUri(uri: Uri) = withContext(Dispatchers.IO) {
        val json = context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use {
            it.readText()
        } ?: throw IllegalStateException("Unable to read backup file")

        val payload = adapter.fromJson(json)
            ?: throw IllegalArgumentException("Invalid backup format")

        dao.replaceAllData(
            profile = payload.personalProfile?.let {
                PersonalProfileEntity(
                    id = 1,
                    fullName = it.fullName,
                    tagline = it.tagline,
                    photoPath = it.photoPath,
                    dateOfBirth = it.dateOfBirth,
                    address = it.address,
                    mobile = it.mobile,
                    email = it.email
                )
            },
            aboutMe = payload.aboutMe?.let {
                AboutMeEntity(
                    id = 1,
                    bioSummary = it.bioSummary,
                    likes = it.likes,
                    interests = it.interests,
                    hobbies = it.hobbies,
                    lifeGoal = it.lifeGoal
                )
            },
            achievements = payload.achievements.map {
                AchievementEntity(
                    title = it.title,
                    year = it.year,
                    category = it.category,
                    description = it.description
                )
            },
            familyInfo = payload.familyInfo?.let {
                FamilyInfoEntity(
                    id = 1,
                    fatherName = it.fatherName,
                    motherName = it.motherName,
                    familyPhotoPath = it.familyPhotoPath,
                    familyQuote = it.familyQuote
                )
            },
            familyMembers = payload.familyMembers.map {
                FamilyMemberEntity(
                    name = it.name,
                    relation = it.relation,
                    phone = it.phone,
                    notes = it.notes,
                    photoPath = it.photoPath
                )
            },
            education = payload.education.map {
                EducationEntity(
                    institutionName = it.institutionName,
                    level = it.level,
                    fieldOfStudy = it.fieldOfStudy,
                    qualification = it.qualification,
                    startYear = it.startYear,
                    endYear = it.endYear,
                    gradeOrScore = it.gradeOrScore,
                    isCurrent = it.isCurrent
                )
            },
            memories = payload.memories.map {
                MemoryEntity(
                    title = it.title,
                    description = it.description,
                    memoryDate = it.memoryDate,
                    location = it.location,
                    mediaPath = it.mediaPath,
                    mediaType = it.mediaType
                )
            },
            dreamsGoals = payload.dreamsGoals.map {
                DreamGoalEntity(
                    title = it.title,
                    description = it.description,
                    category = it.category,
                    targetDate = it.targetDate,
                    status = it.status,
                    progressPercent = it.progressPercent.coerceIn(0, 100)
                )
            },
            contactLinks = payload.contactLinks.map {
                ContactLinkEntity(
                    label = it.label,
                    platform = it.platform,
                    value = it.value,
                    isPrimary = it.isPrimary
                )
            }
        )
    }
}
