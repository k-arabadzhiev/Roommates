package org.kagami.roommate.chat.util

object Constants {

    const val BASE_URL = "http://10.0.2.2:8080"
    const val BASE_URL_WS = "ws://10.0.2.2:8080"
    const val SIGN_UP_ENDPOINT = "$BASE_URL/signup"
    const val LOGIN_ENDPOINT = "$BASE_URL/login"
    const val UPDATE_PROFILE_ENDPOINT = "$BASE_URL/update/"
    const val GET_INTERESTS_ENDPOINT = "$BASE_URL/list_sample"
    const val SILENT_RELOG_ENDPOINT = "$BASE_URL/silent_login"
    const val SUGGESTIONS_ENDPOINT = "$BASE_URL/suggestions"
    const val PROFILE_ENDPOINT = "$BASE_URL/profile/"
    const val USER_ENDPOINT = "$BASE_URL/user/"
    const val MATCHES_ENDPOINT = "$BASE_URL/matches"

    const val PASS_ENDPOINT = "$BASE_URL/pass/"
    const val LIKE_ENDPOINT = "$BASE_URL/like/"
    const val MESSAGE_ENDPOINT_PATH = "/messages/"
    const val CHAT_ENDPOINT_PATH = "/chat"

    const val USER_TABLE = "user_table"
    const val USER_DB = "user_db"
    const val UNAUTHORIZED = "UNAUTHORIZED"

    const val ITEMS_PER_PAGE = 10

    const val MIN_USERNAME = 6
    const val MIN_PASSWORD = 6
    const val USER_DATA_STORE = "user_settings"
    const val CLIENT_ID_HEADER = "client-id"
    const val JWT_TOKEN = "token"
    const val NAME = "name"
    const val PROFILE_PHOTO = "photo"

    const val ID = "id"
    const val MIN_BUDGET = 200f
    const val MAX_BUDGET = 650f
    const val MIN_BUDGET_DEFAULT = 250f
    const val MAX_BUDGET_DEFAULT = 400f
    const val BUDGET_DEFAULT = 325f
    const val MIN_AGE = 18f
    const val MAX_AGE = 50f
    const val MIN_AGE_DEFAULT = 18f
    const val MAX_AGE_DEFAULT = 27f
    val GENDER_CHOICES = listOf("Male", "Female", "Don't care")
    const val MAX_INTERESTS = 3
    const val MAX_PHOTO = 3
    const val MAX_BIO = 300
    const val MESSAGES_COUNT = 50
    const val SENDER_ID = "sender-id"
}