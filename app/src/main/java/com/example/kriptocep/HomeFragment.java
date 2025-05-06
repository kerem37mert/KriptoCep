package com.example.kriptocep;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    ArrayList<Currencies> currencyModel;
    String baseURL = "https://api.coinlore.net/api/";
    Retrofit retrofit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment first
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Now find the WebView using the inflated view
        WebView webView = view.findViewById(R.id.web);
        String video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/bBC-nXj3Ng4?si=CuJqUT-QxCSzw7dh\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadData(video, "text/html", "utf-8");

        // Retrofit setup
        retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CurrenciesAPI currenciesAPI = retrofit.create(CurrenciesAPI.class);
        Call<List<Currencies>> call = currenciesAPI.getCurrencies();

        call.enqueue(new Callback<List<Currencies>>() {
            @Override
            public void onResponse(Call<List<Currencies>> call, Response<List<Currencies>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currencyModel = new ArrayList<>(response.body());
                    for (Currencies currency : currencyModel) {
                        Log.d("Currency", "Name: " + currency.name + ", Price: " + currency.price_usd);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Currencies>> call, Throwable t) {
                Log.e("API Error", t.getMessage());
            }
        });

        return view;
    }
}
