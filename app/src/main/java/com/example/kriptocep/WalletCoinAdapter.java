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

public class WalletCoinAdapter extends RecyclerView.Adapter<WalletCoinAdapter.ViewHolder> {

    List<WalletCoinItem> walletList;

    public WalletCoinAdapter(List<WalletCoinItem> walletList) {
        this.walletList = walletList;
    }

    @NonNull
    @Override
    public WalletCoinAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_coin_item, parent, false);
        return new WalletCoinAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletCoinAdapter.ViewHolder holder, int position) {
        WalletCoinItem item = walletList.get(position);

        holder.walletCoinItemSymbol.setText(item.coinSymbol);
        holder.walletCoinItemValue.setText(String.format("%.2f $", item.currentValue));

        //İkonlar
        try {
            String fileName = "icons/" + item.coinSymbol.toLowerCase() + ".png";
            InputStream inputStream = holder.itemView.getContext().getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            holder.walletCoinItemIcon.setImageBitmap(bitmap);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            holder.walletCoinItemIcon.setImageResource(R.drawable.aboutus);
        }

        // Profit
        holder.walletCoinItemProfit.setText(String.format("%.2f$", item.profit));
        if(item.profit >= 0) {
            holder.walletCoinItemProfit.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        } else {
            holder.walletCoinItemProfit.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return walletList != null ? walletList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView walletCoinItemIcon;
        TextView walletCoinItemSymbol;
        TextView walletCoinItemValue;
        TextView walletCoinItemProfit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            walletCoinItemIcon = itemView.findViewById(R.id.walletCoinItemIcon);
            walletCoinItemSymbol = itemView.findViewById(R.id.walletCoinItemSymbol);
            walletCoinItemValue = itemView.findViewById(R.id.walletCoinItemValue);
            walletCoinItemProfit = itemView.findViewById(R.id.walletCoinItemProfit);
        }
    }
}
