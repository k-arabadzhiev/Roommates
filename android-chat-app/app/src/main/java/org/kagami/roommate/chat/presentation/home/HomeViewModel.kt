package org.kagami.roommate.chat.presentation.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.dataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.disk.DiskCache
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.data.paging.SuggestionsPaginator
import org.kagami.roommate.chat.domain.model.repository.ProfileRepository
import org.kagami.roommate.chat.domain.preferences.UserPreferences
import org.kagami.roommate.chat.util.ApiResult
import org.kagami.roommate.chat.util.Constants
import org.kagami.roommate.chat.util.Constants.ITEMS_PER_PAGE
import org.kagami.roommate.chat.util.Constants.NAME
import org.kagami.roommate.chat.util.Constants.PROFILE_PHOTO
import org.kagami.roommate.chat.util.Constants.UNAUTHORIZED
import org.kagami.roommate.chat.util.UiEvent
import org.kagami.roommate.chat.util.UiText
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProfileRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var state by mutableStateOf(HomeState())
        private set

    private var index = 0

    // there must be a better way for this
    var userName: String = ""
    var profilePhoto: String = ""
    var endReached = false

    private val paginator = SuggestionsPaginator(
        initialKey = state.page,
        onLoadFinished = {
            state = state.copy(isLoading = it)
        },
        onRequest = { nextPage ->
            when (val result = repository.getSuggestions(nextPage)) {
                is ApiResult.Success -> {
                    result
                }
                is ApiResult.Error -> {
                    if (result.message == UNAUTHORIZED) {
                        _uiEvent.send(
                            UiEvent.ShowMessage(
                                UiText.StringResource(R.string.unauthorized_error_text)
                            )
                        )
                        _uiEvent.send(
                            UiEvent.NavigateUp
                        )
                    } else {
                        if (state.items.isEmpty())
                            _uiEvent.send(
                                UiEvent.ShowMessage(
                                    UiText.DynamicString(
                                        result.message?.removeSurrounding("\"") ?: "Unknown Error"
                                    )
                                )
                            )
                    }
                    result
                }
            }
        },
        getNextKey = {
            state.page + 1
        },
        onError = {
            state = state.copy(error = it!!.removeSurrounding("\""))
        },
        onSuccess = { items, newKey ->
            val currentList = state.items.toMutableList()
            currentList.addAll(0, items)
            index += items.size
            val newList = currentList.subList(0, index).map { profile ->
                profile.copy(photos = profile.photos.map { rawUrl ->
                    val url = if (rawUrl.startsWith("http")) rawUrl
                    else "${Constants.BASE_URL}/$rawUrl"

                    val request = ImageRequest.Builder(context)
                        .data(url)
                        .placeholder(R.drawable.ic_placeholder)
                        .size(400, 700)
                        .build()
                    context.imageLoader.execute(request)
                    url
                })
            }
            if (newList.size < ITEMS_PER_PAGE)
                endReached = true

            state = state.copy(
                items = newList,
                page = newKey,
                endReached = items.isEmpty(),
                index = index
            )
        }
    )

    init {
        viewModelScope.launch {
            val user = repository.getUser()
            userName = user.name
            profilePhoto = user.photos[0]
        }
    }

    private fun loadSuggestions() {
        viewModelScope.launch {
            paginator.load()
        }
    }

    fun reloadSuggestions() {
        index = 0
        endReached = false
        state = state.copy(page = 1)
        loadSuggestions()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Reload -> {
                if (!endReached) {
                    index = event.index
                    loadSuggestions()
                }
            }
            is HomeEvent.LeftSwipe -> {
                viewModelScope.launch {
                    repository.swipeLeft(event.id)
                }
            }
            is HomeEvent.RightSwipe -> {
                viewModelScope.launch {
                    val result = repository.swipeRight(event.id)
                    if (result) {
                        val currentList = state.items.toMutableList()
                        currentList.removeIf {
                            it.name == event.name
                        }
                        state = state.copy(items = currentList.toList())
                        _uiEvent.send(
                            UiEvent.ShowMessage(
                                UiText.StringResource(R.string.got_matched, event.name)
                            )
                        )
                    }
                }
            }
        }
    }
}
