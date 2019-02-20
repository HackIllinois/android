package org.hackillinois.androidapp2019.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import org.hackillinois.androidapp2019.database.entity.User

@Dao
interface UserDao {
    @Query("SELECT * FROM Users LIMIT 1")
    fun getUser(): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(User: User)

    @Delete
    fun delete(User: User)
}
