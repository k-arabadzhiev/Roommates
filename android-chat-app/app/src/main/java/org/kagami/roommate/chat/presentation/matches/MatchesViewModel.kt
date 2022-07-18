package org.kagami.roommate.chat.presentation.matches

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.kagami.roommate.chat.domain.model.repository.MatchRepository
import org.kagami.roommate.chat.domain.preferences.UserPreferences
import org.kagami.roommate.chat.util.UiEvent
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val datastore: UserPreferences,
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var state by mutableStateOf(MatchState())
        private set

    fun loadList() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = datastore.getJwt()
            val matches = matchRepository.fetchMatches(token)
            state = state.copy(
                isLoading = false,
                matches = matches
            )
        }
    }
}