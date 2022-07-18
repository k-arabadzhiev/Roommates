package org.kagami.roommate.chat.presentation.account

import org.kagami.roommate.chat.domain.model.user.*
import org.kagami.roommate.chat.util.Constants.BUDGET_DEFAULT
import org.kagami.roommate.chat.util.Constants.MAX_AGE_DEFAULT
import org.kagami.roommate.chat.util.Constants.MAX_BUDGET_DEFAULT
import org.kagami.roommate.chat.util.Constants.MIN_AGE_DEFAULT
import org.kagami.roommate.chat.util.Constants.MIN_BUDGET_DEFAULT
import org.kagami.roommate.chat.util.UiText

data class SettingsState(
    val name: String = "",
    val age: String = "20",
    val gender: Gender = Gender.Male,
    val hasRoom: Boolean = false,
    val city: City = City("", ""),
    val minBudgetRange: Float = MIN_BUDGET_DEFAULT,
    val maxBudgetRange: Float = MAX_BUDGET_DEFAULT,
    val budget: Float = BUDGET_DEFAULT,
    val ageFilterMin: Float = MIN_AGE_DEFAULT,
    val ageFilterMax: Float = MAX_AGE_DEFAULT,
    val interestedIn: List<Int> = listOf(0),
    val bio: String = "",
    val bioCharacters: Int = 0,
    val photoList: List<ByteArray> = listOf(),
    val photos: List<String> = listOf(),
    // no time
    val school: School = School(false, ""),
    val job: Job = Job(Company(false, ""), Title(false, ""))
)
