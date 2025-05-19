package com.example.kriptocep;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectCoinActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_coin);

        toolbar = findViewById(R.id.toolbar);

        // Tolbar Geri Tuşu
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        getSupportActionBar().setDisplayShowTitleEnabled(false); // Uygulama ismini toolbarda gizleme
    }
}