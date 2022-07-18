package org.kagami.roommate.chat.data.local

import androidx.room.*
import org.kagami.roommate.chat.domain.model.user.UserProfile

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserProfile(user: UserProfile)

    @Query("SELECT * FROM user_table")
    suspend fun getProfile(): UserProfile
}