package org.diploma.data.user

import kotlinx.serialization.Serializable

@Serializable
data class Company(
    val visible: Boolean,
    val name: String
)