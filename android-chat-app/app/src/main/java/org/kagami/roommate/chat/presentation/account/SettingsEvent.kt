package org.kagami.roommate.chat.presentation.account

import org.kagami.roommate.chat.domain.model.user.City

sealed class SettingsEvent {
    data class NameChanged(val name: String): SettingsEvent()
    data class AgeChanged(val age: String): SettingsEvent()

    object GenderChanged : SettingsEvent()
    object Update : SettingsEvent()
    object HasRoomChanged : SettingsEvent()

    data class CityChanged(val city: City) : SettingsEvent()
    data class AgeRange(val minAge: Float, val maxAge: Float) : SettingsEvent()
    data class BudgetChanged(val minBudget: Float, val maxBudget: Float) : SettingsEvent()
    data class InterestedInChanged(val choice: Int) : SettingsEvent()

    data class BioChanged(val text: String) : SettingsEvent()
    data class PhotoChange(val photos: List<ByteArray>) : SettingsEvent()
}
