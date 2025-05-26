package com.example.kriptocep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText registerEditEmail;
    EditText registerEditPass;
    EditText registerEditPass2;
    Button registerBtn;
    TextView registerTextLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        registerTextLogin = findViewById(R.id.registerTextLogin);
        registerBtn = findViewById(R.id.registerBtn);
        registerEditEmail = findViewById(R.id.registerEditEmail);
        registerEditPass = findViewById(R.id.registerEditPass);
        registerEditPass2 = findViewById(R.id.registerEditPass2);

        // Giriş Sayfasına Gitmek İçin
        registerTextLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Kayıt Ol butonuna basılınca
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                String emailStr = registerEditEmail.getText().toString();
                String passStr = registerEditPass.getText().toString();
                String passStr2 = registerEditPass2.getText().toString();

                if (emailStr.isEmpty() || passStr.isEmpty() || passStr2.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Boş Alanları Doldurunuz", Toast.LENGTH_SHORT).show();
                    return;
                }

                // E-posta geçerliliğini kontrol et
                if (!isEmailValid(emailStr)) {
                    Toast.makeText(RegisterActivity.this, "Geçersiz e-posta adresi.", Toast.LENGTH_SHORT).show();
                }
                // Şifrelerin eşleşip eşleşmediğini kontrol et
                else if (!passStr.equals(passStr2)) {
                    Toast.makeText(RegisterActivity.this, "Şifreler Eşleşmiyor", Toast.LENGTH_SHORT).show();
                }
                // Parola geçerliliği kontrolü
                else if (!isPasswordValid(passStr)) {
                    Toast.makeText(RegisterActivity.this, "Şifre en az 8 karakter, bir harf, bir rakam ve bir özel karakter içermelidir.", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Kayıt Başarılı
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Kayıt Hatası
                                    Exception e = task.getException();
                                    e.printStackTrace();
                                }
                            });
                }
            }
        });
    }

    // E-posta geçerliliğini kontrol eden metod
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Şifre geçerliliğini kontrol eden metod
    private boolean isPasswordValid(String password) {
        // Şifrenin 8 karakterden uzun olup olmadığını kontrol et
        if (password.length() < 8) {
            return false;
        }

        // En az bir harf, bir rakam ve bir özel karakter içermesi gerekmekte
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[.,@#$!%*?&])[A-Za-z\\d.,@#$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }
}
