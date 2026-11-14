package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

object LockType {
    const val PIN = "PIN"
    const val PASSWORD = "PASSWORD"
}

object ThemeMode {
    const val SYSTEM = "SYSTEM"
    const val LIGHT = "LIGHT"
    const val DARK = "DARK"
}

object MemoryMediaType {
    const val PHOTO = "PHOTO"
    const val VIDEO = "VIDEO"
    const val NOTE = "NOTE"
}

object DreamCategory {
    const val DREAM = "DREAM"
    const val GOAL = "GOAL"
}

object GoalStatus {
    const val NOT_STARTED = "NOT_STARTED"
    const val IN_PROGRESS = "IN_PROGRESS"
    const val COMPLETED = "COMPLETED"
}

object ContactPlatform {
    const val PHONE = "PHONE"
    const val EMAIL = "EMAIL"
    const val WHATSAPP = "WHATSAPP"
    const val INSTAGRAM = "INSTAGRAM"
    const val YOUTUBE = "YOUTUBE"
    const val FACEBOOK = "FACEBOOK"
    const val TWITTER = "TWITTER"
    const val LINKEDIN = "LINKEDIN"
    const val WEBSITE = "WEBSITE"
    const val OTHER = "OTHER"
}

@Entity(tableName = "personal_profile")
data class PersonalProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "",
    val tagline: String = "",
    val photoPath: String = "",
    val dateOfBirth: String = "",
    val address: String = "",
    val mobile: String = "",
    val email: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "about_me")
data class AboutMeEntity(
    @PrimaryKey val id: Int = 1,
    val bioSummary: String = "",
    val likes: String = "",
    val interests: String = "",
    val hobbies: String = "",
    val lifeGoal: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val year: String = "",
    val category: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_info")
data class FamilyInfoEntity(
    @PrimaryKey val id: Int = 1,
    val fatherName: String = "",
    val motherName: String = "",
    val familyPhotoPath: String = "",
    val familyQuote: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relation: String,
    val phone: String = "",
    val notes: String = "",
    val photoPath: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "education")
data class EducationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val institutionName: String,
    val level: String = "",
    val fieldOfStudy: String = "",
    val qualification: String = "",
    val startYear: String = "",
    val endYear: String = "",
    val gradeOrScore: String = "",
    val isCurrent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val memoryDate: String = "",
    val location: String = "",
    val mediaPath: String = "",
    val mediaType: String = MemoryMediaType.PHOTO,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "dreams_goals")
data class DreamGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = DreamCategory.GOAL,
    val targetDate: String = "",
    val status: String = GoalStatus.IN_PROGRESS,
    val progressPercent: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "contact_links")
data class ContactLinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val platform: String = ContactPlatform.INSTAGRAM,
    val value: String,
    val isPrimary: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
