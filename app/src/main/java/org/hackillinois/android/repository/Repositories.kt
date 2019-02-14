package org.hackillinois.android.repository

import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.database.entity.User

val qrRepository: GenericRepository<QR> by lazy {
    GenericRepository<QR>(App.getAPI().qrCode, App.getDatabase().qrDao()::insert, App.getDatabase().qrDao()::getQr)
}

val attendeeRepository: GenericRepository<Attendee> by lazy {
    GenericRepository<Attendee>(App.getAPI().attendee, App.getDatabase().attendeeDao()::insert, App.getDatabase().attendeeDao()::getAttendee)
}

val rolesRepository: GenericRepository<Roles> by lazy {
    GenericRepository<Roles>(App.getAPI().roles, App.getDatabase().rolesDao()::insert, App.getDatabase().rolesDao()::getRoles)
}

val userRepository: GenericRepository<User> by lazy {
    GenericRepository<User>(App.getAPI().user, App.getDatabase().userDao()::insert, App.getDatabase().userDao()::getUser)
}
