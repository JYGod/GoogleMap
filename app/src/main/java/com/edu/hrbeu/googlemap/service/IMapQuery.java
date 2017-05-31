package com.edu.hrbeu.googlemap.service;


import com.edu.hrbeu.googlemap.pojo.RoutesPOJO;

import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface IMapQuery {

    @GET("json")
    Call<RoutesPOJO> getRoutes(@QueryMap Map<String,String> map);


    Retrofit retrofit= new Retrofit.Builder()
            .client(new OkHttpClient())
            .baseUrl("https://maps.googleapis.com/maps/api/directions/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
