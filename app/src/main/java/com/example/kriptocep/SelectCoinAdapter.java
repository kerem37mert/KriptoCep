package com.example.kriptocep;

import android.content.Context;
import android.content.Intent;
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

public class SelectCoinAdapter extends RecyclerView.Adapter<SelectCoinAdapter.ViewHolder> {

    Context context;
    List<Currencies> currencies;

    public SelectCoinAdapter(Context context, List<Currencies> currencies) {
        this.currencies = currencies;
        this.context = context;
    }

    // Arama işleminde listeyi güncellemek için
    public void updateList(List<Currencies> newList) {
        this.currencies = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectCoinAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_coin_item, parent, false);
        return new SelectCoinAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectCoinAdapter.ViewHolder holder, int position) {
        Currencies currency = currencies.get(position);

        holder.selectCoinItemName.setText(currency.name);
        holder.selectCoinItemSymbol.setText(currency.symbol);

        // İkonlar
        try {
            String fileName = "icons/" + currency.symbol.toLowerCase() + ".png";
            InputStream inputStream = holder.itemView.getContext().getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            holder.selectCoinItemIcon.setImageBitmap(bitmap);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            holder.selectCoinItemIcon.setImageResource(R.drawable.aboutus);
        }

        // İtemlere Tıklama
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TransactionActivity.class);
                intent.putExtra("id", currency.id);
                intent.putExtra("name", currency.name);
                intent.putExtra("symbol", currency.symbol);

                context.startActivity(intent);
                ((SelectCoinActivity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return currencies != null ? currencies.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView selectCoinItemIcon;
        TextView selectCoinItemSymbol;
        TextView selectCoinItemName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selectCoinItemIcon = itemView.findViewById(R.id.selectCoinItemIcon);
            selectCoinItemSymbol = itemView.findViewById(R.id.selectCoinItemSymbol);
            selectCoinItemName = itemView.findViewById(R.id.selectCoinItemName);
        }
    }
}
