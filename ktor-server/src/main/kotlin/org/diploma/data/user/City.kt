package org.diploma.data.user

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val name: String,
    val nbh: String
)