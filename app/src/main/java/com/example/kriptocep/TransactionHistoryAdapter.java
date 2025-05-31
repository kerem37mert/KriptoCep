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

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {

    List<WalletFragment.Transaction> transactions;

    public TransactionHistoryAdapter(List<WalletFragment.Transaction> transactions) {
        this.transactions = transactions;
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
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type, amount, price, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.txType);
            amount = itemView.findViewById(R.id.txAmount);
            price = itemView.findViewById(R.id.txPrice);
            date = itemView.findViewById(R.id.txDate);
        }
    }
}
