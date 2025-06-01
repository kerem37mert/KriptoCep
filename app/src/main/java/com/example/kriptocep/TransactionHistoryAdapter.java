package com.example.kriptocep;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {

    List<WalletFragment.Transaction> transactions;
    private Retrofit retrofit;
    private CurrencyAPI currencyAPI;

    public TransactionHistoryAdapter(List<WalletFragment.Transaction> transactions) {
        this.transactions = transactions;

        // Retrofit instance'ını burada oluşturuyoruz, her seferinde değil
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coinlore.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        currencyAPI = retrofit.create(CurrencyAPI.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WalletFragment.Transaction tx = transactions.get(position);

        holder.type.setText(tx.type.toUpperCase());
        holder.amount.setText("Amount: " + tx.amount);
        holder.price.setText("Price: $" + tx.price);
        holder.date.setText("Date: " + new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(new Date(tx.timestamp)));

        fetchCoinName(tx.coinID, holder.coinName);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    private void fetchCoinName(int coinID, TextView coinNameView) {
        Call<List<Currencies>> call = currencyAPI.getCurrency(coinID);

        call.enqueue(new Callback<List<Currencies>>() {
            @Override
            public void onResponse(Call<List<Currencies>> call, Response<List<Currencies>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Currencies coin = response.body().get(0); // API id ile filtreli tek eleman döner
                    coinNameView.setText(coin.name); // symbolde olabilir
                } else {
                    coinNameView.setText("Bilinmeyen Coin");
                }
            }

            @Override
            public void onFailure(Call<List<Currencies>> call, Throwable t) {
                coinNameView.setText("İnternet Hatası");
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView coinName, type, amount, price, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coinName = itemView.findViewById(R.id.txCoinName);
            type = itemView.findViewById(R.id.txType);
            amount = itemView.findViewById(R.id.txAmount);
            price = itemView.findViewById(R.id.txPrice);
            date = itemView.findViewById(R.id.txDate);
        }
    }
}
