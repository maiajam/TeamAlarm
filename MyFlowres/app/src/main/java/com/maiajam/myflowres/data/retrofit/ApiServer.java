package com.maiajam.myflowres.data.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServer {

    private static String BASE_URL = "https://api.unsplash.com/";
    private Retrofit retrofit = null;
    private ApiService apiService;
    private  static ApiServer  INSTANCE;

    public ApiServer() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
    }

    public static ApiServer getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new ApiServer();
        return INSTANCE;
    }


}
