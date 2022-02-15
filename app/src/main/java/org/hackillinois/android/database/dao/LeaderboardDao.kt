package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hackillinois.android.model.leaderboard.LeaderboardEntity

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard ORDER BY points DESC")
    fun getLeaderboard(): LiveData<List<LeaderboardEntity>>

    @Query("DELETE FROM leaderboard")
    fun clearTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(profiles: List<LeaderboardEntity>)

    @Transaction
    fun clearTableAndInsertProfiles(profiles: List<LeaderboardEntity>) {
        clearTable()
        insertAll(profiles)
    }
}
