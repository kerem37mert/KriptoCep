package com.example.kriptocep;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String uid;
    Button btnEditProfile;
    Button btnChangePass;
    Button btnLogout;
    TextView textViewUserName;
    TextView textViewTotalTransactions;
    TextView textViewTotalBalance;
    TextView textViewTotalCoins;
    ImageView imageProfile;
    ArrayList<WalletFragment.Transaction> transactionList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = auth.getCurrentUser().getUid();
        transactionList = new ArrayList<>();

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePass = view.findViewById(R.id.btnChangePass);
        btnLogout = view.findViewById(R.id.btnLogout);
        textViewUserName = view.findViewById(R.id.textViewUserName);
        textViewTotalTransactions = view.findViewById(R.id.textViewTotalTransactions);
        textViewTotalBalance = view.findViewById(R.id.textViewTotalBalance);
        textViewTotalCoins = view.findViewById(R.id.textViewTotalCoins);
        imageProfile = view.findViewById(R.id.imageProfile);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditPassActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        getUserInfo();
        fetchTransactions();
    }

    public void onResume() {
        super.onResume();
        getUserInfo();
        fetchTransactions();
    }

    private void fetchTransactions() {
        db.collection("users")
          .document(uid)
          .collection("transactions")
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> {
              transactionList.clear();
              for (var doc : queryDocumentSnapshots) {
                  WalletFragment.Transaction tx = new WalletFragment.Transaction(
                      doc.getLong("coinID").intValue(),
                      doc.getString("type"),
                      doc.getDouble("amount"),
                      doc.getDouble("price"),
                      doc.getLong("timestamp")
                  );
                  transactionList.add(tx);
              }
              updateStatistics();
          })
          .addOnFailureListener(e -> {
              if (getView() != null) {
                  Snackbar.make(getView(), "İşlemler yüklenirken hata oluştu", Snackbar.LENGTH_LONG).show();
              }
          });
    }

    private void updateStatistics() {
        // Toplam işlem sayısı
        textViewTotalTransactions.setText(String.valueOf(transactionList.size()));

        // Toplam bakiye ve coin hesaplama
        double totalBalance = 0.0;
        Map<Integer, Double> coinBalances = new HashMap<>();

        for (WalletFragment.Transaction tx : transactionList) {
            double amount = tx.amount;
            if (tx.type.equals("SELL")) {
                amount = -amount;
            }
            
            // Coin bakiyesini güncelle
            coinBalances.put(tx.coinID, coinBalances.getOrDefault(tx.coinID, 0.0) + amount);
            
            // Toplam bakiyeyi güncelle
            totalBalance += amount * tx.price;
        }

        // Toplam coin sayısı (sıfırdan büyük bakiyesi olan coinler)
        long totalCoins = coinBalances.values().stream()
                .filter(balance -> balance > 0)
                .count();

        textViewTotalBalance.setText(formatCurrency(totalBalance));
        textViewTotalCoins.setText(String.valueOf(totalCoins));
    }

    public void getUserInfo() {
        // Profil resmini yükle
        File file = new File(requireContext().getFilesDir(), "profile.png");
        if (file.exists()) {
            Glide.with(this)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .circleCrop()
                    .into(imageProfile);
        } else {
            imageProfile.setImageResource(R.drawable.user);
        }

        // Kullanıcı bilgilerini yükle
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        textViewUserName.setText(name != null ? name : "İsimsiz Kullanıcı");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Kullanıcı bilgileri yüklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

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
}
