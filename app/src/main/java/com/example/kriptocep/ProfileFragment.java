package com.example.kriptocep;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        TextView mailText = view.findViewById(R.id.mailText);
        TextView nameText = view.findViewById(R.id.nameText);      // sadece gösterim
        EditText editName = view.findViewById(R.id.editName);      // gizli, düzenleme için
        Button editNameBtn = view.findViewById(R.id.editNameBtn);  // düzenleme butonu

        // Firebase'den mail al
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            mailText.setText(email);
        }

        SharedPreferences preferences = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        String savedName = preferences.getString("username", "");

        nameText.setText(savedName);
        editName.setVisibility(View.GONE);

        // Düzenle butonuna basınca EditText açılır
        editNameBtn.setOnClickListener(v -> {
            nameText.setVisibility(View.GONE);
            editName.setVisibility(View.VISIBLE);
            editName.setText(nameText.getText().toString());
            editName.requestFocus();
        });

        // EditText'te Enter (Done) tuşuna basılınca kaydet
        editName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String name = editName.getText().toString().trim();
                if (!name.isEmpty()) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", name);
                    editor.apply();
                    nameText.setText(name);
                    Toast.makeText(getContext(), "İsim güncellendi", Toast.LENGTH_SHORT).show();
                }

                // KLAVYEYİ GİZLE
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);

                editName.setVisibility(View.GONE);
                nameText.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        // Oturum kapatma
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}
