package org.kagami.roommate.chat.domain.model.use_case.validate

import org.kagami.roommate.chat.presentation.onboarding.RegistrationFormState

class ValidationUseCases(
    private val username: ValidateUsername,
    private val password: ValidatePassword,
    private val name: ValidateName
) {

    operator fun invoke(
        state: RegistrationFormState
    ): RegistrationFormState {
        val usernameResult = username(state.username)
        val nameResult = name(state.name)
        val passwordResult = password(state.password)

        val hasError = listOf(
            usernameResult, passwordResult, nameResult
        ).any { !it.successful }

        return if (hasError) {
            state.copy(
                usernameError = usernameResult.error,
                passwordError = passwordResult.error,
                nameError = nameResult.error,
            )
        } else {
            state
        }
    }
}