package org.kagami.roommate.chat.domain.model.use_case.validate

import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.util.Constants.MIN_USERNAME
import org.kagami.roommate.chat.util.UiText

class ValidateUsername {

    operator fun invoke(username: String): ValidationResult {
        if (username.length < MIN_USERNAME) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.empty_username)
            )
        }
        val regex = "[^\\w.@-]".toRegex()
        val matches = regex.findAll(username).toList().map {
            it.value
        }
        return if (matches.isNotEmpty()) {
            val symbols = matches.distinct().toString()
                .removeSurrounding("[", "]")
                .replace(",", "  ")
            ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.error_username, symbols)
            )
        } else {
            ValidationResult(
                successful = true
            )
        }
    }
}