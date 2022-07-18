package org.kagami.roommate.chat.domain.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class School(
    @SerialName("visible")
    val schoolVisible: Boolean,
    @SerialName("name")
    val schoolName: String
) : Parcelable