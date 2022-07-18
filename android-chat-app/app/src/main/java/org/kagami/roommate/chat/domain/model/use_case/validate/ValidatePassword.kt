package org.kagami.roommate.chat.domain.model.use_case.validate

import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.util.Constants.MIN_PASSWORD
import org.kagami.roommate.chat.util.UiText

class ValidatePassword {

    operator fun invoke(password: String): ValidationResult {
        if (password.length < MIN_PASSWORD) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.minimum_password, MIN_PASSWORD)
            )
        }
        val regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}\$".toRegex()
        return if (regex.matches(password)) {
            ValidationResult(
                successful = true,
            )
        } else {
            ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.invalid_pass_error)
            )
        }
    }
}