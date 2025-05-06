package com.example.kriptocep;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            // Kullanıcıyı kontrol et
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                // Eğer kullanıcı giriş yaptıysa, Ana sayfaya yönlendir
                Intent intent = new Intent(splash_screen.this, MainActivity.class); // Ana ekran
                startActivity(intent);
                finish(); // Giriş ekranını kapat
            } else {
                // Eğer kullanıcı giriş yapmamışsa, Giriş ekranına yönlendir
                Intent intent = new Intent(splash_screen.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Ana ekranı kapat
            }
        }, 3500);
    }
}