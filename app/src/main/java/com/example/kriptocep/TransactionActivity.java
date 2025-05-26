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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        toolbarTransactionTitle.setText(intent.getStringExtra("symbol"));

        // İkon
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

        // Edittext placeholderları dinamik şekilde değiştirme
        editTransactionAmount.setHint("Adet (" + intent.getStringExtra("symbol") + ")");
        editTransactionPrice.setHint("Fiyat: (" + intent.getDoubleExtra("price", 0) + "$)");

        // Varsayılan Değer
        editTransactionPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && TextUtils.isEmpty(editTransactionPrice.getText()))
                    editTransactionPrice.setText(String.valueOf(intent.getDoubleExtra("price", 0)));
            }
        });


        // Toolbar Geri Tuşu
        setSupportActionBar(toolbarTransaction);
        toolbarTransaction.setNavigationOnClickListener(v -> finish());

        getSupportActionBar().setDisplayShowTitleEnabled(false); // Uygulama ismini toolbarda gizleme

        // Toolbar Değiştirme Butonu
        toolbarChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionActivity.this, SelectCoinActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //İşlem Ekleme
        addTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = editTransactionAmount.getText().toString().trim();
                String priceStr = editTransactionPrice.getText().toString().trim();

                if (amountStr.isEmpty() || priceStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountStr);
                double price = Double.parseDouble(priceStr);

                // Seçilem Tab (Buy/Sell)
                String transactionType;
                TabLayout.Tab selectedTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
                int position = selectedTab.getPosition();
                transactionType = position == 0 ? "buy" : "sell"; // İşlem tipi

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Kullanıcı idsi
                String uid = auth.getCurrentUser().getUid();

                Map<String, Object> transaction = new HashMap<>();
                transaction.put("coinID", intent.getIntExtra("id", 0));
                transaction.put("type", transactionType);
                transaction.put("amount", amount);
                transaction.put("price", price);
                transaction.put("timestamp", System.currentTimeMillis());

                // Firestore'a kaydetme
                db.collection("users")
                        .document(uid)
                        .collection("transactions")
                        .add(transaction)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(TransactionActivity.this, "İşlem başarıyla eklendi", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(TransactionActivity.this, "İşlem eklenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}