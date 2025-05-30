package com.example.kriptocep;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String uid;
    Toolbar toolbarEditProfile;
    EditText editTextName;
    EditText editTextEmail;
    EditText editTextDate;
    Button btnProfileUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = auth.getCurrentUser().getUid();

        toolbarEditProfile = findViewById(R.id.toolbarEditProfile);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextDate = findViewById(R.id.editTextDate);
        btnProfileUpdate = findViewById(R.id.btnProfileUpdate);

        if(editTextName.getText().toString().isEmpty()) {
            editTextName.setHint("İsim Giriniz");
        }

        if(editTextDate.getText().toString().isEmpty()) {
            editTextDate.setHint("Tarih Seçiniz");
        }

        // Toolbar Geri Tuşu
        setSupportActionBar(toolbarEditProfile);
        toolbarEditProfile.setNavigationOnClickListener(v -> finish());

        getSupportActionBar().setDisplayShowTitleEnabled(false); // Uygulama ismini toolbarda gizleme

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Şu anki tarihi al (başlangıç için)
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // DatePicker dialogunu oluştur
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditProfileActivity.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // Tarih seçildiğinde EditText'e yaz
                            // Not: Aylar 0'dan başlar, bu yüzden +1 yapıyoruz
                            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            editTextDate.setText(selectedDate);
                        },
                        year, month, day
                );

                // DatePicker'ı göster
                datePickerDialog.show();
            }
        });

        btnProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String birthDate = editTextDate.getText().toString().trim();

                if(email.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "E-posta boş olamaz", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(EditProfileActivity.this, "Geçersiz e-posta adresi", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1. E-Postayı Firebase Authentication'da güncelle
                auth.getCurrentUser().updateEmail(email)
                        .addOnSuccessListener(unused -> {

                            // 2. Firestore'da kullanıcı bilgilerini güncelle
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("birthDate", birthDate);

                            db.collection("users")
                                    .document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(EditProfileActivity.this, "Profil güncellendi", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        Toast.makeText(EditProfileActivity.this, "Firestore güncellemesi başarısız", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            Toast.makeText(EditProfileActivity.this, "E-posta güncellenemedi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });

        getUserInfo();
    }

    public void getUserInfo() {
        // Auth üzerinden e-posta al
        String email = auth.getCurrentUser().getEmail();
        editTextEmail.setText(email);

        // Firestore'dan kalan bilgileri al
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String birthDate = documentSnapshot.getString("birthDate");

                        editTextName.setText(name);
                        editTextDate.setText(birthDate);
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }
}