package com.maiajam.myflowres.data.retrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiService {

    @GET()
    Observable<String> getAllFlowers();
}
