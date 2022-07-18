package org.kagami.roommate.chat.data.remote.api

import org.kagami.roommate.chat.data.local.UserDatabase
import org.kagami.roommate.chat.data.preferences.DataStorePreferences
import org.kagami.roommate.chat.data.remote.dto.BasicResponse
import org.kagami.roommate.chat.data.remote.dto.user.UpdateUserRequest
import org.kagami.roommate.chat.domain.model.repository.ProfileRepository
import org.kagami.roommate.chat.domain.model.user.InterestsDto
import org.kagami.roommate.chat.domain.model.user.RoommateProfile
import org.kagami.roommate.chat.domain.model.user.UserProfile
import org.kagami.roommate.chat.util.ApiResult
import org.kagami.roommate.chat.util.Constants.ITEMS_PER_PAGE
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val db: UserDatabase,
    private val dataStore: DataStorePreferences
) : ProfileRepository {

    override suspend fun updateAccount(
        id: String,
        userdata: UpdateUserRequest,
        clientId: String,
        token: String,
        userPhotos: List<ByteArray>
    ): ApiResult<BasicResponse> {
        return profileApi.updateAccount(id, userdata, clientId, token, userPhotos)
    }

    override suspend fun getInterestsList(): ApiResult<List<InterestsDto>> {
        return profileApi.getInterestsList()
    }

    override suspend fun silentRelog(
        token: String,
        clientId: String,
        userId: String
    ): ApiResult<BasicResponse> {
        return profileApi.silentRelog(token, clientId, userId)
    }

    override suspend fun getSuggestions(page: Int): ApiResult<List<RoommateProfile>> {
        val token = dataStore.getJwt()
        return profileApi.getSuggestions(
            token = token,
            page = page,
            limit = ITEMS_PER_PAGE
        )
    }

    override suspend fun swipeLeft(id: String) {
        val token = dataStore.getJwt()
        val clientId = dataStore.getClientID()
        profileApi.swipeLeft(id, token, clientId)
    }

    override suspend fun swipeRight(id: String): Boolean {
        val token = dataStore.getJwt()
        val clientId = dataStore.getClientID()
        return profileApi.swipeRight(id, token, clientId)
    }

    override suspend fun getUser(
        token: String,
        id: String,
        clientId: String
    ): ApiResult<UserProfile> {
        return profileApi.getUser(token, id, clientId)
    }

    override suspend fun getUser(): UserProfile {
        return db.userDao().getProfile()
    }

    override suspend fun setUser(user: UserProfile) {
        return db.userDao().insertUserProfile(user)
    }
}