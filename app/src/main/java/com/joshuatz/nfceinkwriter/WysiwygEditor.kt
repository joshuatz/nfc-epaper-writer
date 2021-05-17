package com.joshuatz.nfceinkwriter

class WysiwygEditor : GraphicEditorBase() {
    override val layoutId = R.layout.activity_wysiwyg_editor
    override val flashButtonId = R.id.flashWysiwygButton
    override val webViewId = R.id.wysiwygWebView
    override val webViewUrl = "https://jspaint.app/"

    override fun onWebViewPageStarted() {
        super.onWebViewPageStarted()
        Utils.injectEditorCommonJs(this.mWebView!!)
        Utils.injectAssetJs(this.mWebView!!, "/editors/wysiwyg/main.js")
    }

    override fun onWebViewPageFinished() {
        super.onWebViewPageFinished()
        this.updateCanvasSize()
    }
}