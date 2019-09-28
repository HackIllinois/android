package org.hackillinois.android.repository

import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.database.entity.User

val qrRepository: GenericRepository<QR> by lazy {
    GenericRepository(App.getAPI().qrCode(), App.database.qrDao()::insert, App.database.qrDao()::getQr)
}

val attendeeRepository: GenericRepository<Attendee> by lazy {
    GenericRepository(App.getAPI().attendee(), App.database.attendeeDao()::insert, App.database.attendeeDao()::getAttendee)
}

val rolesRepository: GenericRepository<Roles> by lazy {
    GenericRepository(App.getAPI().roles(), App.database.rolesDao()::insert, App.database.rolesDao()::getRoles)
}

val userRepository: GenericRepository<User> by lazy {
    GenericRepository(App.getAPI().user(), App.database.userDao()::insert, App.database.userDao()::getUser)
}
