package com.example.kriptocep;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryFragment extends Fragment {

    private static final String ARG_COIN_ID = "coinID";
    private static final String ARG_COIN_NAME = "coinName";

    private int coinID;
    private String coinName;

    private TextView coinNameText;
    private RecyclerView recyclerView;
    private TransactionHistoryAdapter adapter;
    private List<WalletFragment.Transaction> transactionList;

    public static TransactionHistoryFragment newInstance(int coinID, String coinName) {
        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COIN_ID, coinID);
        args.putString(ARG_COIN_NAME, coinName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            coinID = getArguments().getInt(ARG_COIN_ID);
            coinName = getArguments().getString(ARG_COIN_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        coinNameText = view.findViewById(R.id.coinNameText);
        recyclerView = view.findViewById(R.id.transactionRecycler);

        coinNameText.setText(coinName + " Transactions");

        transactionList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(transactionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchTransactions();
    }

    private void fetchTransactions() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("transactions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (var doc : queryDocumentSnapshots) {
                        WalletFragment.Transaction tx = new WalletFragment.Transaction(
                                doc.getLong("coinID").intValue(), // dikkat!
                                doc.getString("type"),
                                doc.getDouble("amount"),
                                doc.getDouble("price"),
                                doc.getLong("timestamp")
                        );
                        transactionList.add(tx);
                    }
                    Collections.sort(transactionList, (tx1, tx2) -> Long.compare(tx2.timestamp, tx1.timestamp));
                    adapter.notifyDataSetChanged();
                });

    }
}
