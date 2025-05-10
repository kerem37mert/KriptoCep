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

        String formattedMarketCap = formatCurrency(currency.market_cap_usd);
        holder.coinMarketCapTextView.setText("MCap: " + formattedMarketCap);
    }

    @Override
    public int getItemCount() {
        return currencies != null ? currencies.size() : 0;
    }

    private String formatCurrency(double num) {
        if (num >= 1000000000) {
            return String.format("%.2fB", num / 1000000000);
        } else if (num >= 1_000_000) {
            return String.format("%.2fM", num / 1000000);
        } else if (num >= 1_000) {
            return String.format("%.2fK", num / 1000);
        } else {
            return String.valueOf(num);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView coinNameTextView;
        TextView coinPriceTextView;
        TextView coinChangeTextView;
        TextView coinSymbolTextView;

        TextView coinMarketCapTextView;
        ImageView coinImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            coinMarketCapTextView = itemView.findViewById(R.id.coin_list_item_market_cap);
            coinNameTextView = itemView.findViewById(R.id.coin_list_item_name);
            coinPriceTextView = itemView.findViewById(R.id.coin_list_item_price);
            coinChangeTextView = itemView.findViewById(R.id.coin_list_item_change_24h);
            coinSymbolTextView = itemView.findViewById(R.id.coin_list_item_symbol);
            coinImageView = itemView.findViewById(R.id.coin_list_item_icon);
        }
    }
}
