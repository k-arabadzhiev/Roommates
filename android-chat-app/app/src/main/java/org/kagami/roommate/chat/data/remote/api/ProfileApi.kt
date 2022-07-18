package org.kagami.roommate.chat.data.remote.api

import org.kagami.roommate.chat.data.remote.dto.BasicResponse
import org.kagami.roommate.chat.data.remote.dto.auth.NewUserRequest
import org.kagami.roommate.chat.data.remote.dto.user.UpdateUserRequest
import org.kagami.roommate.chat.domain.model.user.InterestsDto
import org.kagami.roommate.chat.domain.model.user.RoommateProfile
import org.kagami.roommate.chat.domain.model.user.UserProfile
import org.kagami.roommate.chat.util.ApiResult

interface ProfileApi {

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

    suspend fun getSuggestions(
        token: String,
        page: Int,
        limit: Int
    ): ApiResult<List<RoommateProfile>>

    suspend fun swipeLeft(id: String, token: String, clientId: String)
    suspend fun swipeRight(id: String, token: String, clientId: String): Boolean
    suspend fun getUser(token: String, id: String, clientId: String): ApiResult<UserProfile>
    suspend fun getProfile(token: String, id: String, clientId: String): ApiResult<RoommateProfile>

}