package org.diploma.data.user

import kotlinx.serialization.Serializable

@Serializable
data class Title(
    val visible: Boolean,
    val name: String
)