package com.example.kriptocep;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GlobalDataAPI {
    @GET("global/")
    Call<List<GlobalData>> getGlobalData();
}
