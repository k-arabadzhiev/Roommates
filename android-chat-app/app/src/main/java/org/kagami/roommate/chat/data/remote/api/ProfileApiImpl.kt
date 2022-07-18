package org.kagami.roommate.chat.data.remote.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.internal.closeQuietly
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.data.remote.dto.BasicResponse
import org.kagami.roommate.chat.data.remote.dto.auth.ReLoginRequest
import org.kagami.roommate.chat.data.remote.dto.user.MatchResultResponse
import org.kagami.roommate.chat.data.remote.dto.user.UpdateUserRequest
import org.kagami.roommate.chat.domain.model.repository.ProfileRepository
import org.kagami.roommate.chat.domain.model.user.InterestsDto
import org.kagami.roommate.chat.domain.model.user.RoommateProfile
import org.kagami.roommate.chat.domain.model.user.UserProfile
import org.kagami.roommate.chat.util.ApiResult
import org.kagami.roommate.chat.util.Constants.CLIENT_ID_HEADER
import org.kagami.roommate.chat.util.Constants.GET_INTERESTS_ENDPOINT
import org.kagami.roommate.chat.util.Constants.LIKE_ENDPOINT
import org.kagami.roommate.chat.util.Constants.PASS_ENDPOINT
import org.kagami.roommate.chat.util.Constants.PROFILE_ENDPOINT
import org.kagami.roommate.chat.util.Constants.SILENT_RELOG_ENDPOINT
import org.kagami.roommate.chat.util.Constants.SUGGESTIONS_ENDPOINT
import org.kagami.roommate.chat.util.Constants.UNAUTHORIZED
import org.kagami.roommate.chat.util.Constants.UPDATE_PROFILE_ENDPOINT
import org.kagami.roommate.chat.util.Constants.USER_ENDPOINT
import org.kagami.roommate.chat.util.checkForInternetConnection
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ProfileApiImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: HttpClient,
    private val parser: Json
) : ProfileApi {

    override suspend fun updateAccount(
        id: String,
        userdata: UpdateUserRequest,
        clientId: String,
        token: String,
        userPhotos: List<ByteArray>
    ): ApiResult<BasicResponse> {
        if (!context.checkForInternetConnection()) {
            return ApiResult.Error(context.getString(R.string.error_internet_turned_off))
        }
        return try {
            // Класът UpdateUserRequest се преобразува в json низ
            val userDataString = parser.encodeToString(userdata)
            println("token: $token, clientId: $clientId")
            // чрез submitForm се изпраща POST към /update/{id}
            val result = client.submitForm(UPDATE_PROFILE_ENDPOINT + id) {
                // тялото на заявката ще бъде MultiPartData
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            // към хедърите на всички части се добавя JWT и клиентското ид
                            headers {
                                append(HttpHeaders.Authorization, "Bearer $token")
                                append(CLIENT_ID_HEADER, clientId)
                            }
                            // към частта user data се добавя парснатия низ
                            append("user data", userDataString, Headers.build {
                                append(HttpHeaders.ContentType, ContentType.Application.Json)
                            })
                            // за всеки масив от майтова от листа userPhotos
                            userPhotos.forEachIndexed { index, bytes ->
                                // отваря се ByteArrayOutputStream
                                val outputStream = ByteArrayOutputStream()
                                // компресира се снимката, за да се изпрати по бързо
                                // както и да заема по-малко място на сървъра
                                val compressedBitmap =
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                        .compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                                // при успешна компресия
                                if (compressedBitmap) {
                                    // снимката се добавя към multipart data
                                    append(
                                        "photo ${index + 1}",
                                        outputStream.toByteArray(),
                                        Headers.build {
                                            append(HttpHeaders.ContentType, ContentType.Image.JPEG)
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "filename=\"photo ${index + 1}.jpeg\""
                                            )
                                        })
                                    outputStream.closeQuietly()
                                }
                            }
                        },
                        boundary = "boundary"
                    )
                )
                onUpload { bytesSentTotal, contentLength ->
                    // TODO should make the function to return flow and observe it
                    // so that it can display upload progress
                    println("Sent $bytesSentTotal bytes from $contentLength")
                }
            }
            if (result.status == HttpStatusCode.OK) {
                ApiResult.Success(data = result.body())
            } else {
                ApiResult.Error(message = result.body())
            }
        } catch (e: Exception) {
            ApiResult.Error(context.getString(R.string.something_went_wrong))
        }
    }

    override suspend fun getInterestsList(): ApiResult<List<InterestsDto>> {
        if (!context.checkForInternetConnection()) {
            return ApiResult.Error(context.getString(R.string.error_internet_turned_off))
        }
        return try {
            val response = client.get(GET_INTERESTS_ENDPOINT)
            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiResult.Success(response.body())
                }
                else -> {
                    val message = response.body<BasicResponse>().message
                    ApiResult.Error(message = message)
                }
            }
        } catch (e: ConnectTimeoutException) {
            ApiResult.Error(message = context.getString(R.string.server_error))
        }
    }

    override suspend fun silentRelog(
        token: String,
        clientId: String,
        userId: String
    ): ApiResult<BasicResponse> {
        if (!context.checkForInternetConnection()) {
            return ApiResult.Error(context.getString(R.string.error_internet_turned_off))
        }
        return try {
            val response = client.post(SILENT_RELOG_ENDPOINT) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(CLIENT_ID_HEADER, clientId)
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                }
                setBody(ReLoginRequest(userId))
            }
            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiResult.Success(response.body())
                }
                HttpStatusCode.Unauthorized -> {
                    ApiResult.Error(message = response.bodyAsText())
                }
                else -> {
                    val message = response.bodyAsText()
                    ApiResult.Error(message = message)
                }
            }
        } catch (e: ConnectTimeoutException) {
            ApiResult.Error(message = context.getString(R.string.server_error))
        }
    }

    override suspend fun getSuggestions(
        token: String,
        page: Int,
        limit: Int
    ): ApiResult<List<RoommateProfile>> {
        if (!context.checkForInternetConnection()) {
            return ApiResult.Error(context.getString(R.string.error_internet_turned_off))
        }
        return try {
            val response = client.get(SUGGESTIONS_ENDPOINT) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url {
                    parameters.append("page", page.toString())
                    parameters.append("limit", limit.toString())
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiResult.Success(response.body())
                }
                HttpStatusCode.Unauthorized -> {
                    ApiResult.Error(
                        data = null,
                        message = UNAUTHORIZED
                    )
                }
                else -> {
                    val message = response.bodyAsText()
                    ApiResult.Error(
                        data = null,
                        message = message
                    )
                }
            }
        } catch (e: ConnectTimeoutException) {
            ApiResult.Error(message = context.getString(R.string.server_error))
        }
    }

    override suspend fun swipeLeft(id: String, token: String, clientId: String) {
        try {
            client.get(PASS_ENDPOINT + id) {
                headers {
                    append(CLIENT_ID_HEADER, clientId)
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        } catch (e: ConnectTimeoutException) {
            Log.d("SWIPE", "swipeLeft: ${e.message}")
        }
    }

    override suspend fun swipeRight(id: String, token: String, clientId: String): Boolean {
        return try {
            val response = client.get(LIKE_ENDPOINT + id) {
                headers {
                    append(CLIENT_ID_HEADER, clientId)
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            val matchResult = response.body<MatchResultResponse>()
            return matchResult.match
        } catch (e: ConnectTimeoutException) {
            Log.d("SWIPE", "swipeRight: ${e.message}")
            false
        }
    }

    override suspend fun getUser(
        token: String,
        id: String,
        clientId: String
    ): ApiResult<UserProfile> {
        if (!context.checkForInternetConnection()) {
            return ApiResult.Error(context.getString(R.string.error_internet_turned_off))
        }
        return try {
            val response = client.get(USER_ENDPOINT + id) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiResult.Success(response.body())
                }
                else -> {
                    val message = response.bodyAsText()
                    ApiResult.Error(message = message)
                }
            }
        } catch (e: ConnectTimeoutException) {
            ApiResult.Error(message = context.getString(R.string.server_error))
        }
    }

    override suspend fun getProfile(
        token: String,
        id: String,
        clientId: String
    ): ApiResult<RoommateProfile> {
        if (!context.checkForInternetConnection()) {
            return ApiResult.Error(context.getString(R.string.error_internet_turned_off))
        }
        return try {
            val response = client.get(PROFILE_ENDPOINT + id) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiResult.Success(response.body())
                }
                else -> {
                    val message = response.bodyAsText()
                    ApiResult.Error(message = message)
                }
            }
        } catch (e: ConnectTimeoutException) {
            ApiResult.Error(message = context.getString(R.string.server_error))
        }
    }
}