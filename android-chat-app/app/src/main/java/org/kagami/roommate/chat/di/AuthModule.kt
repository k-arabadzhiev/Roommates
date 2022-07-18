package org.kagami.roommate.chat.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import org.kagami.roommate.chat.domain.model.auth.AuthService
import org.kagami.roommate.chat.data.remote.api.AuthRepository
import org.kagami.roommate.chat.domain.model.use_case.validate.ValidateName
import org.kagami.roommate.chat.domain.model.use_case.validate.ValidatePassword
import org.kagami.roommate.chat.domain.model.use_case.validate.ValidateUsername
import org.kagami.roommate.chat.domain.model.use_case.validate.ValidationUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthService(
        @ApplicationContext context: Context,
        client: HttpClient
    ): AuthService {
        return AuthRepository(context, client)
    }

    @Provides
    @Singleton
    fun provideAccountValidator() = ValidationUseCases(
        username = ValidateUsername(),
        password = ValidatePassword(),
        name = ValidateName()
    )
}