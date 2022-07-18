package org.kagami.roommate.chat.presentation.account

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
import org.kagami.roommate.chat.data.remote.dto.user.UpdateUserRequest
import org.kagami.roommate.chat.domain.model.repository.ProfileRepository
import org.kagami.roommate.chat.domain.model.user.Gender
import org.kagami.roommate.chat.domain.preferences.UserPreferences
import org.kagami.roommate.chat.util.*
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val dataStore: UserPreferences
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var state by mutableStateOf(SettingsState())
        private set

    init {
        viewModelScope.launch {
            val user = repository.getUser()
            state = state.copy(
                name = user.name,
                age = user.age.toString(),
                gender = if (user.gender == 0) Gender.Male else Gender.Female,
                hasRoom = user.hasRoom,
                city = user.city,
                minBudgetRange = user.minBudget.toFloat(),
                maxBudgetRange = user.maxBudget.toFloat(),
                budget = user.budget.toFloat(),
                ageFilterMin = user.ageFilterMin.toFloat(),
                ageFilterMax = user.ageFilterMax.toFloat(),
                interestedIn = user.interestedIn,
                photos = user.photos,
                bio = user.bio,
                bioCharacters = user.bio.length
            )
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.AgeRange -> {
                state = state.copy(
                    ageFilterMin = event.minAge,
                    ageFilterMax = event.maxAge
                )
            }
            is SettingsEvent.AgeChanged -> {
                val age = event.age
                state = if (age.length < 3) {
                    state.copy(age = age.toDigits())
                } else {
                    state.copy(age = age.substring(0, 1).toDigits())
                }
            }
            is SettingsEvent.BioChanged -> {
                val characters = event.text
                if (characters.length <= Constants.MAX_BIO) {
                    state = state.copy(
                        bio = event.text,
                        bioCharacters = characters.length
                    )
                } else {
                    viewModelScope.launch {
                        _uiEvent.send(
                            UiEvent.ShowMessage(
                                UiText.StringResource(R.string.allowed_max_bio, Constants.MAX_BIO)
                            )
                        )
                    }
                }
            }
            is SettingsEvent.BudgetChanged -> {
                val min = event.minBudget
                val max = event.maxBudget
                val budget = min + ((max - min) / 2)
                state = state.copy(
                    budget = budget,
                    minBudgetRange = min,
                    maxBudgetRange = max
                )
            }
            is SettingsEvent.CityChanged -> {
                state = state.copy(city = event.city)
            }
            is SettingsEvent.GenderChanged -> {
                val gender = if (state.gender == Gender.Male) {
                    Gender.Female
                } else Gender.Male
                state = state.copy(gender = gender)
            }
            is SettingsEvent.HasRoomChanged -> {
                state = state.copy(hasRoom = !state.hasRoom)
            }
            is SettingsEvent.InterestedInChanged -> {
                state = if (event.choice == 2) {
                    state.copy(
                        interestedIn = listOf(0, 1)
                    )
                } else {
                    state.copy(
                        interestedIn = listOf(event.choice)
                    )
                }
            }
            is SettingsEvent.NameChanged -> {
                state = state.copy(name = event.name)
            }
            is SettingsEvent.PhotoChange -> {
                state = state.copy(
                    photoList = event.photos,
                    photos = listOf()
                )
            }
            is SettingsEvent.Update -> {
                update()
            }
        }
    }

    private fun update() {
        viewModelScope.launch {
            val id = dataStore.getUserId()
            val clientId = dataStore.getClientID()
            val token = dataStore.getJwt()

            val userdata = UpdateUserRequest(
                name = state.name,
                age = state.age.toInt(),
                gender = state.gender.ordinal,
                budget = state.budget.toInt(),
                minBudget = state.minBudgetRange.toInt(),
                maxBudget = state.maxBudgetRange.toInt(),
                city = state.city,
                hasRoom = state.hasRoom,
                interestedIn = state.interestedIn,
                ageFilterMax = state.ageFilterMax.toInt(),
                ageFilterMin = state.ageFilterMin.toInt(),
                bio = state.bio,
                job = state.job,
                school = state.school,
                interests = listOf()
            )
            when (repository.updateAccount(
                id = id,
                userdata = userdata,
                clientId = clientId,
                token = token,
                userPhotos = state.photoList
            )) {
                is ApiResult.Success -> {
                    when (val userResult = repository.getUser(token, id, clientId)) {
                        is ApiResult.Error -> {
                            // handle sad path
                        }
                        is ApiResult.Success -> {
                            withContext(Dispatchers.IO) {
                                val userPhotos = userResult.data!!.photos.map { url ->
                                    if (url.startsWith("http")) {
                                        url
                                    } else {
                                        "${Constants.BASE_URL}/$url"
                                    }
                                }
                                val user = userResult.data.copy(photos = userPhotos)
                                repository.setUser(user)
                            }
                        }
                    }
                }
                is ApiResult.Error -> {
                    // handle sad path
                }
            }
        }
    }
}
