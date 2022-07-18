package org.diploma.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import org.diploma.data.requests.UpdateUserRequest
import org.diploma.data.responses.BasicResponse
import org.diploma.data.user.Interests
import org.diploma.data.user.Roommate
import org.diploma.data.user.UserDataSource
import org.diploma.data.user.UserSession
import org.diploma.util.Constants.BASE_PHOTO_URI
import org.diploma.util.getRandomString
import org.koin.core.qualifier.qualifier
import org.koin.ktor.ext.inject
import java.io.File
import java.time.Instant
import java.time.temporal.ChronoUnit

fun Route.roommateRoutes() {
    val db: UserDataSource by inject()
    val json: Json by inject()

    getProfilePhotos()
    getRoommatesList(db)
    updateUserInfo(db, json)
    getUserInfo(db)
    getInterestsList()
    getProfileInfo(db)
}

fun Route.getProfilePhotos() {
    get("/photo/{photoPath...}") {
        val photoPath = call.parameters.getAll("photoPath")
        val filePath = BASE_PHOTO_URI + photoPath?.joinToString("\\")
        val file = File(filePath)
        call.respondFile(file)
    }
}

fun Route.getRoommatesList(db: UserDataSource) {
    authenticate("auth-jwt") {
        get("/suggestions") {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respond(HttpStatusCode.Unauthorized, "No session!")
                return@get
            }
            val page = call.request.queryParameters["page"]?.toIntOrNull()
            if (page == null) {
                call.respond(HttpStatusCode.Conflict, "Missing or invalid parameter!")
                return@get
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()
            if (limit == null) {
                call.respond(HttpStatusCode.Conflict, "Missing or invalid parameter!")
                return@get
            }

            val user = db.findUserById(ObjectId(session.userId))
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found!")
                return@get
            }
            println("Page: $page, limit: $limit ")
            val suggestions = db.getSuggestions(user, page, limit)
            println("Suggestions: ${suggestions.size}\n${user.name} - ${suggestions.map { it.name }}")
            if (suggestions.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "No roommates found! Try changing your settings.")
            } else {
                call.respond(HttpStatusCode.OK, suggestions)
            }
        }
    }
}

fun Route.updateUserInfo(db: UserDataSource, json: Json) {
    authenticate("auth-jwt") {
        post("update/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.Conflict, "Missing or invalid parameter!")
                return@post
            }
            var photoIndex = 1
            lateinit var updatedUserData: UpdateUserRequest
            val photoUris: MutableList<String> = mutableListOf()

            val oldUser = db.findUserById(ObjectId(id))
            if (oldUser == null) {
                call.respond(
                    HttpStatusCode.Conflict,
                    BasicResponse(
                        HttpStatusCode.BadRequest.toString(),
                        "Invalid data."
                    )
                )
                return@post
            }
            var userDir = if (oldUser.photos.isNotEmpty()) {
                val substring = oldUser.photos[0]
                    .substringAfter("photo/")
                    .substringBefore("/")
                substring
            } else {
                ""
            }

            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        updatedUserData = json.decodeFromString(part.value)
                    }
                    is PartData.FileItem -> {
                        // don't generate a new dir name for each photo
                        if (userDir.isEmpty()) {
                            userDir = updatedUserData.name
                                .replace(" ", "_") + "_" + getRandomString()
                        }

                        val dir = File("$BASE_PHOTO_URI\\$userDir\\")
                        if (!dir.exists())
                            dir.mkdirs()

                        val fileName = dir.path + "\\photo_$photoIndex.jpeg"
                        photoIndex++
                        val fileBytes = part.streamProvider().readBytes()
                        File(fileName).writeBytes(fileBytes)
                        photoUris.add(fileName)
                    }
                    else -> {
                        call.respond(HttpStatusCode.BadRequest, "Illegal User Data!\n")
                    }
                }
            }

            val photos = mutableListOf<String>()
            if (photoUris.isNotEmpty()) {
                photos.addAll(photoUris.map {
                    it.replace(BASE_PHOTO_URI, "photo\\").replace("\\", "/")
                })
            }

            val user = Roommate(
                id = oldUser.id,
                username = oldUser.username,
                password = oldUser.password,
                salt = oldUser.salt,
                name = updatedUserData.name,
                photos = photos.toList().ifEmpty { oldUser.photos },
                age = updatedUserData.age,
                city = updatedUserData.city,
                hasRoom = updatedUserData.hasRoom,
                bio = updatedUserData.bio,
                interestedIn = updatedUserData.interestedIn,
                gender = updatedUserData.gender,
                budget = updatedUserData.budget,
                minBudget = updatedUserData.minBudget,
                maxBudget = updatedUserData.maxBudget,
                ageFilterMax = updatedUserData.ageFilterMax,
                ageFilterMin = updatedUserData.ageFilterMin,
                school = updatedUserData.school,
                job = updatedUserData.job,
                interests = updatedUserData.interests.ifEmpty { oldUser.interests },
                matches = oldUser.matches.ifEmpty { listOf() },
                likes = oldUser.likes.ifEmpty { listOf() },
                passes = oldUser.passes.ifEmpty { listOf() },
                lastActivityDate = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString()
            )
            val wasAcknowledged = db.updateUserProfile(user)
            if (!wasAcknowledged) {
                call.respond(
                    HttpStatusCode.Conflict,
                    BasicResponse(
                        HttpStatusCode.Conflict.toString(),
                        "Couldn't write to database."
                    )
                )
                return@post
            }

            call.respond(
                HttpStatusCode.OK,
                BasicResponse(
                    HttpStatusCode.OK.toString(),
                    "Account was updated successfully!"
                )
            )
        }
    }
}

fun Route.getUserInfo(db: UserDataSource) {
    authenticate("auth-jwt") {
        get("user/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.Conflict, "Missing or invalid parameter!")
                return@get
            }
            val user = db.findUserById(ObjectId(id))
            if (user != null) {
                call.respond(
                    HttpStatusCode.OK,
                    user.toUserProfile()
                )
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found!")
            }
        }
    }
}

fun Route.getProfileInfo(db: UserDataSource) {
    authenticate("auth-jwt") {
        get("profile/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.Conflict, "Missing or invalid parameter!")
                return@get
            }
            val profile = db.findProfileById(ObjectId(id))
            if (profile != null) {
                call.respond(
                    HttpStatusCode.OK,
                    profile
                )
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found!")
            }
        }
    }
}

fun Route.getInterestsList() {
    get("/list_sample") {
        val interestsFile = "./src/main/resources/interests.txt"
        val interestsList = File(interestsFile).bufferedReader().readLines().mapIndexed { index, s ->
            Interests(index, s)
        }
        call.respond(HttpStatusCode.OK, interestsList)
    }
}