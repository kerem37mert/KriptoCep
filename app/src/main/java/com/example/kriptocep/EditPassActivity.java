package com.example.kriptocep;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditPassActivity extends AppCompatActivity {

    Toolbar toolbarEditPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_pass);

        toolbarEditPass = findViewById(R.id.toolbarEditPass);

        // Toolbar Geri Tuşu
        setSupportActionBar(toolbarEditPass);
        toolbarEditPass.setNavigationOnClickListener(v -> finish());

        getSupportActionBar().setDisplayShowTitleEnabled(false); // Uygulama ismini toolbarda gizleme
    }
}