package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hackillinois.android.database.entity.Leaderboard

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard ORDER BY points DESC")
    fun getLeaderboard(): LiveData<List<Leaderboard>>

    @Query("DELETE FROM leaderboard")
    fun clearTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(profiles: List<Leaderboard>)

    @Transaction
    fun clearTableAndInsertProfiles(profiles: List<Leaderboard>) {
        clearTable()
        insertAll(profiles)
    }
}
