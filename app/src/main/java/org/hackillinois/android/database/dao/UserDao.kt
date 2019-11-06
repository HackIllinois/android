package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hackillinois.android.database.entity.User

@Dao
interface UserDao {
    @Query("SELECT * FROM Users LIMIT 1")
    fun getUser(): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(User: User)

    @Delete
    fun delete(User: User)
}
