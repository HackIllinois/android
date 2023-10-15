package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hackillinois.android.database.entity.Profile

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles LIMIT 1")
    fun getProfile(): LiveData<Profile>

    @Query("DELETE FROM profiles")
    fun clearTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(profiles: List<Profile>)

    @Transaction
    fun clearTableAndInsertProfiles(profiles: List<Profile>) {
        clearTable()
        insertAll(profiles)
    }

    @Query("UPDATE profiles SET foodWave = :newFoodWave WHERE _id = :profileId")
    fun setWave(profileId: String, newFoodWave: Int)
}
