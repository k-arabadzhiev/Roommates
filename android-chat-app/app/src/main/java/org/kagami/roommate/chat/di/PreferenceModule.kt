package org.kagami.roommate.chat.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.kagami.roommate.chat.data.preferences.DataStorePreferences
import org.kagami.roommate.chat.domain.preferences.UserPreferences

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceModule {

    @Binds
    abstract fun preferenceDataStore(dataStorePreferences: DataStorePreferences): UserPreferences
}