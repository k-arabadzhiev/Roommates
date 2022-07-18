package org.kagami.roommate.chat.di

import com.github.jershell.kbson.ObjectIdSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage
import org.kagami.roommate.chat.data.remote.ws.models.messages.PhotoMessage
import org.kagami.roommate.chat.data.remote.ws.models.messages.TextMessage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSerialization(): Json = Json {
        val serializerModule = SerializersModule {
//            contextual(ObjectIdSerializer)
            polymorphic(ChatMessage::class) {
                subclass(TextMessage::class)
                subclass(PhotoMessage::class)
            }
        }
        serializersModule = serializerModule
    }

    @Provides
    @Singleton
    fun provideHttpClient(jsonConfig: Json): HttpClient {
        return HttpClient(OkHttp) {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(HttpCookies)
            install(ContentNegotiation) {
                json(jsonConfig)
            }
            install(WebSockets)
        }
    }
}