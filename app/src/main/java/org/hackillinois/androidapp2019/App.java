package org.hackillinois.androidapp2019;

import android.app.Application;
import android.arch.persistence.room.Room;

import org.hackillinois.androidapp2019.database.Database;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static API api;
    private static Database database;

    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(), Database.class, "local-db")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static API getAPI() {
        if (api != null) {
            return api;
        }

        return getAPI("");
    }

    public static API getAPI(final String token) {
        Interceptor interceptor = chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build();
            return chain.proceed(newRequest);
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(API.class);
        return api;
    }

    public static Database getDatabase() {
        return database;
    }
}
