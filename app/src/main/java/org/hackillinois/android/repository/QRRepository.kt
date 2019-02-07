package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log

import org.hackillinois.android.App
import org.hackillinois.android.database.entity.QR
import kotlin.concurrent.thread

class QRRepository {
    private val qrDao = App.getDatabase().qrDao()

    fun fetchQR(): LiveData<QR> {
        refreshQr()
        return qrDao.getQr()
    }

    private fun refreshQr() {
        thread {
            try {
                val response = App.getAPI().qrCode.execute()
                response?.let {
                    if (response.isSuccessful) {
                        val qr = it.body()
                        qr?.let {
                            qrDao.insert(qr)
                        }
                    }
                }
            } catch (exception: Exception) {
                Log.e("QRRepository", exception.message)
            }
        }
    }

    companion object {
        val instance = QRRepository()
    }
}