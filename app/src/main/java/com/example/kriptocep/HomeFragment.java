package com.example.kriptocep;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler; // ✅
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

    // Handler ve Runnable tanımı
    Handler handler = new Handler();
    final int FETCH_INTERVAL = 30000; // 30 saniye
    final Runnable fetchRunnable = new Runnable() {
        @Override
        public void run() {
            fetchCurrencies(false);
            handler.postDelayed(this, FETCH_INTERVAL); // tekrar çalıştır
        }
    };

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

        // WebView ile video gösterimi
        WebView webView = view.findViewById(R.id.web);
        String video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/bBC-nXj3Ng4?si=CuJqUT-QxCSzw7dh\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadData(video, "text/html", "utf-8");

        progressCoinList.setVisibility(View.VISIBLE); // progressbarı göster
        fetchCurrencies(true);

        //Periyodik yenileme başlatılıyor
        handler.post(fetchRunnable);
    }

    public void fetchCurrencies(boolean showProgress) {
        if (showProgress) {
            progressCoinList.setVisibility(View.VISIBLE);
        }

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
                    currencies.clear();
                    currencies.addAll(new ArrayList<>(response.body()));
                    currenciesAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API ERROR", "Başarısız yanıt ya da boş veri.");
                }

                if (showProgress)
                    progressCoinList.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Currencies>> call, Throwable t) {
                Log.e("API Error", t.getMessage());

                if (showProgress)
                    progressCoinList.setVisibility(View.GONE);
            }
        });
    }


    // Handler durdurme
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(fetchRunnable);
    }
}