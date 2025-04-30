package com.example.kriptocep;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    Button btn;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        btn = findViewById(R.id.btn);
        bottomNavigationView = findViewById(R.id.bottom_navigation); // XML'deki BottomNavigationView

        btn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // BottomNavigation item seçme olayı
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_wallet) {
                Intent intent2 = new Intent(HomeActivity.this, WalletActivity.class);
                startActivity(intent2);
                return true;
            } else if (itemId == R.id.nav_aboutus) {
                Intent intent3 = new Intent(HomeActivity.this, AboutUsActivity.class);
                startActivity(intent3);
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent3 = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent3);
                return true;
            }
            return false;
        });
    }
}
