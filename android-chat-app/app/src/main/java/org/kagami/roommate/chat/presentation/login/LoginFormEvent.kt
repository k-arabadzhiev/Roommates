package org.kagami.roommate.chat.presentation.login

import org.kagami.roommate.chat.util.UiText

data class LoginFormEvent(
    val username: String = "",
    val usernameError: UiText? = null,
    val password: String = "",
    val passwordError: UiText? = null
)
