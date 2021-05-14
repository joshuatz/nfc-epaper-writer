package com.joshuatz.nfceinkwriter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import android.widget.Button
import androidx.webkit.WebViewAssetLoader
import java.lang.Thread.sleep
import android.util.Base64

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

        val flashButton: Button = findViewById(R.id.flashTextButton);
        flashButton.setOnClickListener {
            // Dump bitmap data from Canvas
            // Cheating by using delay + global. @TODO - rewrite to addJavascriptInterface (careful)
            webView.evaluateJavascript("getImgSerializedFromCanvas(undefined, undefined, (output) => window.imgStr = output);", null);
            sleep(1000L);
            webView.evaluateJavascript("window.imgStr;") { bitmapStr ->
                // Convert base64 string back into raw data
                val imageBytes = Base64.decode(bitmapStr, Base64.DEFAULT);
                // Decode binary to bitmap
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size);
                // Save bitmap to file
                this.openFileOutput(GeneratedImageFilename, Context.MODE_PRIVATE).use { fileOutStream ->
                    fileOutStream.write(imageBytes);
                    fileOutStream.close();
                    val navIntent = Intent(this, NfcFlasher::class.java);
                    val bundle = Bundle();
                    bundle.putString(IntentKeys.GeneratedImgPath, GeneratedImageFilename);
                    navIntent.putExtras(bundle);
                    startActivity(navIntent);
                }
            }
        }
    }
}