package com.joshuatz.nfceinkwriter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.webkit.*
import android.widget.Button
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.webkit.WebViewAssetLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class GraphicEditorBase: AppCompatActivity() {
    @get:LayoutRes abstract val layoutId: Int
    @get:IdRes abstract val flashButtonId: Int
    abstract val webViewUrl: String
    private var mWebView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(this.layoutId)

        val preferences = Preferences(this)
        val pixelSize = ScreenSizesInPixels[preferences.getScreenSize()]

        val webView: WebView = findViewById(R.id.textEditWebView)
        this.mWebView = webView

        // Setup asset loader to handle local asset paths
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        // Override WebView client
        webView.webViewClient = object : WebViewClient() {
            // If request is for local file, intercept and serve
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            // Listen for page load finished, before evaluating JS
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Pass display size to WebView
                webView.evaluateJavascript("setDisplaySize(${pixelSize!!.first}, ${pixelSize.second});", null)
            }
        }

        // Enable some settings for WebView
        webView.settings.javaScriptEnabled = true
        // Chrome client
        webView.webChromeClient = WebChromeClient()


        webView.loadUrl(this.webViewUrl)

        val flashButton: Button = findViewById(this.flashButtonId)
        flashButton.setOnClickListener {
            lifecycleScope.launch {
                getAndFlashGraphic()
            }
        }
    }

    private suspend fun getAndFlashGraphic() {
        val mContext = this
        val imageBytes = this.getBitmapFromWebView(this.mWebView!!)
        // Decode binary to bitmap
        @Suppress("UNUSED_VARIABLE")
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        // Save bitmap to file
        withContext(Dispatchers.IO) {
            openFileOutput(GeneratedImageFilename, Context.MODE_PRIVATE).use { fileOutStream ->
                fileOutStream.write(imageBytes)
                fileOutStream.close()
                val navIntent = Intent(mContext, NfcFlasher::class.java)
                val bundle = Bundle()
                bundle.putString(IntentKeys.GeneratedImgPath, GeneratedImageFilename)
                navIntent.putExtras(bundle)
                startActivity(navIntent)
            }
        }
    }

    abstract suspend fun getBitmapFromWebView(webView: WebView): ByteArray
}