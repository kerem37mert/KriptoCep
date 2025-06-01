package com.example.kriptocep;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SSSActivity extends AppCompatActivity {

    Toolbar toolbarSSS;
    TextView textSSS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sssactivity);

        toolbarSSS = findViewById(R.id.toolbarSSS);
        textSSS = findViewById(R.id.textSSS);
        textSSS.setMovementMethod(new ScrollingMovementMethod());

        String SSSMetni =
                "1. Bu uygulama ne işe yarar?\n"
                + "Kripto varlıklarınızı kolayca yönetmenizi sağlar ve piyasa fiyatlarını anlık olarak takip etmenize imkan tanır.\n\n"

                + "2. Hangi kripto paraları takip edebilirim?\n"
                + "Bitcoin (BTC), Ethereum (ETH), Ripple (XRP) gibi popüler kripto paralar dahil olmak üzere birçok token ve coin desteklenmektedir.\n\n"

                + "3. Piyasa verileri ne sıklıkla güncellenir?\n"
                + "Veriler gerçek zamanlı olarak güncellenir, böylece anlık fiyat hareketlerinden haberdar olabilirsiniz.\n\n"

                + "4. Varlıklarımı nasıl eklerim veya güncellerim?\n"
                + "Uygulama içindeki \"Varlıklarım\" bölümünden sahip olduğunuz kripto paraları manuel olarak ekleyebilir veya düzenleyebilirsiniz.\n\n"

                + "5. Güvenlik nasıl sağlanıyor?\n"
                + "Kişisel bilgileriniz ve varlık verileriniz güvenli bir şekilde saklanmakta, hiçbir şekilde üçüncü taraflarla paylaşılmamaktadır.\n\n"

                + "6. Bildirimleri nasıl ayarlarım?\n"
                + "Henüz uygulamamızın bu versiyonunda bildirim sistemi bulunmuyor.\n\n"

                + "7. Ücretli bir versiyon var mı?\n"
                + "Uygulamanın tüm özelliklerine ücretsiz bir şekilde erişebilirsiniz.\n\n"

                + "8. Kripto işlemi yapabilir miyim?\n"
                + "Bu uygulama sadece varlık yönetimi ve piyasa takibi amaçlıdır. Alım-satım işlemleri desteklenmemektedir.\n\n"

                + "9. Destek için nasıl iletişime geçebilirim?\n"
                + "Herhangi bir sorun veya sorunuz için 'İletişim' bölümünden bize ulaşabilirsiniz.\n\n"

                + "Ek sorularınız için destek ekibimiz her zaman yanınızdadır.";

        textSSS.setText(SSSMetni);

        // Toolbar Geri Tuşu
        setSupportActionBar(toolbarSSS);
        toolbarSSS.setNavigationOnClickListener(v -> finish());

        getSupportActionBar().setDisplayShowTitleEnabled(false); // Uygulama ismini toolbarda gizleme
    }
}