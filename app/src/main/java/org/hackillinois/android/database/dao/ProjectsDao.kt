package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hackillinois.android.database.entity.Project

@Dao
interface ProjectsDao {

    @Query("SELECT * FROM PROJECTS WHERE id = :projectId")
    fun getProject(projectId: String): LiveData<Project>

    @Query("SELECT * from projects")
    fun getAllProjects(): LiveData<List<Project>>

    @Query("SELECT * from projects where tags like '%' || :tag || '%' ORDER BY number")
    fun getProjectsWithTag(tag: String): LiveData<List<Project>>

    @Query("DELETE FROM projects")
    fun clearTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(projects: List<Project>)

    @Transaction
    fun clearTableAndInsertProjects(projects: List<Project>) {
        clearTable()
        insertAll(projects)
    }
}