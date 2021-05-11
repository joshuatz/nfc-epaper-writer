package com.joshuatz.nfceinkwriter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import androidx.webkit.WebViewAssetLoader

class TextEditor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        val preferences = Preferences(this);
        val pixelSize = ScreenSizesInPixels[preferences.getScreenSize()];

        val webView: WebView = findViewById(R.id.textEditWebView);

        // Setup asset loader to handle local asset paths
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build();

        // Override WebView client
        webView.webViewClient = object : WebViewClient() {
            // If request is for local file, intercept and serve
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url);
            }

            // Listen for page load finished, before eval'ing JS
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url);
                // Pass display size to WebView
                webView.evaluateJavascript("setDisplaySize(${pixelSize!!.first}, ${pixelSize!!.second});", null);
            }
        }

        // Enable some settings for WebView
        webView.settings.javaScriptEnabled = true;
        // Chrome client
        webView.webChromeClient = WebChromeClient();


        webView.loadUrl("https://appassets.androidplatform.net/assets/editors/text/index.html");
    }
}