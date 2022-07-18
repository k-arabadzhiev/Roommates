package org.kagami.roommate.chat.domain.model.use_case.profile

import org.kagami.roommate.chat.domain.model.repository.ProfileRepository
import org.kagami.roommate.chat.domain.model.user.InterestsDto
import org.kagami.roommate.chat.util.ApiResult
import javax.inject.Inject

class GetInterestsList @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): ApiResult<List<InterestsDto>> {
        return repository.getInterestsList()
    }
}