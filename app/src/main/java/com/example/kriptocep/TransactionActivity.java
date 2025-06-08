package com.example.kriptocep;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionActivity extends AppCompatActivity {

    Intent intent;
    Toolbar toolbarTransaction;
    TextView toolbarTransactionTitle;
    ImageView toolbarTransactionIcon;
    Button toolbarChangeButton;
    EditText editTransactionAmount;
    EditText editTransactionPrice;
    Button addTransactionBtn;
    TabLayout tabLayout;

    int coinID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction);

        intent = getIntent();

        toolbarTransaction = findViewById(R.id.toolbarTransaction);
        toolbarTransactionTitle = findViewById(R.id.toolbarTransactionTitle);
        toolbarTransactionIcon = findViewById(R.id.toolbarTransacionIcon);
        toolbarChangeButton = findViewById(R.id.toolbarChangeButton);
        editTransactionAmount = findViewById(R.id.editTransactionAmount);
        editTransactionPrice = findViewById(R.id.editTransactionPrice);
        addTransactionBtn = findViewById(R.id.addTransactionBtn);
        tabLayout = findViewById(R.id.tabLayout);

        coinID = intent.getIntExtra("id", 0);
        fetchUpdatedPrice(coinID);
        toolbarTransactionTitle.setText(intent.getStringExtra("symbol"));

        try {
            String fileName = "icons/" + intent.getStringExtra("symbol").toLowerCase() + ".png";
            InputStream inputStream = getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            toolbarTransactionIcon.setImageBitmap(bitmap);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            toolbarTransactionIcon.setImageResource(R.drawable.aboutus);
        }

        editTransactionAmount.setHint("Adet (" + intent.getStringExtra("symbol") + ")");
        editTransactionPrice.setHint("Fiyat: ($" + intent.getDoubleExtra("price", 0) + ")");
        editTransactionPrice.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && TextUtils.isEmpty(editTransactionPrice.getText()))
                editTransactionPrice.setText(String.valueOf(intent.getDoubleExtra("price", 0)));
        });

        setSupportActionBar(toolbarTransaction);
        toolbarTransaction.setNavigationOnClickListener(v -> finish());//Geri tuşuna basıldığında finish() çalışır
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Coin Değiştirme Butonu
        toolbarChangeButton.setOnClickListener(v -> {
            Intent intent = new Intent(TransactionActivity.this, SelectCoinActivity.class);
            startActivity(intent);
            finish();
        });

        addTransactionBtn.setOnClickListener(v -> {
            String amountStr = editTransactionAmount.getText().toString().trim();
            String priceStr = editTransactionPrice.getText().toString().trim();

            if (amountStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            double price = Double.parseDouble(priceStr);

            TabLayout.Tab selectedTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
            String transactionType = selectedTab.getPosition() == 0 ? "buy" : "sell";

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = auth.getCurrentUser().getUid();

            if (transactionType.equals("sell")) {
                db.collection("users")
                        .document(uid)
                        .collection("transactions")
                        .whereEqualTo("coinID", coinID)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            double netAmount = 0.0;
                            for (var doc : queryDocumentSnapshots.getDocuments()) {
                                String type = doc.getString("type");
                                double amt = doc.getDouble("amount");

                                if (type.equals("buy")) {
                                    netAmount += amt;
                                } else if (type.equals("sell")) {
                                    netAmount -= amt;
                                }
                            }

                            if (amount > netAmount) {
                                Toast.makeText(TransactionActivity.this, "Elinizde yeterli miktarda coin yok", Toast.LENGTH_SHORT).show();
                            } else {
                                saveTransaction(db, uid, coinID, transactionType, amount, price);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(TransactionActivity.this, "Geçmiş işlemler alınamadı", Toast.LENGTH_SHORT).show();
                        });
            } else {
                saveTransaction(db, uid, coinID, transactionType, amount, price);
            }
        });
    }

    private void fetchUpdatedPrice(int coinID) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coinlore.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CurrenciesAPI api = retrofit.create(CurrenciesAPI.class);
        api.getCurrencies().enqueue(new Callback<List<Currencies>>() {
            @Override
            public void onResponse(Call<List<Currencies>> call, Response<List<Currencies>> response) {
                if (response.isSuccessful()) {
                    for (Currencies c : response.body()) {
                        if (c.id == coinID) {
                            editTransactionPrice.setHint("Fiyat: ($" + c.price_usd + ")");
                            editTransactionPrice.setOnFocusChangeListener((v, hasFocus) -> {
                                if (hasFocus && TextUtils.isEmpty(editTransactionPrice.getText())) {
                                    editTransactionPrice.setText(String.valueOf(c.price_usd));
                                }
                            });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Currencies>> call, Throwable t) {
                Toast.makeText(TransactionActivity.this, "Fiyat alınamadı", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Coin Alma
    private void saveTransaction(FirebaseFirestore db, String uid, int coinID, String type, double amount, double price) {
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("coinID", coinID);
        transaction.put("type", type);
        transaction.put("amount", amount);
        transaction.put("price", price);
        transaction.put("timestamp", System.currentTimeMillis());

        db.collection("users")
                .document(uid)
                .collection("transactions")
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(TransactionActivity.this, "İşlem başarıyla eklendi", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TransactionActivity.this, "İşlem eklenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                });
    }
}
