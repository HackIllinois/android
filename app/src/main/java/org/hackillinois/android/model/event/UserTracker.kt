package org.hackillinois.android.model.event

import org.hackillinois.android.database.entity.Event

data class UserTracker(val userId: String, val eventsList: List<Event>)
