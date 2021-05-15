package com.joshuatz.nfceinkwriter

import android.util.Base64
import android.webkit.WebView
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TextEditor : GraphicEditorBase() {
    override val layoutId = R.layout.activity_text_editor
    override val flashButtonId = R.id.flashTextButton
    override val webViewUrl = "https://appassets.androidplatform.net/assets/editors/text/index.html"

    override suspend fun getBitmapFromWebView(webView: WebView): ByteArray {
        // Dump bitmap data from Canvas
        // Cheating by using delay + global. @TODO - rewrite to addJavascriptInterface (careful)
        webView.evaluateJavascript(
            "getImgSerializedFromCanvas(undefined, undefined, (output) => window.imgStr = output);",
            null
        )
        delay(1000L)

        return suspendCoroutine<ByteArray> { continuation ->
            webView.evaluateJavascript("window.imgStr;") { bitmapStr ->
                val imageBytes = Base64.decode(bitmapStr, Base64.DEFAULT)
                continuation.resume(imageBytes)
            }
        }
    }
}