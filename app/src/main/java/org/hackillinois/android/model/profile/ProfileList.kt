package org.hackillinois.android.model.profile

import org.hackillinois.android.database.entity.Profile
// simple wrapper for profile list so it can be modified (?)
data class ProfileList(val profiles: List<Profile>)
