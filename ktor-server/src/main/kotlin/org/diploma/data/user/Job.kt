package org.diploma.data.user

import kotlinx.serialization.Serializable

@Serializable
data class Job(
    val company: Company,
    val title: Title
)