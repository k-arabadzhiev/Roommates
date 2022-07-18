package org.diploma.data.user

import org.bson.types.ObjectId
import org.diploma.data.matches.Match
import org.diploma.data.responses.MatchCheckResponse
import org.diploma.data.responses.RoommateProfile
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoUserDataSource(
    db: CoroutineDatabase
) : UserDataSource {

    private val users = db.getCollection<Roommate>()

    override suspend fun getUserByUsername(username: String): Roommate? {
        return users.findOne(Roommate::username eq username)
    }

    override suspend fun insertUser(user: Roommate): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun insertNewUser(user: Roommate): String {
        return users.insertOne(user).insertedId?.asObjectId()?.value?.toHexString() ?: ""
    }

    override suspend fun checkIfUsernameExists(username: String): Boolean {
        val user = users.findOne(Roommate::username eq username)
        return user != null
    }

    override suspend fun findUserById(id: ObjectId): Roommate? {
        return users.findOneById(id)
    }

    override suspend fun getSuggestions(
        user: Roommate,
        page: Int,
        limit: Int
    ): List<RoommateProfile> {
        val likes = user.likes.map { ObjectId(it) }
        val passes = user.passes.map { ObjectId(it) }
        val matches = mutableListOf<ObjectId>()

        user.matches.forEach { match ->
            val ids = match.participants.map {
                ObjectId(it)
            }
            matches.addAll(ids)
        }
        val findQuery = users.find(
            Roommate::id ne user.id,
            Roommate::age gte user.ageFilterMin,
            Roommate::age lte user.ageFilterMax,
            Roommate::hasRoom ne user.hasRoom,
            Roommate::minBudget gte user.minBudget,
            Roommate::maxBudget lte user.maxBudget,
            Roommate::city / City::name eq user.city.name,
            Roommate::gender `in` user.interestedIn,
            Roommate::interestedIn contains user.gender,
            Roommate::id nin passes,
            Roommate::id nin likes,
            Roommate::id nin matches
        ).skip(skip = (page - 1) * limit).limit(limit).partial(true).descendingSort(Roommate::lastActivityDate)

        val query = findQuery.toList().map {
            it.toRoommateProfile()
        }
        println("suggested: $query")
        return query.ifEmpty {
            users.find(
                Roommate::id ne user.id,
                Roommate::age gte user.ageFilterMin,
                Roommate::age lte user.ageFilterMax,
                Roommate::hasRoom ne user.hasRoom,
                Roommate::minBudget gte user.minBudget,
                Roommate::maxBudget lte user.maxBudget,
                Roommate::city / City::name eq user.city.name,
                Roommate::gender `in` user.interestedIn,
                Roommate::interestedIn contains user.gender,
                Roommate::id nin matches
            ).skip(skip = (page - 1) * limit).limit(limit).partial(true).descendingSort(Roommate::lastActivityDate)
                .toList().map { it.toRoommateProfile() }
        }
    }

    override suspend fun isMatch(userId: String, roommateId: String): MatchCheckResponse {
        val query = users.find(
            Roommate::id eq ObjectId(roommateId)
        ).first()
        return MatchCheckResponse(
            query?.likes?.contains(userId) ?: false
        )
    }

    override suspend fun findProfileById(id: ObjectId): RoommateProfile? {
        return users.findOneById(id)?.toRoommateProfile()
    }

    override suspend fun addToLikes(userId: String, roommateId: String): Boolean {
        return users.updateOneById(
            id = ObjectId(userId),
            update = push(Roommate::likes, roommateId)
        ).wasAcknowledged()
    }

    override suspend fun updateMatches(userId: String, roommateId: String, match: Match) {
        users.updateMany(
            filter = or(Roommate::id eq ObjectId(userId), Roommate::id eq ObjectId(roommateId)),
            update = push(Roommate::matches, match)
        )
    }

    override suspend fun addToPasses(userId: String, roommateId: String) {
        users.updateOneById(
            id = ObjectId(userId),
            update = push(Roommate::passes, roommateId)
        )
    }

    override suspend fun updateUserProfile(user: Roommate): Boolean {
        return users.updateOneById(user.id, user).wasAcknowledged()
    }
}