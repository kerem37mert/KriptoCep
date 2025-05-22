package com.example.kriptocep;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AboutUsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        Button adresBtn = view.findViewById(R.id.adresBtn);
        Button hakkimizdaBtn = view.findViewById(R.id.hakkimizdaBtn);
        Button iletisimBtn = view.findViewById(R.id.iletisimBtn);

        adresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), adres.class);
                startActivity(intent);
            }
        });

        hakkimizdaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), hakkimizdaActivity.class);
                startActivity(intent);
            }
        });

        iletisimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), iletisimActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
