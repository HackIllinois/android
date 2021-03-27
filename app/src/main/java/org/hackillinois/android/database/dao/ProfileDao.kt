package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hackillinois.android.database.entity.Profile

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles WHERE id = :profileId")
    fun getProfile(profileId: String): LiveData<Profile>

    @Query("SELECT * FROM profiles")
    fun getAllProfiles(): LiveData<List<Profile>>

    // TODO: Select profiles where each interest in "interests" exists in the profile's interests
    @Query("SELECT * FROM profiles WHERE interests = :interests")
    fun getProfilesWithInterests(interests: List<String>): LiveData<List<Profile>>

    @Query("DELETE FROM profiles")
    fun clearTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(profiles: List<Profile>)

    @Transaction
    fun clearTableAndInsertProfiles(profiles: List<Profile>) {
        clearTable()
        insertAll(profiles)
    }
}