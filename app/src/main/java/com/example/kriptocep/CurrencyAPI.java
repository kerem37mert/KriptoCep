package com.example.kriptocep;

import java.util.List;
import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrencyAPI {
    @GET("ticker/")
    Call<List<Currencies>> getCurrency(@Query("id") int id);
}
