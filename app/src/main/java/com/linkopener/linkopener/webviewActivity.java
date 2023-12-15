package com.linkopener.linkopener;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class webviewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.actionBarColor)); // R.color.your_color, kendi renk kaynağınıza göre değiştirin
        }

        String webURL = getIntent().getStringExtra("webURL");

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(webURL);

        // onbackpressed kullanımdan kaldırıldı ve onun yerine bu kod parçasını kullan
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                }else {
                    startActivity(new Intent(webviewActivity.this,MainActivity.class));
                    finish();
                }
            }
        };
        // Geri tuşuna basıldığında bu callback çalışacak.
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}