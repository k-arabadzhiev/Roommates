package org.kagami.roommate.chat.domain.model.use_case.validate

import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.util.UiText

class ValidateName {

    operator fun invoke(name: String): ValidationResult {
        if (name.isEmpty()) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.empty_name)
            )
        }
        val regex = "[^[A-Za-z]\\s]".toRegex()
        val matches = regex.findAll(name).toList().map {
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