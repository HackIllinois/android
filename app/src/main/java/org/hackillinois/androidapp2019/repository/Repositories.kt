package org.hackillinois.androidapp2019.repository

import org.hackillinois.androidapp2019.App
import org.hackillinois.androidapp2019.database.entity.Attendee
import org.hackillinois.androidapp2019.database.entity.QR
import org.hackillinois.androidapp2019.database.entity.Roles
import org.hackillinois.androidapp2019.database.entity.User

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
