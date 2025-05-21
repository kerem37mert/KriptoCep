package com.example.kriptocep;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;

public class TransactionActivity extends AppCompatActivity {
    Intent intent;
    Toolbar toolbarTransaction;
    TextView toolbarTransactionTitle;
    ImageView toolbarTransactionIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction);

        intent = getIntent();

        toolbarTransaction = findViewById(R.id.toolbarTransaction);
        toolbarTransactionTitle = findViewById(R.id.toolbarTransactionTitle);
        toolbarTransactionIcon = findViewById(R.id.toolbarTransacionIcon);

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


        // Toolbar Geri Tuşu
        setSupportActionBar(toolbarTransaction);
        toolbarTransaction.setNavigationOnClickListener(v -> finish());

        getSupportActionBar().setDisplayShowTitleEnabled(false); // Uygulama ismini toolbarda gizleme

    }
}