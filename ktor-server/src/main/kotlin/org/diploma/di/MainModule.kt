package org.diploma.di

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*
import org.diploma.ChatServer
import org.diploma.data.chat.messages.*
import org.diploma.data.matches.MatchesDataSource
import org.diploma.data.matches.MongoMatchesDataSource
import org.diploma.data.user.MongoUserDataSource
import org.diploma.data.user.UserDataSource
import org.diploma.security.hashing.HashingService
import org.diploma.security.hashing.SHA256HashingService
import org.diploma.security.token.JwtTokenService
import org.diploma.security.token.TokenConfig
import org.diploma.security.token.TokenService
import org.diploma.util.Constants.ROOMMATES_DB
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import org.litote.kmongo.reactivestreams.KMongo
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val mainModule = module {
    single<CoroutineDatabase> {
        val client: CoroutineClient = get()
        client.getDatabase(ROOMMATES_DB)
    }
    single<CoroutineClient> {
        KMongo.createClient().coroutine
    }
    single<UserDataSource> {
        MongoUserDataSource(get())
    }
    single<MatchesDataSource> {
        MongoMatchesDataSource(get())
    }
    single<TokenService> {
        JwtTokenService()
    }
    single<HashingService> {
        SHA256HashingService()
    }
    single<Json> {
        Json {
            val chatMessengerSerializer = SerializersModule {
                polymorphic(ChatMessage::class) {
                    subclass(TextMessage::class)
                    subclass(PhotoMessage::class)
                }
                polymorphic(StatusMessage::class) {
                    subclass(Seen::class)
                    subclass(Delivered::class)
                    subclass(Sent::class)
                }
            }
            serializersModule = chatMessengerSerializer + IdKotlinXSerializationModule
            prettyPrint = false
            ignoreUnknownKeys = true
        }
    }
    single {
        val iss = getProperty<String>("jwt_issuer")
        val aud = getProperty<String>("jwt_audience")
        val sec = getProperty<String>("jwt_secret")
        TokenConfig(
            issuer = iss,
            audience = aud,
            expiresIn = 30.toDuration(DurationUnit.DAYS).toLong(DurationUnit.MILLISECONDS),
            secret = sec
        )
    }

    single {
        val chatServer = ChatServer()
        chatServer
    }
}