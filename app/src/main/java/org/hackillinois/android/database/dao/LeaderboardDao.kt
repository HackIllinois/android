package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hackillinois.android.model.leaderboard.Leaderboard
import org.hackillinois.android.model.leaderboard.LeaderboardProfile

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard ORDER BY points DESC")
    fun getLeaderboard(): LiveData<List<LeaderboardProfile>>

    @Query("DELETE FROM leaderboard")
    fun clearTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(profiles: List<LeaderboardProfile>)

    @Transaction
    fun clearTableAndInsertProfiles(profiles: List<LeaderboardProfile>) {
        clearTable()
        insertAll(profiles)
    }

}