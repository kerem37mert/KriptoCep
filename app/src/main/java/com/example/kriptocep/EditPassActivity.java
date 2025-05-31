package com.example.kriptocep;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditPassActivity extends AppCompatActivity {

    Toolbar toolbarEditPass;
    EditText editTextOldPass;
    EditText editTextNewPass1;
    EditText editTextNewPass2;
    Button btnEditPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_pass);

        toolbarEditPass = findViewById(R.id.toolbarEditPass);
        editTextOldPass = findViewById(R.id.editTextOldPass);
        editTextNewPass1 = findViewById(R.id.editTextNewPass1);
        editTextNewPass2 = findViewById(R.id.editTextNewPass2);
        btnEditPass = findViewById(R.id.btnEditPass);

        // Toolbar Geri Tuşu
        setSupportActionBar(toolbarEditPass);
        toolbarEditPass.setNavigationOnClickListener(v -> finish());

        getSupportActionBar().setDisplayShowTitleEnabled(false); // Uygulama ismini toolbarda gizleme

        btnEditPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = editTextOldPass.getText().toString().trim();
                String newPass1 = editTextNewPass1.getText().toString().trim();
                String newPass2 = editTextNewPass2.getText().toString().trim();

                if (oldPass.isEmpty() || newPass1.isEmpty() || newPass2.isEmpty()) {
                    // Uyarı mesajı
                    Toast.makeText(EditPassActivity.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPass1.equals(newPass2)) {
                    Toast.makeText(EditPassActivity.this, "Yeni şifreler uyuşmuyor", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null || user.getEmail() == null) {
                    Toast.makeText(EditPassActivity.this, "Kullanıcı oturumu bulunamadı", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);

                // Kimlik doğrulama
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if(!isPasswordValid(newPass1)) {
                            Toast.makeText(EditPassActivity.this, "Şifre en az 8 karakter, bir harf, bir rakam ve bir özel karakter içermelidir.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Şifre güncelleme
                        user.updatePassword(newPass1).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(EditPassActivity.this, "Şifre başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                                finish(); // Sayfayı kapatabilirsiniz
                            } else {
                                Toast.makeText(EditPassActivity.this, "Şifre güncellenemedi: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(EditPassActivity.this, "Eski şifre yanlış", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

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