package org.hackillinois.android.model

data class Event (val name: String,
                  val description: String,
                  val startTime: Long,
                  val endTime: Long,
                  val locations: List<EventLocation>,
                  val sponsor: String,
                  val eventType: String
)

data class EventLocation (var description: String,
                          val latitude: Double,
                          val longitude: Double
)

val SiebelCenter = EventLocation("Siebel Center", 40.1138, -88.2249)
val EceBuilding = EventLocation("ECE Building", 40.1148, -88.2280)