package com.example.kriptocep;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TransactionHistoryFragment extends Fragment implements TransactionHistoryAdapter.OnTransactionDeleteListener {

    private RecyclerView recyclerView;
    private TransactionHistoryAdapter adapter;
    private List<WalletFragment.Transaction> transactionList;
    private FirebaseFirestore db;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firebase ve kullanıcı ID'sini başlangıçta al
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // RecyclerView kurulumu
        recyclerView = view.findViewById(R.id.transactionRecycler);
        transactionList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(transactionList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Swipe-to-delete özelliğini ekle
        setupSwipeToDelete();

        // Status bar padding ayarı
        setupStatusBarPadding(view);

        // İşlemleri getir
        fetchTransactions();
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                WalletFragment.Transaction deletedTransaction = transactionList.get(position);
                adapter.removeItem(position);
                
                // Custom Snackbar
                Snackbar snackbar = Snackbar.make(recyclerView, "", Snackbar.LENGTH_LONG);
                View customView = getLayoutInflater().inflate(R.layout.custom_snackbar, null);
                
                // Snackbar'ın varsayılan view'ını kaldır
                @SuppressLint("RestrictedApi")
                Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                snackbarLayout.removeAllViews();
                
                // Custom view'ı ekle
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                );
                snackbarLayout.addView(customView, params);
                
                // Animasyon ekle
                Animation slideIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
                customView.startAnimation(slideIn);
                
                // Geri Al butonuna tıklama işlemi
                Button undoButton = customView.findViewById(R.id.snackbarAction);
                undoButton.setOnClickListener(v -> {
                    // Önce adapter'da geri yükle
                    adapter.restoreItem(deletedTransaction, position);
                    
                    // Firebase'e geri yükle
                    Map<String, Object> transaction = new HashMap<>();
                    transaction.put("coinID", deletedTransaction.coinID);
                    transaction.put("type", deletedTransaction.type);
                    transaction.put("amount", deletedTransaction.amount);
                    transaction.put("price", deletedTransaction.price);
                    transaction.put("timestamp", deletedTransaction.timestamp);

                    db.collection("users")
                      .document(uid)
                      .collection("transactions")
                      .add(transaction)
                      .addOnSuccessListener(documentReference -> {
                          // Başarılı
                      })
                      .addOnFailureListener(e -> {
                          // Hata durumunda kullanıcıya bilgi ver
                          if (getView() != null) {
                              Snackbar.make(getView(), "İşlem geri yüklenirken hata oluştu", Snackbar.LENGTH_SHORT).show();
                          }
                      });
                    
                    snackbar.dismiss();
                });
                
                // Snackbar'ı göster
                snackbar.show();
                
                // Snackbar'ın arka planını şeffaf yap
                snackbarLayout.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                
                // Arka plan rengini ayarla
                Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.delete_background);
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // Silme ikonunu çiz
                Drawable deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete);
                if (deleteIcon != null) {
                    int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                    int iconRight = itemView.getRight() - iconMargin;
                    int iconLeft = iconRight - deleteIcon.getIntrinsicWidth();
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    deleteIcon.draw(c);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setupStatusBarPadding(View view) {
        ConstraintLayout rootLayout = view.findViewById(R.id.transactionLayout);
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        rootLayout.setPadding(
            rootLayout.getPaddingLeft(),
            statusBarHeight + rootLayout.getPaddingTop(),
            rootLayout.getPaddingRight(),
            rootLayout.getPaddingBottom()
        );
    }

    private void fetchTransactions() {
        db.collection("users")
          .document(uid)
          .collection("transactions")
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> {
              transactionList.clear(); // Listeyi temizle
              for (var doc : queryDocumentSnapshots) {
                  WalletFragment.Transaction tx = new WalletFragment.Transaction(
                      doc.getLong("coinID").intValue(),
                      doc.getString("type"),
                      doc.getDouble("amount"),
                      doc.getDouble("price"),
                      doc.getLong("timestamp")
                  );
                  transactionList.add(tx);
              }
              Collections.sort(transactionList, (tx1, tx2) -> Long.compare(tx2.timestamp, tx1.timestamp));
              adapter.notifyDataSetChanged();
          })
          .addOnFailureListener(e -> {
              // Hata durumunda kullanıcıya bilgi ver
              if (getView() != null) {
                  Snackbar.make(getView(), "İşlemler yüklenirken hata oluştu", Snackbar.LENGTH_LONG).show();
              }
          });
    }

    @Override
    public void onTransactionDelete(WalletFragment.Transaction transaction) {
        // Firestore'dan silme işlemi
        db.collection("users")
          .document(uid)
          .collection("transactions")
          .whereEqualTo("timestamp", transaction.timestamp)
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> {
              if (!queryDocumentSnapshots.isEmpty()) {
                  queryDocumentSnapshots.getDocuments().get(0).getReference().delete();
              }
          });
    }
}
