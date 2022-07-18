package org.kagami.roommate.chat.domain.model.repository

import org.kagami.roommate.chat.data.remote.dto.BasicResponse
import org.kagami.roommate.chat.data.remote.dto.user.UpdateUserRequest
import org.kagami.roommate.chat.domain.model.user.InterestsDto
import org.kagami.roommate.chat.domain.model.user.RoommateProfile
import org.kagami.roommate.chat.domain.model.user.UserProfile
import org.kagami.roommate.chat.util.ApiResult

interface ProfileRepository {

    suspend fun getInterestsList(): ApiResult<List<InterestsDto>>
    suspend fun updateAccount(
        id: String,
        userdata: UpdateUserRequest,
        clientId: String,
        token: String,
        userPhotos: List<ByteArray>
    ): ApiResult<BasicResponse>

    suspend fun silentRelog(
        token: String,
        clientId: String,
        userId: String
    ): ApiResult<BasicResponse>

    suspend fun getSuggestions(page: Int): ApiResult<List<RoommateProfile>>
    suspend fun swipeLeft(id: String)
    suspend fun swipeRight(id: String): Boolean
    suspend fun getUser(token: String, id: String, clientId: String): ApiResult<UserProfile>
    suspend fun getUser(): UserProfile
    suspend fun setUser(user: UserProfile)
}