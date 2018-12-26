package org.hackillinois.android.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Event(
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locationDescription: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeString(name)
            writeString(description)
            writeLong(startTime)
            writeLong(endTime)
            writeString(locationDescription)
            writeDouble(latitude)
            writeDouble(longitude)
        }

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }

    fun getStart(): Calendar = Calendar.getInstance().apply { timeInMillis = startTime * MILLIS_IN_SECONDS }
    fun getEnd(): Calendar = Calendar.getInstance().apply { timeInMillis = endTime * MILLIS_IN_SECONDS }

    fun getStartTimeMs() = startTime * 1000L

    fun getStartTimeOfDay(): String {
        val eventStartTime = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("America/Chicago")
            timeInMillis = startTime * MILLIS_IN_SECONDS
        }
        val builder = StringBuilder()
        val hour = eventStartTime.get(Calendar.HOUR_OF_DAY)
        val minutes = eventStartTime.get(Calendar.MINUTE)

        if (hour > 12) {
            builder.append(hour % 12)
            builder.append(":")
            builder.append(minutes)
            builder.append(" PM")
        } else {
            builder.append(hour)
            builder.append(":")
            builder.append(minutes)
            builder.append(" AM")
        }

        return builder.toString()
    }
}

private const val MILLIS_IN_SECONDS = 1000L
