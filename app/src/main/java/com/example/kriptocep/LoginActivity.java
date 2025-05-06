package com.example.kriptocep;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText loginEditEmail;
    EditText loginEditPass;
    Button loginBtn;
    TextView loginTextRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginTextRegister = findViewById(R.id.loginTextRegister);
        loginBtn = findViewById(R.id.loginBtn);
        loginEditEmail = findViewById(R.id.LoginEditEmail);
        loginEditPass = findViewById(R.id.loginEditPass);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Kayıt sayfasına gitmek için
        loginTextRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailStr = loginEditEmail.getText().toString();
                String passwordStr = loginEditPass.getText().toString();

                if (emailStr.isEmpty() || passwordStr.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth mAuth = FirebaseAuth.getInstance();

// Giriş Yap
                mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Giriş Başarılı
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Giriş Hatası
                                Toast.makeText(LoginActivity.this, "Kullanıcı hesabı bulunamadı", Toast.LENGTH_SHORT).show();

                                // Titreşim
                                if (vibrator != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        VibrationEffect effect = VibrationEffect.createOneShot(750, VibrationEffect.DEFAULT_AMPLITUDE);
                                        vibrator.vibrate(effect);
                                    } else {
                                        vibrator.vibrate(500);
                                    }
                                }
                                Exception e = task.getException();
                                e.printStackTrace();
                            }
                        });

            }
        });

    }
}