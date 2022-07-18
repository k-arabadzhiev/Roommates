package org.kagami.roommate.chat.presentation.onboarding

import org.kagami.roommate.chat.domain.model.user.City

sealed class RegistrationFormEvent {
    data class UsernameChanged(val username: String) : RegistrationFormEvent()
    data class NameChanged(val name: String) : RegistrationFormEvent()
    data class PasswordChanged(val password: String) : RegistrationFormEvent()
    data class AgeChanged(val age: String) : RegistrationFormEvent()
    object GenderChanged : RegistrationFormEvent()
    object Register : RegistrationFormEvent()
    object HasRoomChanged : RegistrationFormEvent()
    data class CityChanged(val city: City) : RegistrationFormEvent()
    data class AgeRange(val minAge: Float, val maxAge: Float) : RegistrationFormEvent()
    data class BudgetChanged(val minBudget: Float, val maxBudget: Float) : RegistrationFormEvent()
    data class InterestedInChanged(val choice: Int) : RegistrationFormEvent()
    data class InterestChecked(val id: Int, val checked: Boolean) :
        RegistrationFormEvent()

    data class BioChanged(val text: String) : RegistrationFormEvent()
    data class PhotoChange(val photos: List<ByteArray>) : RegistrationFormEvent()

    object Next : RegistrationFormEvent()
    object ProfileUpdate : RegistrationFormEvent()
    object CityNext : RegistrationFormEvent()
}
