package br.com.brunocheles.mycontab.model.di

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.brunocheles.mycontab.model.items.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton


data class UserDate(val year: Int, val month: Int)

@Singleton
class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id_key")
        private val USERNAME_KEY = stringPreferencesKey("username_key")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email_key")
        private val USER_CONFIG_KEY = stringPreferencesKey("user_config_key")
        private val USER_PHOTO_URL_KEY = stringPreferencesKey("user_photo_url_key")
        private val USER_PROVIDER_KEY = stringPreferencesKey("user_provider_key")

        // GLOBAIS
        private val YEAR_KEY = intPreferencesKey("year_key")
        private val MONTH_KEY = intPreferencesKey("month_key")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_FIRST_START = booleanPreferencesKey("is_first_start")
    }

    val savedDate: Flow<UserDate> = dataStore.data.map { preferences ->
        val today = LocalDate.now()
        val year = preferences[YEAR_KEY] ?: today.year
        val month = preferences[MONTH_KEY] ?: (today.monthValue - 1)

        UserDate(year, month)
    }

    val user: Flow<User?> = dataStore.data
        .catch { e ->
            if (e is IOException) {
                Log.e("DataStore", "Erro ao ler preferências.", e)
                emit(emptyPreferences())
                dataStore.edit { it.clear() }
            } else throw e
        }
        .map { prefs ->
        val isLogged = prefs[IS_LOGGED_IN] ?: false
        if (isLogged) {
            User(
                userId = prefs[USER_ID_KEY],
                username = prefs[USERNAME_KEY],
                email = prefs[USER_EMAIL_KEY],
                userConfig = prefs[USER_CONFIG_KEY],
                photoUrl = prefs[USER_PHOTO_URL_KEY],
                provider = prefs[USER_PROVIDER_KEY]
            )
        } else null
    }

    suspend fun forceClearDataStore() {
        dataStore.edit { it.clear() }
        Log.w("DataStore", "⚠️ DataStore foi limpo manualmente (forceClearDataStore)")
    }

    val isFirstStart: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_FIRST_START] ?: true
    }

    val dateFlow: Flow<Pair<Int, Int>> = dataStore.data.map { preferences ->
        val year = preferences[YEAR_KEY] ?: LocalDate.now().year
        val month = preferences[MONTH_KEY] ?: (LocalDate.now().monthValue - 1)
        year to month
    }

    // --- SAVE USER SESSION ---
    suspend fun saveUserSession(user: User) {
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = user.userId ?: ""
            prefs[USERNAME_KEY] = user.username ?: ""
            prefs[USER_EMAIL_KEY] = user.email ?: ""
            prefs[USER_CONFIG_KEY] = user.userConfig ?: ""
            prefs[USER_PHOTO_URL_KEY] = user.photoUrl ?: ""
            prefs[USER_PROVIDER_KEY] = user.provider ?: ""
            prefs[IS_LOGGED_IN] = true
        }
    }

    // --- CLEAR SESSION ---
    suspend fun logout() {
        dataStore.edit { prefs ->
            val wasFirstStart = prefs[IS_FIRST_START] ?: false
            prefs.clear()
            prefs[IS_FIRST_START] = wasFirstStart
        }
    }

    // --- DATE PERSISTENCE (mantida) ---
    suspend fun saveDate(year: Int, month: Int) {
        dataStore.edit { prefs ->
            prefs[YEAR_KEY] = year
            prefs[MONTH_KEY] = month
        }
    }

    // --- FIRST START FLAG ---
    suspend fun setFirstStart(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_FIRST_START] = value
        }
    }

    suspend fun getUserIdOnce(): String? {
        return dataStore.data.first()[USER_ID_KEY]
    }
}