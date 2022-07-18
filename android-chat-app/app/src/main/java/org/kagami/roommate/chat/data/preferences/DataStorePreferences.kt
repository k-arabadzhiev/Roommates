package org.kagami.roommate.chat.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import org.kagami.roommate.chat.domain.preferences.UserPreferences
import org.kagami.roommate.chat.util.Constants.CLIENT_ID_HEADER
import org.kagami.roommate.chat.util.Constants.ID
import org.kagami.roommate.chat.util.Constants.JWT_TOKEN
import org.kagami.roommate.chat.util.Constants.NAME
import org.kagami.roommate.chat.util.Constants.PROFILE_PHOTO
import org.kagami.roommate.chat.util.Constants.USER_DATA_STORE
import java.util.*
import javax.inject.Inject

class DataStorePreferences @Inject constructor(
    @ApplicationContext val context: Context,
) : UserPreferences {

    override suspend fun getClientID(): String {
        val currentPrefs = context.dataStore.data.first()
        val clientIdExists = currentPrefs[CLIENT_ID] != null
        return if (clientIdExists) {
            currentPrefs[CLIENT_ID] ?: ""
        } else {
            val newClientId = UUID.randomUUID().toString()
            context.dataStore.edit { preferences ->
                preferences[CLIENT_ID] = newClientId
            }
            newClientId
        }
    }

    override suspend fun setUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    override suspend fun updateJWT(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    override suspend fun getUserId(): String {
        val currentPrefs = context.dataStore.data.first()
        return currentPrefs[USER_ID] ?: ""
    }

    override suspend fun getJwt(): String {
        val currentPrefs = context.dataStore.data.first()
        return currentPrefs[USER_TOKEN] ?: ""
    }

    override suspend fun getName(): String {
        val currentPrefs = context.dataStore.data.first()
        return currentPrefs[USER_NAME] ?: ""
    }

    override suspend fun getProfilePhoto(): String {
        val currentPrefs = context.dataStore.data.first()
        return currentPrefs[PHOTO] ?: ""
    }

    override suspend fun updateName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    override suspend fun updatePhoto(photo: String) {
        context.dataStore.edit { preferences ->
            preferences[PHOTO] = photo
        }
    }

    companion object {
        private val CLIENT_ID = stringPreferencesKey(CLIENT_ID_HEADER)
        private val USER_ID = stringPreferencesKey(ID)
        private val USER_TOKEN = stringPreferencesKey(JWT_TOKEN)
        private val USER_NAME = stringPreferencesKey(NAME)
        private val PHOTO = stringPreferencesKey(PROFILE_PHOTO)

        private val Context.dataStore by preferencesDataStore(
            name = USER_DATA_STORE
        )
    }
}