package com.maiajam.myflowres.data.retrofit;

import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServer {

    private static String BASE_URL = "https://api.unsplash.com/";
    private Retrofit retrofit = null;
    private ApiService apiService;
    private  static ApiServer  INSTANCE;

    public ApiService ApiServer() {
        if (retrofit == null) {

            HttpLoggingInterceptor logger1 = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logger1)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    public static ApiServer getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new ApiServer();
        return INSTANCE;
    }




}
