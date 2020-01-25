package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hackillinois.android.database.entity.ProjectModel

@Dao
interface ProjectsDao {
    @Query("SELECT * from projects")
    fun getAllProjects(): LiveData<List<ProjectModel>>

    @Query("SELECT * from projects where tags like :tag LIMIT 1")
    fun getProjectsWithTag(tag: String): LiveData<List<ProjectModel>>

    @Query("DELETE FROM projects")
    fun clearTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(projects: List<ProjectModel>)

    @Transaction
    fun clearTableAndInsertProjects(projects: List<ProjectModel>) {
        clearTable()
        insertAll(projects)
    }
}