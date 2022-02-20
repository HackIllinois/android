package org.hackillinois.android.model

// TODO move this class to a proper package
data class Group(
    val avatarIcon: Int,
    val name: String,
    val status: String,
    val starred: Boolean,
    val profileMatch: String,
    val description: String
)
