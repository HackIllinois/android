package org.hackillinois.android.repository

import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.database.entity.User

val qrRepository: GenericRepository<QR> by lazy {
    GenericRepository(::createQrCodeCall, App.database.qrDao()::insert, App.database.qrDao()::getQr)
}

val attendeeRepository: GenericRepository<Attendee> by lazy {
    GenericRepository(::createAttendeeCall, App.database.attendeeDao()::insert, App.database.attendeeDao()::getAttendee)
}

val rolesRepository: GenericRepository<Roles> by lazy {
    GenericRepository(::createRolesCall, App.database.rolesDao()::insert, App.database.rolesDao()::getRoles)
}

val userRepository: GenericRepository<User> by lazy {
    GenericRepository(::createUserCall, App.database.userDao()::insert, App.database.userDao()::getUser)
}

private fun createQrCodeCall() = App.getAPI().qrCode()
private fun createAttendeeCall() = App.getAPI().attendee()
private fun createRolesCall() = App.getAPI().roles()
private fun createUserCall() = App.getAPI().user()
