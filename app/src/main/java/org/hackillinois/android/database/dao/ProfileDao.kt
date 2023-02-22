package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hackillinois.android.database.entity.Profile

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles WHERE id = :profileId")
    fun getProfile(profileId: String): LiveData<Profile>

    @Query("SELECT * FROM profiles ORDER BY id ASC")
    fun getAllProfiles(): LiveData<List<Profile>>

    // TODO: Select profiles where each interest in "interests" exists in the profile's interests
//    @Query("SELECT * FROM profiles WHERE interests = :interests")
//    fun getProfilesWithInterests(interests: List<String>): LiveData<List<Profile>>

    @Query("DELETE FROM profiles")
    fun clearTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(profiles: List<Profile>)

    @Transaction
    fun clearTableAndInsertProfiles(profiles: List<Profile>) {
        clearTable()
        insertAll(profiles)
    }

//    @Query("UPDATE profiles SET description = :newDescription WHERE id = :profileId")
//    fun setDescription(profileId: String, newDescription: String)

//    @Query("UPDATE profiles SET interests = :newInterests WHERE id = :profileId")
//    fun setInterests(profileId: String, newInterests: List<String>)

    @Query("UPDATE profiles SET firstName = :newFirstName WHERE id = :profileId")
    fun setFirstName(profileId: String, newFirstName: String)

    @Query("UPDATE profiles SET lastName = :newLastName WHERE id = :profileId")
    fun setLastName(profileId: String, newLastName: String)

    @Query("UPDATE profiles SET discord = :newDiscord WHERE id = :profileId")
    fun setDiscord(profileId: String, newDiscord: String)

    @Query("UPDATE profiles SET foodWave = :newFoodWave WHERE id = :profileId")
    fun setDiscord(profileId: String, newFoodWave: Int)

//    @Query("UPDATE profiles SET teamStatus = :newTeamStatus WHERE id = :profileId")
//    fun setTeamStatus(profileId: String, newTeamStatus: String)
}
