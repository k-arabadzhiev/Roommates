package org.kagami.roommate.chat.domain.model.use_case.validate

import org.kagami.roommate.chat.util.UiText

data class ValidationResult(
    val successful: Boolean,
    val error: UiText? = null
)
