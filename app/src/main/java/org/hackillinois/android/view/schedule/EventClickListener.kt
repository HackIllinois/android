package org.hackillinois.android.view.schedule

import org.hackillinois.android.database.entity.Event

interface EventClickListener {
    // iOS doesn't have this -- Hack 2021
    fun openEventInfoActivity(event: Event)
}
