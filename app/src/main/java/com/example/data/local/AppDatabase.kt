package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PersonalProfileEntity::class,
        AboutMeEntity::class,
        AchievementEntity::class,
        FamilyInfoEntity::class,
        FamilyMemberEntity::class,
        EducationEntity::class,
        MemoryEntity::class,
        DreamGoalEntity::class,
        ContactLinkEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_life_profile_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
