package com.example.kriptocep;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class gizlilikActivity extends AppCompatActivity {

    Toolbar toolbarHakkimizda;
    TextView myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gizlilik);

        toolbarHakkimizda = findViewById(R.id.toolbarHakkimizda);
        myTextView = findViewById(R.id.myTextView);
        myTextView.setMovementMethod(new ScrollingMovementMethod());

        String gizlilikMetni =
                "Son güncelleme tarihi: 1 Haziran 2025\n\n" +
                "Bu gizlilik politikası, KriptoCep uygulamasının kullanıcılarından topladığı bilgileri, " +
                "bu bilgilerin nasıl kullanıldığını, korunduğunu ve paylaşıldığını açıklamaktadır. Uygulamayı kullanarak, " +
                "bu gizlilik politikasında belirtilen şartları kabul etmiş olursunuz.\n\n" +

                "1. TOPLANAN BİLGİLER\n\n" +
                "Uygulamamızı kullandığınızda sizden bazı kişisel ve kişisel olmayan veriler toplayabiliriz. " +
                "Bunlar aşağıdakileri içerebilir:\n" +
                "- Cihaz bilgileri (model, işletim sistemi, dil ve ülke bilgisi)\n" +
                "- Uygulama kullanım verileri (hangi sayfalarda ne kadar zaman geçirildi, tıklama bilgileri vb.)\n" +
                "- IP adresi ve oturum bilgileri\n" +
                "- İsteğe bağlı olarak ilettiğiniz geri bildirim veya destek talepleri\n\n" +

                "2. BİLGİLERİN KULLANIMI\n\n" +
                "Topladığımız bilgiler aşağıdaki amaçlarla kullanılabilir:\n" +
                "- Uygulamanın işlevselliğini sağlamak ve geliştirmek\n" +
                "- Teknik sorunları analiz etmek ve çözmek\n" +
                "- Kullanıcı deneyimini iyileştirmek\n" +
                "- Geri bildirimleri değerlendirmek\n" +
                "- Yasal yükümlülükleri yerine getirmek\n\n" +

                "3. ÜÇÜNCÜ TARAF HİZMETLERİ\n\n" +
                "Uygulama bazı üçüncü taraf servis sağlayıcılarını kullanabilir. Bu hizmet sağlayıcılar, " +
                "kendi gizlilik politikalarına tabi olarak bilgi toplayabilir:\n" +
                "- Google Play Hizmetleri\n" +
                "- Firebase (analiz, bildirim gönderimi)\n" +
                "- Crashlytics (uygulama çökme raporları)\n\n" +
                "Bu hizmet sağlayıcıların gizlilik politikalarını incelemeniz önerilir.\n\n" +

                "4. VERİLERİN GÜVENLİĞİ\n\n" +
                "Toplanan verilerin güvenliği bizim için önemlidir. Bu nedenle veriler, " +
                "yetkisiz erişime karşı koruma sağlamak amacıyla güvenli yöntemlerle saklanır. " +
                "Bununla birlikte, internet üzerinden yapılan hiçbir veri aktarımının %100 güvenli olacağı garanti edilemez.\n\n" +

                "5. VERİLERE ERİŞİM VE KONTROL\n\n" +
                "Kullanıcılar, kendileriyle ilgili verilerin hangi amaçlarla kullanıldığını öğrenme, " +
                "gerekli durumlarda bu verileri silme veya düzelttirme hakkına sahiptir. " +
                "Bu tür talepler için bizimle iletişime geçebilirsiniz: destek@ornekuygulama.com\n\n" +

                "6. ÇOCUKLARIN GİZLİLİĞİ\n\n" +
                "Uygulamamız 13 yaş altındaki çocuklara yönelik değildir. Bu yaş grubuna ait olduğunuz tespit edilirse, " +
                "verileriniz derhal silinecektir. Ebeveynler çocuklarının izinsiz veri paylaştığını düşünüyorsa " +
                "bizimle iletişime geçmelidir.\n\n" +

                "7. DEĞİŞİKLİKLER VE GÜNCELLEMELER\n\n" +
                "Gizlilik politikamız zaman zaman güncellenebilir. Bu değişiklikler uygulama içerisinde veya web sitemizde duyurulur. " +
                "Yapılan değişiklikleri düzenli olarak kontrol etmeniz önerilir.\n\n";

        myTextView.setText(gizlilikMetni);

        // Toolbar Geri Tuşu
        setSupportActionBar(toolbarHakkimizda);
        toolbarHakkimizda.setNavigationOnClickListener(v -> finish());

        getSupportActionBar().setDisplayShowTitleEnabled(false); // Uygulama ismini toolbarda gizleme
    }
}