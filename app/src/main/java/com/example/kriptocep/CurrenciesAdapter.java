package com.example.kriptocep;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CurrenciesAdapter extends RecyclerView.Adapter<CurrenciesAdapter.ViewHolder> {

    List<Currencies> currencies;

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

        // En fazla 6 karakterlik coin adı
        String name = currency.name;
        if (name.length() > 8) {
            name = name.substring(0, 8)+ "...";
        }
        holder.coinNameTextView.setText(name);

        holder.coinPriceTextView.setText(String.valueOf(currency.price_usd) + "$");
        holder.coinSymbolTextView.setText(currency.symbol);

        // İkonlar
        try {
            String fileName = "icons/" + currency.symbol.toLowerCase() + ".png";
            InputStream inputStream = holder.itemView.getContext().getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            holder.coinImageView.setImageBitmap(bitmap);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            holder.coinImageView.setImageResource(R.drawable.aboutus);
        }

        // Market Değeri
        String formattedMarketCap = formatCurrency(currency.market_cap_usd);
        holder.coinMarketCapTextView.setText(formattedMarketCap + "$");

        // Günlük Değişim
        holder.coinChangeTextView.setText(String.valueOf(currency.percent_change_24h) + "%");

        if(currency.percent_change_24h > 0)
            holder.coinChangeTextView.setTextColor(holder.itemView.getResources().getColor(R.color.green));
        else if(currency.percent_change_24h < 0)
            holder.coinChangeTextView.setTextColor(holder.itemView.getResources().getColor(R.color.red));
        else
            holder.coinChangeTextView.setTextColor(holder.itemView.getResources().getColor(R.color.white));
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