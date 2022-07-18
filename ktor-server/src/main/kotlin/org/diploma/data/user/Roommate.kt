package org.diploma.data.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.diploma.data.matches.Match
import org.diploma.data.responses.RoommateProfile
import org.diploma.data.responses.UserProfile

@Serializable
data class Roommate(
    @Contextual
    @SerialName("_id")
    val id: ObjectId = ObjectId(),
    val username: String,
    val password: String,
    val salt: String,
    val name: String,
    val photos: List<String> = listOf(),
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
    val interestedIn: List<Int> = listOf(),
    @SerialName("age_filter_max")
    val ageFilterMax: Int,
    @SerialName("age_filter_min")
    val ageFilterMin: Int,
    val bio: String,
    val job: Job,
    val school: School,
    @SerialName("user_interests")
    val interests: List<Interests> = listOf(),
    val matches: List<Match> = listOf(),
    @SerialName("likes")
    val likes: List<String> = listOf(),
    @SerialName("recently_pass")
    val passes: List<String> = listOf(),
    @SerialName("last_activity_date")
    val lastActivityDate: String
) {
    fun toRoommateProfile(): RoommateProfile {
        return RoommateProfile(
            id = id.toString(),
            name = name,
            photos = photos,
            age = age,
            gender = gender,
            budget = budget,
            city = city,
            hasRoom = hasRoom,
            job = job,
            bio = bio,
            school = school,
            interests = interests,
            lastActivityDate = lastActivityDate
        )
    }

    fun toUserProfile(): UserProfile {
        return UserProfile(
            id = id.toString(),
            name = name,
            photos = photos,
            age = age,
            gender = gender,
            budget = budget,
            minBudget = minBudget,
            maxBudget = maxBudget,
            city = city,
            hasRoom = hasRoom,
            interestedIn = interestedIn,
            ageFilterMax = ageFilterMax,
            ageFilterMin = ageFilterMin,
            bio = bio,
            job = job,
            school = school,
            interests = interests,
        )
    }
}