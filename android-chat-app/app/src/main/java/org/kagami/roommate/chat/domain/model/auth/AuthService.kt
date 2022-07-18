package org.kagami.roommate.chat.domain.model.auth

import org.kagami.roommate.chat.data.remote.dto.BasicResponse
import org.kagami.roommate.chat.data.remote.dto.auth.LoginRequest
import org.kagami.roommate.chat.data.remote.dto.auth.LoginResponse
import org.kagami.roommate.chat.data.remote.dto.auth.NewUserRequest
import org.kagami.roommate.chat.util.ApiResult

interface AuthService {
    suspend fun createNewAccount(userdata: NewUserRequest): ApiResult<BasicResponse>
    suspend fun login(request: LoginRequest, clientId: String): ApiResult<LoginResponse>
}