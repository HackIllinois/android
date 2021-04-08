package org.hackillinois.android

import android.app.Application
import android.util.Log
import androidx.room.Room
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.hackillinois.android.database.Database
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(applicationContext, Database::class.java, "local-db")
                .fallbackToDestructiveMigration()
                .build()
    }

    companion object {
        private var apiInitialized = false
        private lateinit var apiInternal: API

        lateinit var database: Database
            private set

        fun getAPI(token: String? = null): API {
            if (token == null) {
                return if (apiInitialized) apiInternal else getAPI("")
            }

            val interceptor = { chain: Interceptor.Chain ->
                val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", token)
                        .build()
                chain.proceed(newRequest)
            }

            val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(API.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            apiInternal = retrofit.create(API::class.java)
            apiInitialized = true
            return apiInternal
        }
    }
}
