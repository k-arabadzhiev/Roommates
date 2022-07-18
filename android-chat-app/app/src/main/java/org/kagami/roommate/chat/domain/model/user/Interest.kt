package org.kagami.roommate.chat.domain.model.user


data class Interest(
    val id: Int,
    val name: String,
    var checked: Boolean = false
){
    fun toInterestsDto(): InterestsDto {
        return InterestsDto(
            id = id,
            interestName = name
        )
    }
}