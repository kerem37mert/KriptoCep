package com.example.kriptocep;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CurrenciesAPI {
    @GET("ticker/?id=90,80,518,58,48543,33285,2,257,2713,93845,2751,44883,54683,2321,48555,1,45219,28,93841,47305,111341,48563,46018,148111,118,135601,33718,32607,33830,93847,136105,2679,46183,100427,70497,51947,45161,56821,45985,258,45577,48839,121613")
    Call<List<Currencies>> getCurrencies();
}
