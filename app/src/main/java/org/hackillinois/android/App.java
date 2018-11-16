package org.hackillinois.android;

import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.hackillinois.android.api.API;
import org.hackillinois.android.api.Secret;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static API api;

    public void onCreate() {
        super.onCreate();
    }

    public static API getAPI() {
        if (api != null) {
            return api;
        }

        Interceptor interceptor = new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", Secret.JWT)
                        .build();
                return chain.proceed(newRequest);
            }
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
}
