package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData

import org.hackillinois.android.App
import org.hackillinois.android.database.entity.QR
import kotlin.concurrent.thread

class QRRepository {
    private val qrDao = App.getDatabase().qrDao()

    private val minutesTillStale: Long = 2
    private val secondsInMinute: Long = 60
    private val millisInSecond: Long = 1000
    private val millisTillStale = minutesTillStale * secondsInMinute * millisInSecond

    fun fetchQR(): LiveData<QR> {
        refreshQr()
        return qrDao.getQr()
    }

    private fun refreshQr() {
        thread {
            // checks to see if an updated version of the qr code is in the database
            // if not, run API query and save it in DB
            val userExists = qrDao.hasUpdatedQr(System.currentTimeMillis() - millisTillStale) != null
            if (!userExists) {
                val response = App.getAPI().qrCode.execute()
                response?.let {
                    val qr = it.body()
                    qr?.let {
                        qr.lastRefreshed = System.currentTimeMillis()
                        qrDao.insert(qr)
                    }
                }
            }
        }
    }

    companion object {
        val instance = QRRepository()
    }
}