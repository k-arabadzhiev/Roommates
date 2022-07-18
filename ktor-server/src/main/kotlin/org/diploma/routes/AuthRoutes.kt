package org.diploma.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.diploma.data.requests.LoginRequest
import org.diploma.data.requests.NewUserRequest
import org.diploma.data.requests.ReLoginRequest
import org.diploma.data.responses.BasicResponse
import org.diploma.data.responses.LoginResponse
import org.diploma.data.user.*
import org.diploma.security.hashing.HashingService
import org.diploma.security.hashing.SaltedHash
import org.diploma.security.token.TokenClaim
import org.diploma.security.token.TokenConfig
import org.diploma.security.token.TokenService
import org.diploma.util.Constants.MIN_PASS_LENGTH
import org.koin.ktor.ext.inject
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

fun Route.authRoutes() {
    val userDataSource: UserDataSource by inject()
    val tokenService: TokenService by inject()
    val hashingService: HashingService by inject()
    val tokenConfig: TokenConfig by inject()

    login(userDataSource, tokenConfig, hashingService, tokenService)
    createUserAccount(userDataSource, hashingService)
    checkUsername(userDataSource)
    initDbDataWithRandomization(userDataSource, hashingService)
    relog()
}

fun Route.checkUsername(db: UserDataSource) {
    // checking if username exists to reduce traffic
    get("/username") {
        val username = call.request.queryParameters["v"]
        if (username != null) {
            try {
                if (db.checkIfUsernameExists(username)) {
                    throw UserExistsException()
                } else {
                    call.respond(HttpStatusCode.OK, "Username available!")
                }
            } catch (e: UserExistsException) {
                call.respond(HttpStatusCode.Conflict, e.message.toString())
                return@get
            }
        } else {
            call.respond(HttpStatusCode.Conflict, "Empty username!")
        }
    }
}

fun Route.createUserAccount(
    db: UserDataSource, hashingService: HashingService
) {
    post("signup") {
        try {
            val newUser = call.receive<NewUserRequest>()
            if (db.checkIfUsernameExists(newUser.username)) {
                throw UserExistsException()
            }
            val areFieldsBlank = newUser.username.isBlank() || newUser.password.isBlank()
            val isPwTooShort = newUser.password.length < MIN_PASS_LENGTH
            if (areFieldsBlank || isPwTooShort) {
                call.respond(
                    HttpStatusCode.Conflict,
                    BasicResponse(message = "Invalid data.")
                )
                return@post
            }
            val saltedHash = hashingService.generateSaltedHash(newUser.password)
            val user = Roommate(
                username = newUser.username,
                password = saltedHash.hash,
                salt = saltedHash.salt,
                name = newUser.name,
                gender = newUser.gender,
                age = newUser.age,
                photos = listOf(),
                budget = -1,
                minBudget = -1,
                maxBudget = -1,
                city = City("", ""),
                hasRoom = false,
                interestedIn = listOf(),
                ageFilterMin = -1,
                ageFilterMax = -1,
                bio = "",
                school = School(false, ""),
                job = Job(Company(false, ""), Title(false, "")),
                lastActivityDate = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString()
            )
            val insertedId = db.insertNewUser(user)
            if (insertedId.isEmpty()) {
                call.respond(
                    HttpStatusCode.Conflict,
                    BasicResponse(message = "Couldn't write to database.")
                )
                return@post
            }
            call.respond(
                HttpStatusCode.OK,
                BasicResponse(message = insertedId)
            )
        } catch (e: UserExistsException) {
            call.respond(
                HttpStatusCode.Conflict,
                BasicResponse(message = e.message.toString())
            )
            return@post
        }
    }
}

fun Route.login(
    db: UserDataSource,
    tokenConfig: TokenConfig,
    hashingService: HashingService,
    tokenService: TokenService
) {
    post("/login") {
        val request = call.receiveOrNull<LoginRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = db.getUserByUsername(request.username)
        if (user == null) {
            val response = LoginResponse(message = "Incorrect username or password")
            call.respond(HttpStatusCode.Conflict, response)
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            val response = LoginResponse(message = "Incorrect username or password")
            call.respond(HttpStatusCode.Conflict, response)
            return@post
        }

        println("userId: ${user.id} ${user.id.toHexString()}")
        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )
        if (call.sessions.get<UserSession>() == null) {
            val clientId = call.request.header("client-id") ?: ""
            println("client-id: $clientId")
            call.sessions.set(UserSession(clientId, user.id.toString()))
        }
        call.respond(
            status = HttpStatusCode.OK,
            message = LoginResponse(token = token, message = user.id.toHexString())
        )
    }
}

fun Route.relog() {
    authenticate("auth-jwt") {
        post("/silent_login") {
            val request = call.receiveOrNull<ReLoginRequest>() ?: run {
                val response =
                    BasicResponse(
                        status = HttpStatusCode.Unauthorized.toString(),
                        message = "Missing or invalid parameter!"
                    )
                call.respond(HttpStatusCode.Unauthorized, response)
                return@post
            }
            if (call.sessions.get<UserSession>() == null) {
                val clientId = call.request.header("client-id") ?: ""
                println("client-id: $clientId")
                call.sessions.set(UserSession(clientId, request.id))
                call.respond(
                    HttpStatusCode.OK, BasicResponse(message = "OK")
                )
            }
        }
    }
}

