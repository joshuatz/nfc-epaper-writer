package com.joshuatz.nfceinkwriter

import android.webkit.WebView
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Utils {
    companion object {
        suspend fun evaluateJavascript(webView: WebView, evalStr: String): String {
            return suspendCoroutine { continuation ->
                webView.evaluateJavascript(evalStr) {
                    continuation.resume(it)
                }
            }
        }
        fun injectAssetJs(webView: WebView, assetPath: String) {
            webView.evaluateJavascript("(()=>{const e=\"https://appassets.androidplatform.net/assets$assetPath\";let o=!!document.querySelector(`script[src=\"\${e}\"]`);const t=()=>{const t=document.head||document.body;if(!o&&t){const n=document.createElement(\"script\");n.src=e,t.appendChild(n),o=!0,console.log(\"Editor Common JS injected!\")}};t(),o?console.log(\"Skipping Common JS injection - already injected\"):(window.addEventListener(\"DOMContentLoaded\",t),setTimeout(t,200))})();", null)
        }
        fun injectEditorCommonJs(webView: WebView) {
            this.injectAssetJs(webView,"/editors/common.js")
        }
    }
}