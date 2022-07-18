package org.kagami.roommate.chat.domain.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
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
    val userInterests: List<InterestsDto>,
    @SerialName("last_activity_date")
    val lastActivityDate: String
) : Parcelable
