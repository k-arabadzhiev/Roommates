package org.diploma.data.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.diploma.data.user.City
import org.diploma.data.user.Interests
import org.diploma.data.user.Job
import org.diploma.data.user.School

@Serializable
data class RoommateProfile(
    val id: String,
    val name: String,
    val photos: List<String>,
    val age: Int,
    val gender: Int,
    @SerialName("display_budget")
    val budget: Int,
    val city: City,
    @SerialName("has_room")
    val hasRoom: Boolean,
    val bio: String,
    val job: Job,
    val school: School,
    @SerialName("user_interests")
    val interests: List<Interests>,
    @SerialName("last_activity_date")
    val lastActivityDate: String
)
