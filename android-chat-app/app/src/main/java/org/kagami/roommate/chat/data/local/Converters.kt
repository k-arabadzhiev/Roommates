package org.kagami.roommate.chat.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage
import org.kagami.roommate.chat.domain.model.user.*

class Converters {

    private val parser: Json = Json

    @TypeConverter
    fun fromIntListToString(list: List<Int>): String {
        return parser.encodeToString(list)
    }

    @TypeConverter
    fun fromStringToIntList(value: String): List<Int> {
        return parser.decodeFromString(value)
    }

    @TypeConverter
    fun fromStringListToString(list: List<String>): String {
        return parser.encodeToString(list)
    }

    @TypeConverter
    fun fromStringToStringList(value: String): List<String> {
        return parser.decodeFromString(value)
    }

    @TypeConverter
    fun fromCityToString(city: City): String {
        return parser.encodeToString(city)
    }

    @TypeConverter
    fun fromStringToCity(value: String): City {
        return parser.decodeFromString(value)
    }

    @TypeConverter
    fun fromJobToString(job: Job): String {
        return parser.encodeToString(job)
    }

    @TypeConverter
    fun fromStringToJob(value: String): Job {
        return parser.decodeFromString(value)
    }

    @TypeConverter
    fun fromSchoolToString(school: School): String {
        return parser.encodeToString(school)
    }

    @TypeConverter
    fun fromStringToSchool(value: String): School {
        return parser.decodeFromString(value)
    }

    @TypeConverter
    fun fromInterestsToString(list: List<InterestsDto>): String {
        return parser.encodeToString(list)
    }

    @TypeConverter
    fun fromStringToInterests(value: String): List<InterestsDto> {
        return parser.decodeFromString(value)
    }

}