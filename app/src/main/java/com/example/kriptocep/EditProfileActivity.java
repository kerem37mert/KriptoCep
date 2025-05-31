package com.example.kriptocep;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String uid;
    Toolbar toolbarEditProfile;
    ImageView imageEditProfile;
    EditText editTextName;
    EditText editTextEmail;
    EditText editTextDate;
    Button btnProfileUpdate;
    android.net.Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = auth.getCurrentUser().getUid();

        toolbarEditProfile = findViewById(R.id.toolbarEditProfile);
        imageEditProfile = findViewById(R.id.imageEditProfile);
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

        imageEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1000);
            }
        });

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

                if (selectedImageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        saveImageToInternalStorage(bitmap, "profile.png");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(EditProfileActivity.this, "Fotoğraf kaydedilemedi", Toast.LENGTH_SHORT).show();
                    }
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

        loadProfileImage();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            // Seçilen resmin URI'sini al
            selectedImageUri = data.getData();

            // ImageView'da göster
            imageEditProfile.setImageURI(selectedImageUri);
        }
    }

    public String saveImageToInternalStorage(Bitmap bitmap, String fileName) {
        File directory = EditProfileActivity.this.getFilesDir(); // Internal storage: /data/data/your.package.name/files/
        File file = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return file.getAbsolutePath(); // Dilersen yol da döndürülür
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void loadProfileImage() {
        File file = new File(getFilesDir(), "profile.png");

        if (file.exists()) {
            // Glide ile dosyadan resmi yükle
            Glide.with(this)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageEditProfile);
        } else {
            // Default profil resmi göster
            imageEditProfile.setImageResource(R.drawable.user);
        }
    }
}