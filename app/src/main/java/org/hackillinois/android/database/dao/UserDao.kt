package org.hackillinois.android.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import org.hackillinois.android.database.entity.User

@Dao
interface UserDao {
    @Query("SELECT * FROM Users LIMIT 1")
    fun getUser(): LiveData<User>

    @Query("SELECT * FROM Users WHERE last_refreshed > :lastRefreshMax LIMIT 1")
    fun hasUpdatedUser(lastRefreshMax: Long): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(User: User)

    @Delete
    fun delete(User: User)
}
