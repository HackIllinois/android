package org.hackillinois.android.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.hackillinois.android.database.helpers.EventLocation

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>) = Gson().toJson(list)

    @TypeConverter
    fun eventLocationFromString(value: String): List<EventLocation> {
        val listType = object : TypeToken<List<EventLocation>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun stringFromEventLocation(list: List<EventLocation>) = Gson().toJson(list)
}
