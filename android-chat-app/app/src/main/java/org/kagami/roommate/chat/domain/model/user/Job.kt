package org.kagami.roommate.chat.domain.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Job(
    val company: Company,
    val title: Title
): Parcelable