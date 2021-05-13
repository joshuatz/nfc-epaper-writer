package com.joshuatz.nfceinkwriter

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView

class NfcFlasher : NfcFlasherBase() {
    private var mBitmap: Bitmap? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_flasher)

        val imagePreviewElem: ImageView = findViewById(R.id.previewImageView);

        val intentExtras = intent.extras;
        val bitmapPath = intentExtras?.getString(IntentKeys.GeneratedImgPath);
        if (bitmapPath != null) {
            // @TODO - handle exceptions, navigate back to prev activity
            val fileRef = getFileStreamPath(bitmapPath);
            imagePreviewElem.setImageURI(Uri.fromFile(fileRef));
        }
    }
}