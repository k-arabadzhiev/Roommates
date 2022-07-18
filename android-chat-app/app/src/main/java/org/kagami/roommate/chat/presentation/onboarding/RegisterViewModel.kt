package org.kagami.roommate.chat.presentation.onboarding

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.domain.model.auth.AuthService
import org.kagami.roommate.chat.data.remote.dto.auth.LoginRequest
import org.kagami.roommate.chat.data.remote.dto.auth.NewUserRequest
import org.kagami.roommate.chat.data.remote.dto.user.UpdateUserRequest
import org.kagami.roommate.chat.domain.model.repository.ProfileRepository
import org.kagami.roommate.chat.domain.model.use_case.profile.GetInterestsList
import org.kagami.roommate.chat.domain.preferences.UserPreferences
import org.kagami.roommate.chat.domain.model.use_case.validate.ValidationUseCases
import org.kagami.roommate.chat.domain.model.user.*
import org.kagami.roommate.chat.util.*
import org.kagami.roommate.chat.util.Constants.BASE_URL
import org.kagami.roommate.chat.util.Constants.MAX_BIO
import org.kagami.roommate.chat.util.Constants.MAX_INTERESTS
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authService: AuthService,
    private val validate: ValidationUseCases,
    private val datastore: UserPreferences,
    private val interests: GetInterestsList,
    private val repository: ProfileRepository
) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var state by mutableStateOf(RegistrationFormState())
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onEvent(event: RegistrationFormEvent) {
        when (event) {
            is RegistrationFormEvent.UsernameChanged -> {
                state = state.copy(username = event.username)
            }
            is RegistrationFormEvent.PasswordChanged -> {
                state = state.copy(password = event.password)
            }
            is RegistrationFormEvent.NameChanged -> {
                state = state.copy(name = event.name)
            }
            is RegistrationFormEvent.AgeChanged -> {
                val age = event.age
                state = if (age.length < 3) {
                    state.copy(age = age.toDigits())
                } else {
                    state.copy(age = age.substring(0, 1).toDigits())
                }
            }
            is RegistrationFormEvent.GenderChanged -> {
                val gender = if (state.gender == Gender.Male) {
                    Gender.Female
                } else Gender.Male
                state = state.copy(gender = gender)
            }
            is RegistrationFormEvent.Register -> {
                onRegisterClick()
            }
            is RegistrationFormEvent.HasRoomChanged -> {
                state = state.copy(hasRoom = !state.hasRoom)
            }
            is RegistrationFormEvent.CityChanged -> {
                state = state.copy(city = event.city)
            }
            is RegistrationFormEvent.Next -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Success)
                }
            }
            is RegistrationFormEvent.CityNext -> {
                viewModelScope.launch {
                    if (state.city.cityName.isEmpty() || state.city.nbh.isEmpty()) {
                        _uiEvent.send(
                            UiEvent.ShowMessage(
                                UiText.StringResource(R.string.empty_fields)
                            )
                        )
                    } else {
                        _uiEvent.send(UiEvent.Success)
                    }
                }
            }
            is RegistrationFormEvent.BudgetChanged -> {
                val min = event.minBudget
                val max = event.maxBudget
                val budget = min + ((max - min) / 2)
                state = state.copy(
                    budget = budget,
                    minBudgetRange = min,
                    maxBudgetRange = max
                )
            }
            is RegistrationFormEvent.AgeRange -> {
                state = state.copy(
                    ageFilterMin = event.minAge,
                    ageFilterMax = event.maxAge
                )
            }
            is RegistrationFormEvent.InterestedInChanged -> {
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
            is RegistrationFormEvent.InterestChecked -> {
                val interestsList = state.interests.mapIndexed { index, interest ->
                    if (event.id == index) interest.copy(checked = event.checked)
                    else interest
                }
                val checkedCount = interestsList.count {
                    it.checked
                }
                if (checkedCount <= MAX_INTERESTS) {
                    state = state.copy(interests = interestsList)
                } else {
                    viewModelScope.launch {
                        _uiEvent.send(
                            UiEvent.ShowMessage(
                                UiText.StringResource(R.string.allowed_num_interests, MAX_INTERESTS)
                            )
                        )
                    }
                }
            }
            is RegistrationFormEvent.BioChanged -> {
                val characters = event.text
                if (characters.length <= MAX_BIO) {
                    state = state.copy(
                        bio = event.text,
                        bioCharacters = characters.length
                    )
                } else {
                    viewModelScope.launch {
                        _uiEvent.send(
                            UiEvent.ShowMessage(
                                UiText.StringResource(R.string.allowed_max_bio, MAX_BIO)
                            )
                        )
                    }
                }
            }
            is RegistrationFormEvent.PhotoChange -> {
                state = state.copy(
                    photoList = event.photos
                )
            }
            is RegistrationFormEvent.ProfileUpdate -> {
                updateUserProfile()
            }
        }
    }

    private fun updateUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val clientId = datastore.getClientID()
            val id = datastore.getUserId()
            val jwt = datastore.getJwt()
            if (id.isEmpty() || clientId.isEmpty() || jwt.isEmpty()) {
                _uiEvent.send(
                    UiEvent.ShowMessage(
                        UiText.StringResource(R.string.something_went_wrong)
                    )
                )
                return@launch
            }
            val updatedUser = UpdateUserRequest(
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
                interests = state.interests.filter {
                    it.checked
                }.map {
                    it.toInterestsDto()
                }
            )
            when (repository.updateAccount(
                id,
                updatedUser,
                clientId,
                jwt,
                state.photoList
            )) {
                is ApiResult.Success -> {
                    when (val userResult = repository.getUser(jwt, id, clientId)) {
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
                                _isLoading.value = false
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
                    _uiEvent.send(UiEvent.Success)
                }
                is ApiResult.Error -> {
                    _uiEvent.send(
                        UiEvent.ShowMessage(
                            UiText.StringResource(R.string.something_went_wrong)
                        )
                    )
                    return@launch
                }
            }
        }
    }

    fun getInterestsList() {
        viewModelScope.launch {
            val result = interests.invoke()
            if (result is ApiResult.Success) {
                val interestsList = result.data!!.map {
                    it.toInterest()
                }
                state = state.copy(interests = interestsList)
            } else {
                _uiEvent.send(
                    UiEvent.ShowMessage(
                        UiText.StringResource(R.string.server_error)
                    )
                )
            }
        }
    }

    private fun onRegisterClick() {
        viewModelScope.launch {
            state = validate(state)
            val newUser = NewUserRequest(
                username = state.username,
                password = state.password,
                name = state.name,
                age = state.age.toInt(),
                gender = state.gender.ordinal
            )
            when (val result = authService.createNewAccount(newUser)) {
                is ApiResult.Success -> {
                    val id = result.data?.message!!
                    if (id.isEmpty()) {
                        _uiEvent.send(
                            UiEvent.ShowMessage(
                                UiText.StringResource(R.string.no_id_response)
                            )
                        )
                        return@launch
                    }
                    datastore.setUserId(id)
                    val clientId = datastore.getClientID()
                    val request = LoginRequest(newUser.username, newUser.password)
                    when (val loginResult = authService.login(request, clientId)) {
                        is ApiResult.Success -> {
                            val token = loginResult.data?.token!!
                            val userId = loginResult.data.message!!
                            datastore.updateJWT(token)
                            datastore.setUserId(userId)
                            _uiEvent.send(UiEvent.Success)
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
                is ApiResult.Error -> {
                    _uiEvent.send(
                        UiEvent.ShowMessage(
                            UiText.StringResource(R.string.error_signup)
                        )
                    )
                    return@launch
                }
            }
        }
    }

}