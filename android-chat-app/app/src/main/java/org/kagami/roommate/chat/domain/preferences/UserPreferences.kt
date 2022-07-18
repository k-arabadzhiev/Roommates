package org.kagami.roommate.chat.domain.preferences

interface UserPreferences {

    suspend fun getClientID(): String
    suspend fun setUserId(userId: String)
    suspend fun updateJWT(token: String)
    suspend fun getUserId(): String
    suspend fun getJwt(): String
    suspend fun getName(): String
    suspend fun getProfilePhoto(): String
    suspend fun updateName(name: String)
    suspend fun updatePhoto(photo: String)
}