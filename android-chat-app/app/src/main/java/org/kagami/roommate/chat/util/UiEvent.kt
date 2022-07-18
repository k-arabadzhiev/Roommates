package org.kagami.roommate.chat.util

sealed class UiEvent {
    object Success: UiEvent()
    object NavigateUp : UiEvent()
    data class ShowMessage(val message: UiText) : UiEvent()
}