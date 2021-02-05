package com.octopus.mapmarks.util;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GGWWServices {

    @GET("/geocode/geo")
    Flowable<ResponseBody> geocode(@Query("address") String address, @Query("key") String key, @Query("output") String output );

}
