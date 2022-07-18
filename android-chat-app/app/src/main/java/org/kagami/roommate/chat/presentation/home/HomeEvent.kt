package org.kagami.roommate.chat.presentation.home

sealed class HomeEvent {
    data class Reload(val index: Int) : HomeEvent()

    data class LeftSwipe(val id: String) : HomeEvent()
    data class RightSwipe(val id: String, val name: String) : HomeEvent()
}
