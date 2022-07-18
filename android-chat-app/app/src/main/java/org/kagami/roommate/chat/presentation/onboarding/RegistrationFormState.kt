package org.kagami.roommate.chat.presentation.onboarding

import org.kagami.roommate.chat.data.remote.dto.BasicResponse
import org.kagami.roommate.chat.domain.model.user.*
import org.kagami.roommate.chat.util.ApiResult
import org.kagami.roommate.chat.util.Constants.BUDGET_DEFAULT
import org.kagami.roommate.chat.util.Constants.MAX_AGE_DEFAULT
import org.kagami.roommate.chat.util.Constants.MAX_BUDGET_DEFAULT
import org.kagami.roommate.chat.util.Constants.MIN_AGE_DEFAULT
import org.kagami.roommate.chat.util.Constants.MIN_BUDGET_DEFAULT
import org.kagami.roommate.chat.util.UiText

data class RegistrationFormState(
    val username: String = "",
    val usernameError: UiText? = null,
    val password: String = "",
    val passwordError: UiText? = null,
    val name: String = "",
    val nameError: UiText? = null,
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
    val interests: List<Interest> = listOf(),
    val bio: String = "",
    val bioCharacters: Int = 0,
    val photoList: List<ByteArray> = listOf(),
    // no time to implement these
    val school: School = School(false, ""),
    val job: Job = Job(Company(false, ""), Title(false, ""))
)
