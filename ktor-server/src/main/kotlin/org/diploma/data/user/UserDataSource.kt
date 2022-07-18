package org.diploma.data.user

import org.bson.types.ObjectId
import org.diploma.data.matches.Match
import org.diploma.data.responses.MatchCheckResponse
import org.diploma.data.responses.RoommateProfile

interface UserDataSource {
    suspend fun getUserByUsername(username: String): Roommate?
    suspend fun insertUser(user: Roommate): Boolean
    suspend fun insertNewUser(user: Roommate): String
    suspend fun updateUserProfile(user: Roommate): Boolean
    suspend fun checkIfUsernameExists(username: String): Boolean
    suspend fun findUserById(id: ObjectId): Roommate?
    suspend fun findProfileById(id: ObjectId): RoommateProfile?
    suspend fun getSuggestions(
        user: Roommate,
        page: Int,
        limit: Int
    ): List<RoommateProfile>

    suspend fun isMatch(userId: String, roommateId: String): MatchCheckResponse
    suspend fun addToLikes(userId: String, roommateId: String): Boolean
    suspend fun addToPasses(userId: String, roommateId: String)
    suspend fun updateMatches(userId: String, roommateId: String, match: Match)
}