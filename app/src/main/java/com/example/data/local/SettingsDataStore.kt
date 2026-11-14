package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.security.SecureRandom

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_life_prefs")

data class AppSettings(
    val language: String = "hi",
    val themeMode: String = ThemeMode.SYSTEM,
    val lockEnabled: Boolean = false,
    val lockType: String = LockType.PIN,
    val lockHash: String = "",
    val lockSalt: String = ""
)

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LOCK_ENABLED = booleanPreferencesKey("lock_enabled")
        val LOCK_TYPE = stringPreferencesKey("lock_type")
        val LOCK_HASH = stringPreferencesKey("lock_hash")
        val LOCK_SALT = stringPreferencesKey("lock_salt")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            language = prefs[Keys.LANGUAGE] ?: "hi",
            themeMode = prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM,
            lockEnabled = prefs[Keys.LOCK_ENABLED] ?: false,
            lockType = prefs[Keys.LOCK_TYPE] ?: LockType.PIN,
            lockHash = prefs[Keys.LOCK_HASH] ?: "",
            lockSalt = prefs[Keys.LOCK_SALT] ?: ""
        )
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    suspend fun setLockCredential(type: String, secret: String) {
        val salt = generateSalt()
        val hash = hashSecret(secret, salt)
        context.dataStore.edit {
            it[Keys.LOCK_ENABLED] = true
            it[Keys.LOCK_TYPE] = type
            it[Keys.LOCK_SALT] = salt
            it[Keys.LOCK_HASH] = hash
        }
    }

    suspend fun disableLock() {
        context.dataStore.edit {
            it[Keys.LOCK_ENABLED] = false
            it[Keys.LOCK_HASH] = ""
            it[Keys.LOCK_SALT] = ""
        }
    }

    fun verifyCredential(secret: String, settings: AppSettings): Boolean {
        if (!settings.lockEnabled || settings.lockHash.isBlank()) return true
        return hashSecret(secret, settings.lockSalt) == settings.lockHash
    }

    private fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun hashSecret(secret: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest("$salt:$secret".toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
