package org.kagami.roommate.chat.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.kagami.roommate.chat.domain.model.repository.ProfileRepository
import org.kagami.roommate.chat.domain.preferences.UserPreferences
import org.kagami.roommate.chat.presentation.destinations.DirectionDestination
import org.kagami.roommate.chat.presentation.destinations.HomeScreenDestination
import org.kagami.roommate.chat.presentation.destinations.LoginScreenDestination
import org.kagami.roommate.chat.util.ApiResult
import org.kagami.roommate.chat.util.UiEvent
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val datastore: UserPreferences,
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startRoute: MutableState<DirectionDestination> =
        mutableStateOf(LoginScreenDestination)
    val startRoute: State<DirectionDestination> = _startRoute

    init {
        viewModelScope.launch {
            val jwt = datastore.getJwt()
            println("token: $jwt")
            if (jwt.isNotEmpty()) {
                val clientId = datastore.getClientID()
                val userId = datastore.getUserId()
                when (repository.silentRelog(jwt, clientId, userId)) {
                    is ApiResult.Success -> {
                        _startRoute.value = HomeScreenDestination
                    }
                    is ApiResult.Error -> {
                        _startRoute.value = LoginScreenDestination
                    }
                }
            } else {
                _startRoute.value = LoginScreenDestination
            }
            _isLoading.value = false
        }
    }
}