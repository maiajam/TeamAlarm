package com.maiajam.myflowres.data.retrofit;


import com.maiajam.myflowres.data.modle.allFlowers.AllFlower;
import com.maiajam.myflowres.helper.Constant;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("photos")
    Observable<List<AllFlower>> getAllFlowersPhotos(@Query("page") int page,
                                                    @Query("per_page") int per_page,
                                                    @Query("order_by") String order_by,
                                                    @Query("client_id") String client_id);

}
