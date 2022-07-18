package org.kagami.roommate.chat.domain.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class InterestsDto(
    val id: Int,
    @SerialName("name")
    val interestName: String
) : Parcelable {
    fun toInterest(): Interest {
        return Interest(
            id = id,
            name = interestName
        )
    }
}