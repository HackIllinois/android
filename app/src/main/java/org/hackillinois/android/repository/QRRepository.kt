package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log

import org.hackillinois.android.App
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.database.entity.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class QRRepository {
    private val qrDao = App.getDatabase().qrDao()

    fun fetchQR(): LiveData<QR> {
        refreshQr()
        return qrDao.getQr()
    }

    private fun refreshQr() {
        App.getAPI().qrCode.enqueue(object : Callback<QR> {
            override fun onResponse(call: Call<QR>, response: Response<QR>) {
                if (response.isSuccessful) {
                    val qr = response.body()
                    thread {
                        qr?.let { qrDao.insert(qr) }
                    }
                }
            }

            override fun onFailure(call: Call<QR>, t: Throwable) { }
        })
    }

    companion object {
        val instance = QRRepository()
    }
}