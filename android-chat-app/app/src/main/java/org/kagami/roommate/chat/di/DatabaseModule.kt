package org.kagami.roommate.chat.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.kagami.roommate.chat.data.local.UserDatabase
import org.kagami.roommate.chat.util.Constants.USER_DB
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            USER_DB,
        ).build()
    }

    @Provides
    @Singleton
    fun provideMatchDao(
        db: UserDatabase
    ) = db.userDao()

}