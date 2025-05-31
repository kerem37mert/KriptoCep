package com.example.kriptocep;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WalletFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String uid;
    Button transactionBtn;
    TextView totalBalance;
    TextView netProfit;
    TextView textViewNotFound;
    TextView textViewCurrencyName;
    TextView textViewValue;
    Retrofit retrofit;
    String baseURL = "https://api.coinlore.net/api/";
    RecyclerView recyclerViewWalletCoin;

    class Transaction {
        int coinID;
        String type;
        double amount;
        double price;
        long timestamp;

        public Transaction(int coinID, String type, double amount, double price, long timestamp) {
            this.coinID = coinID;
            this.type = type;
            this.amount = amount;
            this.price = price;
            this.timestamp = timestamp;
        }
    }

    Map<Integer, List<Transaction>> coinTransactionMap;
    double totalWalletValue = 0.0;
    double totalProfit = 0.0;
    int apiResponsesReceived = 0;

    LinearLayoutManager linearLayoutManager;
    WalletCoinAdapter walletCoinAdapter;
    List<WalletCoinItem> walletList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        coinTransactionMap = new HashMap<>();

        transactionBtn = view.findViewById(R.id.transactionBtn);
        totalBalance = view.findViewById(R.id.totalBalance);
        netProfit = view.findViewById(R.id.netProfit);
        textViewNotFound = view.findViewById(R.id.textViewNotFound);
        textViewCurrencyName = view.findViewById(R.id.textViewCurrencyName);
        textViewValue = view.findViewById(R.id.textViewValue);

        recyclerViewWalletCoin = view.findViewById(R.id.recyclerViewWalletCoin);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewWalletCoin.setLayoutManager(linearLayoutManager);
        walletList = new ArrayList<>();
        walletCoinAdapter = new WalletCoinAdapter( walletList);
        recyclerViewWalletCoin.setAdapter(walletCoinAdapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = auth.getCurrentUser().getUid();

        getTransactions();

        transactionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectCoinActivity.class);
            startActivity(intent);
        });
    }

    public void onResume() {
        super.onResume();
        getTransactions();
    }

    public void getTransactions() {
        db.collection("users")
                .document(uid)
                .collection("transactions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        // Hiç işlem yok: kullanıcı cüzdanına hiç varlık eklememiş
                        textViewNotFound.setVisibility(View.VISIBLE);
                        textViewCurrencyName.setVisibility(View.GONE);
                        textViewValue.setVisibility(View.GONE);
                        return;
                    }

                    textViewNotFound.setVisibility(View.GONE);
                    textViewCurrencyName.setVisibility(View.VISIBLE);
                    textViewValue.setVisibility(View.VISIBLE);

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        int coinID = doc.getLong("coinID").intValue();
                        String type = doc.getString("type");
                        double amount = doc.getDouble("amount");
                        double price = doc.getDouble("price");
                        long timestamp = doc.getLong("timestamp");

                        Transaction transaction = new Transaction(coinID, type, amount, price, timestamp);

                        if (!coinTransactionMap.containsKey(coinID))
                            coinTransactionMap.put(coinID, new ArrayList<>());

                        // Mevcut coin listesine bu transaction'ı ekle
                        coinTransactionMap.get(coinID).add(transaction);                    }

                    // Tüm transaction'lar alındıktan sonra cüzdanı yaz
                    writeWallet();
                });
    }

    public void writeWallet() {
        totalWalletValue = 0.0;
        totalProfit = 0.0;
        apiResponsesReceived = 0;

        for (Map.Entry<Integer, List<Transaction>> entry : coinTransactionMap.entrySet()) {
            int coinID = entry.getKey();
            List<Transaction> transactions = entry.getValue();

            fetchCurrencyWithCallback(coinID, transactions);
        }
    }

    public void fetchCurrencyWithCallback(int coinID, List<Transaction> transactions) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        CurrencyAPI currencyAPI = retrofit.create(CurrencyAPI.class);
        Call<List<Currencies>> call = currencyAPI.getCurrency(coinID);

        call.enqueue(new Callback<List<Currencies>>() {
            @Override
            public void onResponse(Call<List<Currencies>> call, Response<List<Currencies>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Currencies currency = response.body().get(0);

                    double totalBuyAmount = 0.0;
                    double totalBuyCost = 0.0;
                    double totalSellRevenue = 0.0;
                    double netAmount = 0.0;

                    for (Transaction tx : transactions) {
                        if (tx.type.equals("buy")) {
                            totalBuyAmount += tx.amount;
                            totalBuyCost += tx.amount * tx.price;
                            netAmount += tx.amount;
                        } else if (tx.type.equals("sell")) {
                            totalSellRevenue += tx.amount * tx.price;
                            netAmount -= tx.amount;
                        }
                    }

                    double currentValue = netAmount * currency.price_usd;
                    double profit = (totalSellRevenue + currentValue) - totalBuyCost;

                    totalWalletValue += currentValue;
                    totalProfit += profit;

                    WalletCoinItem item = new WalletCoinItem(
                            coinID,
                            currency.name,
                            currency.symbol,
                            netAmount,
                            currency.price_usd,
                            currentValue,
                            profit
                    );
                    walletList.add(item);

                    apiResponsesReceived++;

                    // Tüm coin verileri alındıysa ekrana yaz
                    if (apiResponsesReceived == coinTransactionMap.size()) {

                        // Uygulamanın çökmesini önelemek için
                        if (!isAdded() || getActivity() == null || getView() == null) {
                            return;
                        }

                        totalBalance.setText(String.format("$%.2f", totalWalletValue));
                        if (totalProfit >= 0) {
                            netProfit.setText(String.format("+$%.2f", totalProfit));
                            netProfit.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            netProfit.setText(String.format("$%.2f", totalProfit));
                            netProfit.setTextColor(getResources().getColor(R.color.red));
                        }
                        walletCoinAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("API ERROR", "Başarısız yanıt veya boş veri.");
                }
            }

            @Override
            public void onFailure(Call<List<Currencies>> call, Throwable t) {
                Log.e("API ERROR", t.getMessage());
            }
        });
    }
}
