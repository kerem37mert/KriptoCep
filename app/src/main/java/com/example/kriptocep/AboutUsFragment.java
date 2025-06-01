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

    Button gizlilikBtn;;
    Button iletisimBtn;
    Button sssBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gizlilikBtn = view.findViewById(R.id.gizlilikBtn);
        iletisimBtn = view.findViewById(R.id.iletisimBtn);
        sssBtn = view.findViewById(R.id.sssBtn);

        gizlilikBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), gizlilikActivity.class);
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

        sssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SSSActivity.class);
                startActivity(intent);
            }
        });
    }
}
