package com.example.appfirst.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 👇 crea el DataStore
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object UserPrefs {
    private val KEY_ONBOARD_DONE = booleanPreferencesKey("onboard_done")
    private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    private val KEY_USER_ID = longPreferencesKey("user_id") // 👈 clave para guardar el id del usuario

    // ✅ Onboarding
    suspend fun getOnboardDone(context: Context): Boolean {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_ONBOARD_DONE] ?: false
        }.first()
    }

    suspend fun setOnboardDone(context: Context, done: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARD_DONE] = done }
    }

    // ✅ Login
    suspend fun setLoggedIn(
        context: Context,
        isLoggedIn: Boolean,
        email: String = "",
        userId: Long? = null
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = isLoggedIn
            if (isLoggedIn && email.isNotBlank()) {
                prefs[KEY_USER_EMAIL] = email
                userId?.let { prefs[KEY_USER_ID] = it } // 👈 guardar id solo si viene
            } else if (!isLoggedIn) {
                prefs.remove(KEY_USER_EMAIL)
                prefs.remove(KEY_USER_ID)
            }
        }
    }

    suspend fun isLoggedIn(context: Context): Boolean {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_IS_LOGGED_IN] ?: false
        }.first()
    }

    suspend fun getLoggedUserEmail(context: Context): String {
        return try {
            context.dataStore.data.map { prefs ->
                prefs[KEY_USER_EMAIL] ?: ""
            }.first()
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun getLoggedUserId(context: Context): Long? {
        return try {
            context.dataStore.data.map { prefs ->
                prefs[KEY_USER_ID]
            }.first()
        } catch (e: Exception) {
            null
        }
    }

    // ✅ Flow para observar onboarding en tiempo real
    fun onboardDoneFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_ONBOARD_DONE] ?: false }
}
