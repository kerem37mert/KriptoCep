package com.example.kriptocep;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SelectCoinActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText editTextCoinSearch;
    TextView textViewNoResult;
    Retrofit retrofit;
    ArrayList<Currencies> currencyModel;
    String baseURL = "https://api.coinlore.net/api/";
    RecyclerView recyclerViewSelectCoin;
    ProgressBar progressSelectCoin;
    LinearLayoutManager linearLayoutManager;
    SelectCoinAdapter selectCoinAdapter;
    List<Currencies> currencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_coin);

        toolbar = findViewById(R.id.toolbar);
        editTextCoinSearch = findViewById(R.id.editTextCoinSearch);
        textViewNoResult = findViewById(R.id.textViewNoResult);

        currencies = new ArrayList<>();

        recyclerViewSelectCoin = findViewById(R.id.recyclerViewSelectCoin);
        progressSelectCoin = findViewById(R.id.progressSelectCoin);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewSelectCoin.setLayoutManager(linearLayoutManager);
        selectCoinAdapter = new SelectCoinAdapter(currencies);
        recyclerViewSelectCoin.setAdapter(selectCoinAdapter);

        progressSelectCoin.setVisibility(View.GONE);
        fetchCurrencies(true);

        // Toolbar Geri Tuşu
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        getSupportActionBar().setDisplayShowTitleEnabled(false); // Uygulama ismini toolbarda gizleme

        // COIN ARAMA
        editTextCoinSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCoins(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void fetchCurrencies(boolean showProgress) {
        if (showProgress) {
            progressSelectCoin.setVisibility(View.VISIBLE);
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
                    selectCoinAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API ERROR", "Başarısız yanıt ya da boş veri.");
                }

                if (showProgress)
                    progressSelectCoin.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Currencies>> call, Throwable t) {
                Log.e("API Error", t.getMessage());

                if (showProgress)
                    progressSelectCoin.setVisibility(View.GONE);
            }
        });
    }

    public void filterCoins(String keyword) {
        List<Currencies> filteredList = new ArrayList<>();

        for (Currencies currency : currencies) {
            if (currency.name.toLowerCase().contains(keyword.toLowerCase())
                    || currency.symbol.toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(currency);
            }
        }

        if (filteredList.isEmpty())
            textViewNoResult.setVisibility(View.VISIBLE);
        else
            textViewNoResult.setVisibility(View.GONE);

        selectCoinAdapter.updateList(filteredList);
    }
}