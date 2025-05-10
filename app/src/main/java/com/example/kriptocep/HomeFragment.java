package com.example.kriptocep;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

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
    RecyclerView recyclerViewCoinList;
    ProgressBar progressCoinList;
    LinearLayoutManager linearLayoutManager;
    CurrenciesAdapter currenciesAdapter;
    List<Currencies> currencies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        currencies = new ArrayList<>();

        recyclerViewCoinList = view.findViewById(R.id.recyclerViewCoinList);
        progressCoinList = view.findViewById(R.id.progressCoinList);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewCoinList.setLayoutManager(linearLayoutManager);
        currenciesAdapter = new CurrenciesAdapter(currencies);
        recyclerViewCoinList.setAdapter(currenciesAdapter);

        // WebView kullanrak youtube video gösterme
        WebView webView = view.findViewById(R.id.web);
        String video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/bBC-nXj3Ng4?si=CuJqUT-QxCSzw7dh\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.TRANSPARENT); // Beyaz arka planı kaldırır
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null); // Daha iyi performans
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadData(video, "text/html", "utf-8");

        fetchCurrencies();


    }

    public void fetchCurrencies() {
        progressCoinList.setVisibility(View.VISIBLE);

        // Retrofit kurulumu
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
                    Log.d("API DATA", "Veri alındı: " + response.body().size());
                    currencies.addAll(new ArrayList<>(response.body()));
                    currenciesAdapter.notifyDataSetChanged();
                    progressCoinList.setVisibility(View.GONE);
                } else {
                    Log.e("API ERROR", "Başarısız yanıt ya da boş veri.");
                }
            }

            @Override
            public void onFailure(Call<List<Currencies>> call, Throwable t) {
                Log.e("API Error", t.getMessage());
            }
        });
    }
}
