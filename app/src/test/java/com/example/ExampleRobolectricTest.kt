package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AboutMeEntity
import com.example.data.local.AppDatabase
import com.example.data.local.BackupManager
import com.example.data.local.DreamCategory
import com.example.data.local.DreamGoalEntity
import com.example.data.local.GoalStatus
import com.example.data.local.LockType
import com.example.data.local.PersonalProfileEntity
import com.example.data.local.SettingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches My Life bilingual title`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("My Life – मेरी पहचान", appName)
    }

    @Test
    fun `pin and password verification works securely`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = SettingsDataStore(context)

        store.setLockCredential(LockType.PIN, "2580")
        val settings = store.settingsFlow.first()
        assertTrue(settings.lockEnabled)
        assertTrue(store.verifyCredential("2580", settings))
        assertFalse(store.verifyCredential("1234", settings))

        store.disableLock()
        val disabled = store.settingsFlow.first()
        assertFalse(disabled.lockEnabled)
    }

    @Test
    fun `room database and json backup export work cleanly`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = db.profileDao()
            dao.upsertPersonalProfile(
                PersonalProfileEntity(
                    fullName = "Aarav Sharma",
                    mobile = "9876543210",
                    email = "aarav@example.com"
                )
            )
            dao.upsertAboutMe(
                AboutMeEntity(
                    likes = "Music, Travel",
                    hobbies = "Photography"
                )
            )
            dao.upsertDreamGoal(
                DreamGoalEntity(
                    title = "Climb Kedarkantha",
                    category = DreamCategory.DREAM,
                    status = GoalStatus.COMPLETED,
                    progressPercent = 100
                )
            )

            val backupManager = BackupManager(context, dao)
            val json = backupManager.exportJsonString()
            assertTrue(json.contains("Aarav Sharma"))
            assertTrue(json.contains("Photography"))
            assertTrue(json.contains("Climb Kedarkantha"))
        } finally {
            db.close()
        }
    }
}
