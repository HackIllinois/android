package org.hackillinois.android.view.home.eventlist

import org.hackillinois.android.database.entity.Event

interface EventClickListener {
    fun openEventInfoActivity(event: Event)
}
