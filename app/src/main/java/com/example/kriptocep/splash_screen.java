package com.example.kriptocep;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splash_screen extends AppCompatActivity {

    ImageView splashImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        splashImg = findViewById(R.id.splashImg);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        splashImg.startAnimation(animation);

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                Intent intent = new Intent(splash_screen.this, MainActivity.class); // Ana ekran
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(splash_screen.this, LoginActivity.class);
                startActivity(intent);
                finish(); //
            }
        }, 2000);
    }
}