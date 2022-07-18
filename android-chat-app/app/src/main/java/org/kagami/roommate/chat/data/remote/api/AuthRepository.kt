package org.kagami.roommate.chat.data.remote.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.internal.closeQuietly
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.data.remote.dto.BasicResponse
import org.kagami.roommate.chat.data.remote.dto.auth.LoginRequest
import org.kagami.roommate.chat.data.remote.dto.auth.LoginResponse
import org.kagami.roommate.chat.data.remote.dto.auth.NewUserRequest
import org.kagami.roommate.chat.domain.model.auth.AuthService
import org.kagami.roommate.chat.util.ApiResult
import org.kagami.roommate.chat.util.Constants.CLIENT_ID_HEADER
import org.kagami.roommate.chat.util.Constants.LOGIN_ENDPOINT
import org.kagami.roommate.chat.util.Constants.SIGN_UP_ENDPOINT
import org.kagami.roommate.chat.util.checkForInternetConnection
import java.io.*
import javax.inject.Inject

class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: HttpClient
) : AuthService {

    override suspend fun createNewAccount(userdata: NewUserRequest): ApiResult<BasicResponse> {
        if (!context.checkForInternetConnection()) {
            return ApiResult.Error(context.getString(R.string.error_internet_turned_off))
        }
        return try {
            val response = client.post(SIGN_UP_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(userdata)
            }
            val message = response.body<BasicResponse>().message
            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiResult.Success(BasicResponse(message = message))
                }
                else -> {
                    ApiResult.Error(message = message)
                }
            }
        } catch (e: ConnectTimeoutException) {
            ApiResult.Error(message = context.getString(R.string.server_error))
        }
    }

    override suspend fun login(request: LoginRequest, clientId: String): ApiResult<LoginResponse> {
        if (!context.checkForInternetConnection()) {
            return ApiResult.Error(context.getString(R.string.error_internet_turned_off))
        }
        return try {
            val response = client.post(LOGIN_ENDPOINT) {
                header(CLIENT_ID_HEADER, clientId)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            val loginResponse = response.body<LoginResponse>()
            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiResult.Success(loginResponse)
                }
                else -> {
                    ApiResult.Error(data = loginResponse, message = "")
                }
            }
        } catch (e: ConnectTimeoutException) {
            ApiResult.Error(
                data = LoginResponse(message = context.getString(R.string.server_error)),
                message = null
            )
        }
    }
}