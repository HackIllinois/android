package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "projects")
@TypeConverters(Converters::class)
data class Project(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val mentors: List<String>,
    val room: String,
    val tags: List<String>,
    val number: Int
) {
    fun getMentorsString(): String {
        var mentors = ""
        for (i in 0..this.mentors.lastIndex - 1) {
            mentors += this.mentors[i] + ", "
        }
        mentors += this.mentors[this.mentors.lastIndex]

        return mentors
    }
}
