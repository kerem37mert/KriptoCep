package com.example.kriptocep;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class ChartActivity extends AppCompatActivity {

    WebView webViewChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        webViewChart = findViewById(R.id.webViewChart);

        WebSettings webSettings = webViewChart.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webViewChart.setWebViewClient(new WebViewClient());
        webViewChart.setWebChromeClient(new WebChromeClient());

        String symbol = getIntent().getStringExtra("symbol");
        if (symbol == null) symbol = "BTC"; //
        String usdSymbol = "BINANCE:" + symbol.toUpperCase() + "USDT";
        if(symbol.equals("USDT"))
            usdSymbol = "COINBASE:" + symbol.toUpperCase() + "USD";
        if(symbol.equals("USDC"))
            usdSymbol = "BINANCEUS:" + symbol.toUpperCase() + "USD";

        //

        String htmlContent = "<!DOCTYPE html><html><head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
                "<script type='text/javascript' src='https://s3.tradingview.com/tv.js'></script>" +
                "</head><body style='margin:0;padding:0;'>" +
                "<div id='tradingview_chart' style='height:100vh;width:100vw;'></div>" +
                "<script type='text/javascript'>" +
                "new TradingView.widget({" +
                "  container_id: 'tradingview_chart'," +
                "  autosize: true," +
                "  symbol: '" + usdSymbol + "'," +
                "  interval: 'D'," +
                "  timezone: 'Etc/UTC'," +
                "  theme: 'dark'," +
                "  style: '1'," +
                "  locale: 'en'," +
                "  enable_publishing: false," +
                "  allow_symbol_change: true" +
                "});" +
                "</script></body></html>";


        webViewChart.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }
}