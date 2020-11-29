package com.radiogbd.streaming.apps.mars.Http;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface BlbaseInterface {
    @GET
    Call<String> geturl(@Url String url);
}
