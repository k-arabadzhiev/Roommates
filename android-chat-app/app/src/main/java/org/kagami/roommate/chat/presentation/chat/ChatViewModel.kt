package org.kagami.roommate.chat.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.data.remote.ws.ChatSocketService
import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage
import org.kagami.roommate.chat.data.remote.ws.models.messages.PhotoMessage
import org.kagami.roommate.chat.data.remote.ws.models.messages.TextMessage
import org.kagami.roommate.chat.domain.model.repository.MatchRepository
import org.kagami.roommate.chat.domain.preferences.UserPreferences
import org.kagami.roommate.chat.presentation.navArgs
import org.kagami.roommate.chat.util.ApiResult
import org.kagami.roommate.chat.util.UiEvent
import org.kagami.roommate.chat.util.UiText
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val datastore: UserPreferences,
    private val matchRepository: MatchRepository,
    private val chatSocketService: ChatSocketService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val navArgs: ChatNavArgs = savedStateHandle.navArgs()

    var state by mutableStateOf(ChatState())
        private set

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.disconnect()
        }
    }

    fun observeMessages() {
        viewModelScope.launch {
            val token = datastore.getJwt()
            val id = datastore.getUserId()
            when (chatSocketService.initSession(token, navArgs.matchId, id)) {
                is ApiResult.Success -> {
                    chatSocketService.observeMessages()
                        .onEach { message ->
                            addNewMessageToList(message)
                        }.launchIn(viewModelScope)
                }
                is ApiResult.Error -> {
                    _uiEvent.send(
                        UiEvent.ShowMessage(
                            UiText.StringResource(R.string.error_init_con)
                        )
                    )
                }
            }
        }
    }

    private fun addNewMessageToList(message: ChatMessage) {
        val newList = state.messages.toMutableList().apply {
            add(0, message)
        }
        state = state.copy(
            messages = newList
        )
    }

    fun sendMessage() {
        if (messageText.value.isNotEmpty()) {
            viewModelScope.launch {
                val id = datastore.getUserId()
                val url = messageText.value
                val isImage = if (url.startsWith("http")) {
                    chatSocketService.checkForImage(url)
                } else false
                val chatMessage = if (isImage) {
                    PhotoMessage(
                        id = ObjectId().toHexString(),
                        timestamp = Instant.now().toEpochMilli(),
                        to = navArgs.participantId,
                        from = id,
                        url = url
                    )
                } else {
                    TextMessage(
                        id = ObjectId().toHexString(),
                        timestamp = Instant.now().toEpochMilli(),
                        to = navArgs.participantId,
                        from = id,
                        content = messageText.value.trim()
                    )
                }
                addNewMessageToList(chatMessage)
                _messageText.value = ""
                chatSocketService.sendMessage(chatMessage)
            }
        }
    }

    fun loadMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = datastore.getJwt()
            val messages = matchRepository.getMatchMessages(token, navArgs.matchId)
            state = state.copy(
                isLoading = false,
                messages = messages
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}
