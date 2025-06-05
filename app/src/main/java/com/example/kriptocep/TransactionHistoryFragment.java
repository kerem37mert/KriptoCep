package com.example.kriptocep;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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

    private RecyclerView recyclerView;
    private TransactionHistoryAdapter adapter;
    private List<WalletFragment.Transaction> transactionList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.transactionRecycler);

        transactionList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(transactionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchTransactions();

        ConstraintLayout rootLayout = view.findViewById(R.id.transactionLayout); // Eğer id yoksa ekleyelim

        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        rootLayout.setPadding(
                rootLayout.getPaddingLeft(),
                statusBarHeight + rootLayout.getPaddingTop(), // üst padding + varsa mevcut padding
                rootLayout.getPaddingRight(),
                rootLayout.getPaddingBottom()
        );
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
