package org.hackillinois.android.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import org.hackillinois.android.database.entity.Roles

@Dao
interface RolesDao {
    @Query("SELECT * FROM roles LIMIT 1")
    fun getRoles(): LiveData<Roles>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(qr: Roles)

    @Delete
    fun delete(qr: Roles)
}
