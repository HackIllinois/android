package org.hackillinois.android2019.repository

import org.hackillinois.android2019.App
import org.hackillinois.android2019.database.entity.Attendee
import org.hackillinois.android2019.database.entity.QR
import org.hackillinois.android2019.database.entity.Roles
import org.hackillinois.android2019.database.entity.User

val qrRepository: GenericRepository<QR> by lazy {
    GenericRepository(::createQrCodeCall, App.getDatabase().qrDao()::insert, App.getDatabase().qrDao()::getQr)
}

val attendeeRepository: GenericRepository<Attendee> by lazy {
    GenericRepository(::createAttendeeCall, App.getDatabase().attendeeDao()::insert, App.getDatabase().attendeeDao()::getAttendee)
}

val rolesRepository: GenericRepository<Roles> by lazy {
    GenericRepository(::createRolesCall, App.getDatabase().rolesDao()::insert, App.getDatabase().rolesDao()::getRoles)
}

val userRepository: GenericRepository<User> by lazy {
    GenericRepository(::createUserCall, App.getDatabase().userDao()::insert, App.getDatabase().userDao()::getUser)
}

/**
 * These functions exist so that the GenericRepository doesn't hold an instance of the call itself
 * If it does hold an instance of a call, that call also bundles the JWT with it
 * As a result, users logging out of one account and back into the another without closing the app will have the incorrect JWT
 */
private fun createQrCodeCall() = App.getAPI().qrCode
private fun createAttendeeCall() = App.getAPI().attendee
private fun createRolesCall() = App.getAPI().roles
private fun createUserCall() = App.getAPI().user
