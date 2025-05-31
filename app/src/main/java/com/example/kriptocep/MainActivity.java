package com.example.kriptocep;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.kriptocep.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // İlk Fragment
        replaceFragment(new HomeFragment());


        // Buton kontrolleri
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.nav_home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.nav_wallet:
                    replaceFragment(new WalletFragment());
                    break;
                case R.id.nav_transactions:
                    replaceFragment(new TransactionHistoryFragment());
                    break;

                case R.id.nav_aboutus:
                    replaceFragment(new AboutUsFragment());
                    break;
                case R.id.nav_profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }

            return true;
        });
    }

    // Fragment Değiştirme Metodu
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragentTransaction = fragmentManager.beginTransaction();
        fragentTransaction.replace(R.id.fragment_container, fragment);
        fragentTransaction.commit();
    }
}