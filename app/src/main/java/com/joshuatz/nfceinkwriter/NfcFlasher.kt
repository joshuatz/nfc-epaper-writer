package com.joshuatz.nfceinkwriter

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView

class NfcFlasher : NfcFlasherBase() {
    private var mImgFilePath: String? = null
    private var mImgFileUri: Uri? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mImgFileUri != null) {
            outState.putString("serializedGeneratedImgUri",mImgFileUri.toString())
        }
    }

    // @TODO - change intent to just pass raw bytearr? Cleanup path usage?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_flasher)

        val savedUriStr = savedInstanceState?.getString("serializedGeneratedImgUri")
        if (savedUriStr != null) {
            mImgFileUri = Uri.parse(savedUriStr)
        } else {
            val intentExtras = intent.extras
            mImgFilePath = intentExtras?.getString(IntentKeys.GeneratedImgPath)
            if (mImgFilePath != null) {
                // @TODO - handle exceptions, navigate back to prev activity
                val fileRef = getFileStreamPath(mImgFilePath)
                mImgFileUri = Uri.fromFile(fileRef)
            }
        }
        if (mImgFileUri == null) {
            // Fallback to last generated image
            val fileRef = getFileStreamPath(GeneratedImageFilename)
            mImgFileUri = Uri.fromFile(fileRef)
        }

        val imagePreviewElem: ImageView = findViewById(R.id.previewImageView)
        imagePreviewElem.setImageURI(mImgFileUri)

        if (mImgFileUri != null) {
            val bmOptions = BitmapFactory.Options()
            this.mBitmap = BitmapFactory.decodeFile(mImgFileUri!!.path, bmOptions)
        }
    }
}