package com.joshuatz.nfceinkwriter

class TextEditor : GraphicEditorBase() {
    override val layoutId = R.layout.activity_text_editor
    override val flashButtonId = R.id.flashTextButton
    override val webViewId = R.id.textEditWebView
    override val webViewUrl = "https://appassets.androidplatform.net/assets/editors/text/index.html"

    override fun onWebViewPageFinished() {
        super.onWebViewPageFinished()
        this.updateCanvasSize()
    }
}