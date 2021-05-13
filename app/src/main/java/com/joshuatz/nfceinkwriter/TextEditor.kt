package com.joshuatz.nfceinkwriter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.*
import android.widget.Button
import androidx.webkit.WebViewAssetLoader
import kotlinx.coroutines.delay
import waveshare.feng.nfctag.activity.WaveshareHandler
import java.lang.Thread.sleep
import android.util.Base64
import java.io.IOException

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
                this.saveBitmap(this, bitmap, Bitmap.CompressFormat.PNG, "image/png", "Foobar");
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


            // val waveshareHandler = WaveshareHandler(this);
//            val navIntent = Intent(this, NfcFlasher::class.java);
//            startActivity(navIntent);
        }
    }

    // @TODO - for testing, delete
    // credit https://stackoverflow.com/a/56990305/11447682
    fun saveBitmap(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        val resolver = context.contentResolver
        var uri: Uri? = null

        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Failed to create new MediaStore record.")

            resolver.openOutputStream(uri)?.use {
                if (!bitmap.compress(format, 95, it))
                    throw IOException("Failed to save bitmap.")
            } ?: throw IOException("Failed to open output stream.")

            return uri

        } catch (e: IOException) {

            uri?.let { orphanUri ->
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(orphanUri, null, null)
            }

            throw e
        }
    }
}