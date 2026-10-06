package com.yagsatis.mobile;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final int REQUEST_FILE_CHOOSER = 1001;
    private static final int REQUEST_CREATE_BACKUP = 1002;

    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private String pendingBackupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(11, 18, 32));
        getWindow().setNavigationBarColor(Color.rgb(11, 18, 32));

        webView = new WebView(this);
        webView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setTextZoom(100);
        settings.setMediaPlaybackRequiresUserGesture(true);

        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.setWebViewClient(new AppWebViewClient());
        webView.setWebChromeClient(new AppWebChromeClient());

        if (savedInstanceState == null) {
            webView.loadUrl("file:///android_asset/index.html");
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.removeJavascriptInterface("AndroidBridge");
            webView.destroy();
        }
        super.onDestroy();
    }

    private void openExternal(Uri uri) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException ignored) {
            Toast.makeText(this, "Bu bağlantıyı açacak uygulama bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    private final class AppWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            String scheme = uri.getScheme();
            if ("file".equalsIgnoreCase(scheme) || "about".equalsIgnoreCase(scheme)) {
                return false;
            }
            openExternal(uri);
            return true;
        }
    }

    private final class AppWebChromeClient extends WebChromeClient {
        @Override
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            if (MainActivity.this.filePathCallback != null) {
                MainActivity.this.filePathCallback.onReceiveValue(null);
            }
            MainActivity.this.filePathCallback = filePathCallback;

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/json", "text/plain"});
            try {
                startActivityForResult(intent, REQUEST_FILE_CHOOSER);
                return true;
            } catch (ActivityNotFoundException e) {
                MainActivity.this.filePathCallback = null;
                Toast.makeText(MainActivity.this, "Dosya seçici açılamadı.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    public final class AndroidBridge {
        @JavascriptInterface
        public void saveTextFile(String fileName, String content) {
            pendingBackupText = content == null ? "" : content;
            runOnUiThread(() -> {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                intent.putExtra(Intent.EXTRA_TITLE,
                        (fileName == null || fileName.trim().isEmpty()) ? "yag_satis_yedek.json" : fileName);
                try {
                    startActivityForResult(intent, REQUEST_CREATE_BACKUP);
                } catch (ActivityNotFoundException e) {
                    pendingBackupText = null;
                    Toast.makeText(MainActivity.this, "Dosya kaydetme ekranı açılamadı.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @JavascriptInterface
        public String platform() {
            return "android";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FILE_CHOOSER) {
            if (filePathCallback == null) return;
            Uri[] results = null;
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                results = new Uri[]{data.getData()};
            }
            filePathCallback.onReceiveValue(results);
            filePathCallback = null;
            return;
        }

        if (requestCode == REQUEST_CREATE_BACKUP) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null && pendingBackupText != null) {
                try (OutputStream output = getContentResolver().openOutputStream(data.getData(), "w")) {
                    if (output == null) throw new IllegalStateException("Dosya açılamadı");
                    output.write(pendingBackupText.getBytes(StandardCharsets.UTF_8));
                    output.flush();
                    Toast.makeText(this, "Yedek dosyası kaydedildi.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Yedek kaydedilemedi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            pendingBackupText = null;
        }
    }
}
