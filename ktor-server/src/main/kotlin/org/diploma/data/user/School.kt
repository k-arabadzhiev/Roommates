package org.diploma.data.user

import kotlinx.serialization.Serializable

@Serializable
data class School(
    val visible: Boolean,
    val name: String
)