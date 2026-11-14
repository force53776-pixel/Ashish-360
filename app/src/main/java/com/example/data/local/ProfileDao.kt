package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    // Personal Profile
    @Query("SELECT * FROM personal_profile WHERE id = 1")
    fun observePersonalProfile(): Flow<PersonalProfileEntity?>

    @Query("SELECT * FROM personal_profile WHERE id = 1")
    suspend fun getPersonalProfileOnce(): PersonalProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPersonalProfile(profile: PersonalProfileEntity)

    @Query("DELETE FROM personal_profile")
    suspend fun clearPersonalProfile()

    // About Me
    @Query("SELECT * FROM about_me WHERE id = 1")
    fun observeAboutMe(): Flow<AboutMeEntity?>

    @Query("SELECT * FROM about_me WHERE id = 1")
    suspend fun getAboutMeOnce(): AboutMeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAboutMe(aboutMe: AboutMeEntity)

    @Query("DELETE FROM about_me")
    suspend fun clearAboutMe()

    // Achievements
    @Query("SELECT * FROM achievements ORDER BY createdAt DESC")
    fun observeAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements ORDER BY createdAt DESC")
    suspend fun getAchievementsOnce(): List<AchievementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAchievement(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(items: List<AchievementEntity>)

    @Query("DELETE FROM achievements WHERE id = :id")
    suspend fun deleteAchievement(id: Long)

    @Query("DELETE FROM achievements")
    suspend fun clearAchievements()

    // Family Info
    @Query("SELECT * FROM family_info WHERE id = 1")
    fun observeFamilyInfo(): Flow<FamilyInfoEntity?>

    @Query("SELECT * FROM family_info WHERE id = 1")
    suspend fun getFamilyInfoOnce(): FamilyInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFamilyInfo(familyInfo: FamilyInfoEntity)

    @Query("DELETE FROM family_info")
    suspend fun clearFamilyInfo()

    // Family Members
    @Query("SELECT * FROM family_members ORDER BY createdAt ASC")
    fun observeFamilyMembers(): Flow<List<FamilyMemberEntity>>

    @Query("SELECT * FROM family_members ORDER BY createdAt ASC")
    suspend fun getFamilyMembersOnce(): List<FamilyMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFamilyMember(member: FamilyMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMembers(items: List<FamilyMemberEntity>)

    @Query("DELETE FROM family_members WHERE id = :id")
    suspend fun deleteFamilyMember(id: Long)

    @Query("DELETE FROM family_members")
    suspend fun clearFamilyMembers()

    // Education
    @Query("SELECT * FROM education ORDER BY isCurrent DESC, createdAt DESC")
    fun observeEducation(): Flow<List<EducationEntity>>

    @Query("SELECT * FROM education ORDER BY isCurrent DESC, createdAt DESC")
    suspend fun getEducationOnce(): List<EducationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEducation(education: EducationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEducationList(items: List<EducationEntity>)

    @Query("DELETE FROM education WHERE id = :id")
    suspend fun deleteEducation(id: Long)

    @Query("DELETE FROM education")
    suspend fun clearEducation()

    // Memories
    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun observeMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    suspend fun getMemoriesOnce(): List<MemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMemory(memory: MemoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemories(items: List<MemoryEntity>)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("DELETE FROM memories")
    suspend fun clearMemories()

    // Dreams & Goals
    @Query("SELECT * FROM dreams_goals ORDER BY createdAt DESC")
    fun observeDreamsGoals(): Flow<List<DreamGoalEntity>>

    @Query("SELECT * FROM dreams_goals ORDER BY createdAt DESC")
    suspend fun getDreamsGoalsOnce(): List<DreamGoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDreamGoal(item: DreamGoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDreamsGoals(items: List<DreamGoalEntity>)

    @Query("DELETE FROM dreams_goals WHERE id = :id")
    suspend fun deleteDreamGoal(id: Long)

    @Query("DELETE FROM dreams_goals")
    suspend fun clearDreamsGoals()

    // Contact & Social Links
    @Query("SELECT * FROM contact_links ORDER BY isPrimary DESC, createdAt ASC")
    fun observeContactLinks(): Flow<List<ContactLinkEntity>>

    @Query("SELECT * FROM contact_links ORDER BY isPrimary DESC, createdAt ASC")
    suspend fun getContactLinksOnce(): List<ContactLinkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertContactLink(link: ContactLinkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactLinks(items: List<ContactLinkEntity>)

    @Query("DELETE FROM contact_links WHERE id = :id")
    suspend fun deleteContactLink(id: Long)

    @Query("DELETE FROM contact_links")
    suspend fun clearContactLinks()

    @Transaction
    suspend fun replaceAllData(
        profile: PersonalProfileEntity?,
        aboutMe: AboutMeEntity?,
        achievements: List<AchievementEntity>,
        familyInfo: FamilyInfoEntity?,
        familyMembers: List<FamilyMemberEntity>,
        education: List<EducationEntity>,
        memories: List<MemoryEntity>,
        dreamsGoals: List<DreamGoalEntity>,
        contactLinks: List<ContactLinkEntity>
    ) {
        clearPersonalProfile()
        clearAboutMe()
        clearAchievements()
        clearFamilyInfo()
        clearFamilyMembers()
        clearEducation()
        clearMemories()
        clearDreamsGoals()
        clearContactLinks()

        profile?.let { upsertPersonalProfile(it.copy(id = 1)) }
        aboutMe?.let { upsertAboutMe(it.copy(id = 1)) }
        if (achievements.isNotEmpty()) insertAchievements(achievements.map { it.copy(id = 0) })
        familyInfo?.let { upsertFamilyInfo(it.copy(id = 1)) }
        if (familyMembers.isNotEmpty()) insertFamilyMembers(familyMembers.map { it.copy(id = 0) })
        if (education.isNotEmpty()) insertEducationList(education.map { it.copy(id = 0) })
        if (memories.isNotEmpty()) insertMemories(memories.map { it.copy(id = 0) })
        if (dreamsGoals.isNotEmpty()) insertDreamsGoals(dreamsGoals.map { it.copy(id = 0) })
        if (contactLinks.isNotEmpty()) insertContactLinks(contactLinks.map { it.copy(id = 0) })
    }
}
