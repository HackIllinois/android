package org.hackillinois.android2019.model.event

import org.hackillinois.android2019.database.entity.Event

data class UserTracker(val userId: String, val eventsList: List<Event>)