fun Route.initDbDataWithRandomization(db: UserDataSource, hashingService: HashingService) {
    get("/init_db") {
        val maleNamesFile = "./src/main/resources/male names.txt"
        val femaleNamesFile = "./src/main/resources/female names.txt"
        val surnamesFile = "./src/main/resources/surnames.txt"
        val nbh = "./src/main/resources/neighborhoods.txt"

        val jobFile = "./src/main/resources/jobs.txt"
        val companiesFile = "./src/main/resources/companies.txt"
        val interestsFile = "./src/main/resources/interests.txt"
        val femalePhotoUrlsFile = "./src/main/resources/unsplash woman photo urls.txt"
        val malePhotoUrlsFile = "./src/main/resources/unsplash man photo urls.txt"

        val maleNames = File(maleNamesFile).bufferedReader().readLines()
        val femaleNames = File(femaleNamesFile).bufferedReader().readLines()
        val surnames = File(surnamesFile).bufferedReader().readLines()
        val nbhs = File(nbh).bufferedReader().readLines()
        val jobs = File(jobFile).bufferedReader().readLines()
        val companies = File(companiesFile).bufferedReader().readLines()
        val interestsList = File(interestsFile).bufferedReader().readLines()
        val femaleUrls = File(femalePhotoUrlsFile).bufferedReader().readLines()
        val maleUrls = File(malePhotoUrlsFile).bufferedReader().readLines()
        val allowedNames = maleNames + femaleNames

        repeat(500) { number ->
            val firstName = allowedNames.random()
            val surname = surnames.random()
            val userName = "${firstName.lowercase()}_${surname.lowercase()}"
            val password = "123456"
            val name = "$firstName $surname"
            val gender = if (firstName in maleNames) 0 else 1

/*            val photoUris: MutableList<String> = mutableListOf()
            val userDir = name
                .replace(" ", "_") + "_" + getRandomString()
            val dir = File("$BASE_PHOTO_URI\\$userDir\\")
            dir.mkdirs()

            repeat(3) {
                val fileName = dir.path + "\\photo_${it.plus(1)}.jpeg"
                val fileBytes = File("./src/main/resources/photo_${it.plus(1)}.jpeg").readBytes()
                File(fileName).writeBytes(fileBytes)
                photoUris.add(fileName)
            }

            val photos = photoUris.map {
                it.replace(BASE_PHOTO_URI, "photo\\").replace("\\", "/")
            }*/
            val photos = if (gender == 1) {
                val photo = femaleUrls.random()
                val photo1 = femaleUrls.random()
                val photo2 = femaleUrls.random()
                listOf(photo, photo1, photo2)
            } else {
                val photo = maleUrls.random()
                val photo1 = maleUrls.random()
                val photo2 = maleUrls.random()
                listOf(photo, photo1, photo2)
            }

            val city = City("Sofia", nbhs.random())
            val hasRoom = number % 2 == 0
            val interestedInSize = (1..2).random()
            val interestedIn = (1..interestedInSize).map {
                (0..1).random()
            }
            val minAge = (18..22).random()
            val maxAge = (30..40).random()
            val interests = mutableListOf<Interests>()
            repeat(3) {
                val id = (interestsList.indices).random()
                interests.add(Interests(id, interestsList[id]))
            }

            val activity = LocalDateTime.now()
                .minusDays((1L..10L).random())
                .truncatedTo(ChronoUnit.MILLIS).toString()

            val minBudget = (55..70).random() * 5
            val maxBudget = (80..100).random() * 5
            val budget = if (minBudget < maxBudget) {
                minBudget + (maxBudget - minBudget) / 2
            } else {
                maxBudget + (minBudget - maxBudget) / 2
            }

            val saltedHash = hashingService.generateSaltedHash(password)
            val user = Roommate(
                username = userName,
                password = saltedHash.hash,
                salt = saltedHash.salt,
                name = name,
                photos = photos,
                age = (20..30).random(),
                city = city,
                hasRoom = hasRoom,
                bio = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s.",
                interestedIn = interestedIn,
                gender = gender,
                budget = budget,
                maxBudget = if (minBudget < maxBudget) maxBudget else minBudget,
                minBudget = if (minBudget < maxBudget) minBudget else maxBudget,
                ageFilterMax = if (minAge < maxAge) maxAge else minAge,
                ageFilterMin = if (minAge < maxAge) minAge else maxAge,
                school = School(Random.nextBoolean(), "TU-Sofia"),
                job = Job(
                    Company(Random.nextBoolean(), companies.random()),
                    Title(Random.nextBoolean(), jobs.random())
                ),
                interests = interests,
                matches = listOf(),
                likes = listOf(),
                passes = listOf(),
                lastActivityDate = activity
            )

            db.insertUser(user)
        }

        call.respond(HttpStatusCode.OK)
    }
}