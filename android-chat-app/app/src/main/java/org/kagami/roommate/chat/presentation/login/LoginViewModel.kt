package org.kagami.roommate.chat.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.data.remote.dto.auth.LoginRequest
import org.kagami.roommate.chat.domain.model.auth.AuthService
import org.kagami.roommate.chat.domain.model.repository.ProfileRepository
import org.kagami.roommate.chat.domain.preferences.UserPreferences
import org.kagami.roommate.chat.util.ApiResult
import org.kagami.roommate.chat.util.Constants
import org.kagami.roommate.chat.util.Constants.BASE_URL
import org.kagami.roommate.chat.util.UiEvent
import org.kagami.roommate.chat.util.UiText
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
    private val datastore: UserPreferences,
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var state by mutableStateOf(LoginFormEvent())

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> {
                state = state.copy(username = event.username)
            }
            is LoginEvent.PasswordChanged -> {
                state = state.copy(password = event.password)
            }
            is LoginEvent.Login -> {
                onLoginClick()
            }
        }
    }

    private fun onLoginClick() {
        viewModelScope.launch {
            if (state.username.isEmpty()) {
                state = state.copy(
                    usernameError = UiText.StringResource(R.string.empty_username)
                )
                return@launch
            } else {
                state = state.copy(usernameError = null)
            }
            if (state.password.isEmpty()) {
                state = state.copy(
                    passwordError = UiText.StringResource(R.string.empty_password)
                )
                return@launch
            } else {
                state = state.copy(passwordError = null)
            }
            val clientId = datastore.getClientID()
            val request = LoginRequest(state.username, state.password)
            when (val loginResult = authService.login(request, clientId)) {
                is ApiResult.Success -> {
                    val token = loginResult.data?.token!!
                    val userId = loginResult.data.message!!
                    datastore.updateJWT(token)
                    datastore.setUserId(userId)
                    when (val userResult = repository.getUser(token, userId, clientId)) {
                        is ApiResult.Success -> {
                            withContext(Dispatchers.IO) {
                                val userPhotos = userResult.data!!.photos.map { url ->
                                    if (url.startsWith("http")) {
                                        url
                                    } else {
                                        "${BASE_URL}/$url"
                                    }
                                }
                                val user = userResult.data.copy(photos = userPhotos)
                                repository.setUser(user)
                                _uiEvent.send(UiEvent.Success)
                            }
                        }
                        is ApiResult.Error -> {
                            _uiEvent.send(
                                UiEvent.ShowMessage(
                                    UiText.DynamicString(userResult.message ?: "")
                                )
                            )
                            return@launch
                        }
                    }
                }
                is ApiResult.Error -> {
                    _uiEvent.send(
                        UiEvent.ShowMessage(
                            UiText.DynamicString(loginResult.data?.message!!)
                        )
                    )
                    return@launch
                }
            }
        }
    }
}