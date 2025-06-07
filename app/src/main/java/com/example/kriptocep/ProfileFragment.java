package com.example.kriptocep;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    Retrofit retrofit;
    String baseURL = "https://api.coinlore.net/api/";
    Map<Integer, List<WalletFragment.Transaction>> coinTransactionMap;
    double totalBalance = 0.0;
    int apiResponsesReceived = 0;

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
        coinTransactionMap = new HashMap<>();

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
              coinTransactionMap.clear();
              for (var doc : queryDocumentSnapshots) {
                  WalletFragment.Transaction tx = new WalletFragment.Transaction(
                      doc.getLong("coinID").intValue(),
                      doc.getString("type"),
                      doc.getDouble("amount"),
                      doc.getDouble("price"),
                      doc.getLong("timestamp")
                  );
                  if (!coinTransactionMap.containsKey(doc.getLong("coinID").intValue())) {
                      coinTransactionMap.put(doc.getLong("coinID").intValue(), new ArrayList<>());
                  }
                  coinTransactionMap.get(doc.getLong("coinID").intValue()).add(tx);
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
        int totalTransactions = 0;
        for (List<WalletFragment.Transaction> transactions : coinTransactionMap.values()) {
            totalTransactions += transactions.size();
        }
        textViewTotalTransactions.setText(String.valueOf(totalTransactions));

        // Toplam bakiye ve coin hesaplama
        Map<Integer, Double> coinBalances = new HashMap<>();
        totalBalance = 0.0;
        apiResponsesReceived = 0;

        for (Map.Entry<Integer, List<WalletFragment.Transaction>> entry : coinTransactionMap.entrySet()) {
            double coinBalance = 0.0;
            int coinID = entry.getKey();
            List<WalletFragment.Transaction> transactions = entry.getValue();

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(baseURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }

            CurrencyAPI currencyAPI = retrofit.create(CurrencyAPI.class);
            Call<List<Currencies>> call = currencyAPI.getCurrency(coinID);

            call.enqueue(new Callback<List<Currencies>>() {
                @Override
                public void onResponse(Call<List<Currencies>> call, Response<List<Currencies>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Currencies currency = response.body().get(0);

                        double totalBuyAmount = 0.0;
                        double totalBuyCost = 0.0;
                        double totalSellAmount = 0.0;

                        for (WalletFragment.Transaction tx : transactions) {
                            if (tx.type.equals("buy")) {
                                totalBuyAmount += tx.amount;
                                totalBuyCost += tx.amount * tx.price;
                            } else if (tx.type.equals("sell")) {
                                totalSellAmount += tx.amount;
                            }
                        }

                        double netAmount = totalBuyAmount - totalSellAmount;
                        if (netAmount < 0) netAmount = 0;

                        double currentValue = netAmount * currency.price_usd;
                        totalBalance += currentValue;

                        if (netAmount > 0) {
                            coinBalances.put(coinID, netAmount);
                        }

                        apiResponsesReceived++;
                        if (apiResponsesReceived == coinTransactionMap.size()) {
                            if (!isAdded() || getActivity() == null || getView() == null) return;

                            // Toplam coin sayısı (sıfırdan büyük bakiyesi olan coinler)
                            textViewTotalCoins.setText(String.valueOf(coinBalances.size()));
                            textViewTotalBalance.setText("$" + formatCurrency(totalBalance));
                        }
                    } else {
                        Log.e("API ERROR", "Başarısız yanıt veya boş veri girişi.");
                        apiResponsesReceived++;
                        if (apiResponsesReceived == coinTransactionMap.size()) {
                            if (!isAdded() || getActivity() == null || getView() == null) return;
                            textViewTotalCoins.setText(String.valueOf(coinBalances.size()));
                            textViewTotalBalance.setText("$" + formatCurrency(totalBalance));
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Currencies>> call, Throwable t) {
                    Log.e("API ERROR", t.getMessage());
                    apiResponsesReceived++;
                    if (apiResponsesReceived == coinTransactionMap.size()) {
                        if (!isAdded() || getActivity() == null || getView() == null) return;
                        textViewTotalCoins.setText(String.valueOf(coinBalances.size()));
                        textViewTotalBalance.setText("$" + formatCurrency(totalBalance));
                    }
                }
            });
        }
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
