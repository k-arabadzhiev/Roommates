package org.kagami.roommate.chat.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import kotlinx.serialization.json.Json
import org.kagami.roommate.chat.data.local.UserDatabase
import org.kagami.roommate.chat.data.preferences.DataStorePreferences
import org.kagami.roommate.chat.data.remote.api.MatchRepositoryImpl
import org.kagami.roommate.chat.data.remote.api.ProfileApi
import org.kagami.roommate.chat.data.remote.api.ProfileApiImpl
import org.kagami.roommate.chat.data.remote.api.ProfileRepositoryImpl
import org.kagami.roommate.chat.data.remote.ws.ChatSocketService
import org.kagami.roommate.chat.data.remote.ws.ChatSocketServiceImpl
import org.kagami.roommate.chat.domain.model.repository.MatchRepository
import org.kagami.roommate.chat.domain.model.repository.ProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserDataModule {

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileApiImpl: ProfileApi,
        db: UserDatabase,
        datastore: DataStorePreferences
    ): ProfileRepository {
        return ProfileRepositoryImpl(
            profileApiImpl,
            db,
            datastore
        )
    }

    @Provides
    @Singleton
    fun provideMatchRepository(
        client: HttpClient
    ): MatchRepository {
        return MatchRepositoryImpl(client)
    }

    @Provides
    @Singleton
    fun provideProfileApi(
        @ApplicationContext context: Context,
        client: HttpClient,
        parser: Json
    ): ProfileApi {
        return ProfileApiImpl(
            context, client, parser
        )
    }

    @Provides
    @Singleton
    fun provideChatService(
        client: HttpClient,
        parser: Json
    ): ChatSocketService {
        return ChatSocketServiceImpl(client, parser)
    }
}