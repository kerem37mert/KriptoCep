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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    String baseURL = "https://api.coinlore.net/api/";
    Retrofit retrofit;
    RecyclerView recyclerViewCoinList;
    ProgressBar progressCoinList;
    LinearLayoutManager linearLayoutManager;
    CurrenciesAdapter currenciesAdapter;
    List<Currencies> currencies;
    TextView totalMC;
    TextView totalVol;
    TextView btcDom;

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
        currenciesAdapter = new CurrenciesAdapter(getContext(), currencies);
        recyclerViewCoinList.setAdapter(currenciesAdapter);

        totalMC = view.findViewById(R.id.totalMC);
        totalVol = view.findViewById(R.id.totalVOl);
        btcDom = view.findViewById(R.id.btcDom);

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
        fetchGlobalData();

        //Periyodik yenileme başlatılıyor
        handler.post(fetchRunnable);
    }

    public void fetchCurrencies(boolean showProgress) {
        if (showProgress) progressCoinList.setVisibility(View.VISIBLE);

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

    // Handler durdurma
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(fetchRunnable);
    }


    // Global Veriler
    public void fetchGlobalData() {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GlobalDataAPI globalDataAPI = retrofit.create(GlobalDataAPI.class);
        Call<List<GlobalData>> call = globalDataAPI.getGlobalData();

        call.enqueue(new Callback<List<GlobalData>>() {
            @Override
            public void onResponse(Call<List<GlobalData>> call, Response<List<GlobalData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API DATA", "Veri alındı: " + response.body().size());
                    totalMC.setText("$" + formatCurrency(response.body().get(0).total_mcap));
                    totalVol.setText("$" + formatCurrency(response.body().get(0).total_volume));
                    btcDom.setText(String.valueOf(response.body().get(0).btc_d) + "%");

                } else {
                    Log.e("API ERROR", "Başarısız yanıt ya da boş veri.");
                }
            }

            @Override
            public void onFailure(Call<List<GlobalData>> call, Throwable t) {

            }
        });
    }

    private String formatCurrency(double num) {
        if (num >= 1_000_000_000_000.0) {
            return String.format("%.2fT", num / 1_000_000_000_000.0);
        } else if (num >= 1_000_000_000.0) {
            return String.format("%.2fB", num / 1_000_000_000.0);
        } else if (num >= 1_000_000.0) {
            return String.format("%.2fM", num / 1_000_000.0);
        } else if (num >= 1_000.0) {
            return String.format("%.2fK", num / 1_000.0);
        } else {
            return String.format("%.2f", num);
        }
    }
}