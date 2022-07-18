package org.diploma.data.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.diploma.data.user.*

@Serializable
data class UserProfile(
    @SerialName("_id")
    val id: String,
    val name: String,
    val photos: List<String>,
    val age: Int,
    val gender: Int,
    @SerialName("display_budget")
    val budget: Int,
    @SerialName("min_budget")
    val minBudget: Int,
    @SerialName("max_budget")
    val maxBudget: Int,
    val city: City,
    @SerialName("has_room")
    val hasRoom: Boolean,
    @SerialName("interested_in")
    val interestedIn: List<Int>,
    @SerialName("age_filter_max")
    val ageFilterMax: Int,
    @SerialName("age_filter_min")
    val ageFilterMin: Int,
    val bio: String,
    val job: Job,
    val school: School,
    @SerialName("user_interests")
    val interests: List<Interests>,
)
