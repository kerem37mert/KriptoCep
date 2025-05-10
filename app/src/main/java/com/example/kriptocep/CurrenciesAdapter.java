package com.example.kriptocep;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CurrenciesAdapter extends RecyclerView.Adapter<CurrenciesAdapter.ViewHolder> {

    List<Currencies> currencies;

    public CurrenciesAdapter(List<Currencies> currencies) {
        this.currencies = currencies;
    }

    @NonNull
    @Override
    public CurrenciesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coin_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrenciesAdapter.ViewHolder holder, int position) {
        Currencies currency = currencies.get(position);
        holder.coinNameTextView.setText(currency.name);
        holder.coinPriceTextView.setText(String.valueOf(currency.price_usd));
        holder.coinChangeTextView.setText(String.valueOf(currency.percent_change_24h));
        holder.coinSymbolTextView.setText(currency.symbol);
    }

    @Override
    public int getItemCount() {
        return currencies != null ? currencies.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView coinNameTextView;
        TextView coinPriceTextView;
        TextView coinChangeTextView;
        TextView coinSymbolTextView;
        ImageView coinImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            coinNameTextView = itemView.findViewById(R.id.coin_list_item_name);
            coinPriceTextView = itemView.findViewById(R.id.coin_list_item_price);
            coinChangeTextView = itemView.findViewById(R.id.coin_list_item_change_24h);
            coinSymbolTextView = itemView.findViewById(R.id.coin_list_item_symbol);
            coinImageView = itemView.findViewById(R.id.coin_list_item_icon);
        }
    }
}
