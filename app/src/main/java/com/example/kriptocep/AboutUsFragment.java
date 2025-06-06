package com.example.kriptocep;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUsFragment extends Fragment {

    Button gizlilikBtn;
    Button iletisimBtn;
    Button sssBtn;
    ImageButton btnTwitter;
    ImageButton btnInstagram;
    ImageButton btnLinkedIn;
    TextView appVersion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Butonları bul
        gizlilikBtn = view.findViewById(R.id.gizlilikBtn);
        iletisimBtn = view.findViewById(R.id.iletisimBtn);
        sssBtn = view.findViewById(R.id.sssBtn);
        btnTwitter = view.findViewById(R.id.btnTwitter);
        btnInstagram = view.findViewById(R.id.btnInstagram);
        btnLinkedIn = view.findViewById(R.id.btnLinkedIn);
        appVersion = view.findViewById(R.id.appVersion);

        // Uygulama versiyonunu göster
        try {
            PackageInfo pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            String version = pInfo.versionName;
            appVersion.setText("Versiyon " + version);
        } catch (PackageManager.NameNotFoundException e) {
            appVersion.setText("Versiyon 1.0");
        }

        // Sosyal medya butonları
        btnTwitter.setOnClickListener(v -> openSocialMedia("https://twitter.com/kriptocep"));
        btnInstagram.setOnClickListener(v -> openSocialMedia("https://instagram.com/kriptocep"));
        btnLinkedIn.setOnClickListener(v -> openSocialMedia("https://linkedin.com/company/kriptocep"));

        // Diğer butonlar
        gizlilikBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), gizlilikActivity.class);
            startActivity(intent);
        });

        iletisimBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), iletisimActivity.class);
            startActivity(intent);
        });

        sssBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SSSActivity.class);
            startActivity(intent);
        });
    }

    private void openSocialMedia(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Bağlantı açılamadı", Toast.LENGTH_SHORT).show();
        }
    }
}
