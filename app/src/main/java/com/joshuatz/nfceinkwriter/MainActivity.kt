package com.joshuatz.nfceinkwriter

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.tech.NfcA
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var mPreferencesController: Preferences? = null;

    private enum class IntentCodes {
        ImageFilePicked
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register action bar / toolbar
        setSupportActionBar(findViewById(R.id.main_toolbar));

        // Get user preferences
        this.mPreferencesController = Preferences(this);
        var sharedPrefs = this.mPreferencesController?.getPreferences();
        this.updateScreenSizeDisplay(null);

        // Setup screen size changer
        val screenSizeChangeInvite: Button = findViewById(R.id.changeDisplaySizeInvite);
        screenSizeChangeInvite.setOnClickListener {
            this.mPreferencesController?.showScreenSizePicker(fun(updated: String): Void? {
                this.updateScreenSizeDisplay(updated);
                return null;
            });
        }

        // Setup image file picker
        val imageFilePickerCTA: Button = findViewById(R.id.cta_pick_image_file)
        imageFilePickerCTA.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type="*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/x-ms-bmp", "image/bmp", "image/x-bmp"))
            }
            startActivityForResult(intent, IntentCodes.ImageFilePicked.ordinal);
        }

        // Setup WYSIWYG button click
        val wysiwygEditButtonInvite: Button = findViewById(R.id.cta_new_graphic);
        wysiwygEditButtonInvite.setOnClickListener {
            val intent = Intent(this, WysiwygEditor::class.java);
            startActivity(intent);
        }

        // Setup text button click
        val textEditButtonInvite: Button = findViewById(R.id.cta_new_text);
        textEditButtonInvite.setOnClickListener {
            val intent = Intent(this, TextEditor::class.java);
            startActivity(intent);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == IntentCodes.ImageFilePicked.ordinal && resultCode == Activity.RESULT_OK) {
            if (resultData?.data != null) {
                Log.v("Result Data", resultData?.data?.path ?: "No result data path");
                var uri: Uri = resultData.data!!.normalizeScheme();
                var type = applicationContext.contentResolver.getType(uri);
                Log.v("Type", type!!);
            }
            throw Exception("Hello!");
        }
    }

    private fun updateScreenSizeDisplay(updated: String?) {
        var screenSizeStr = updated;
        if (screenSizeStr == null) {
            screenSizeStr = this.mPreferencesController?.getPreferences()
                ?.getString(Constants.PreferenceKeys.DisplaySize, DefaultScreenSize);
        }
        findViewById<TextView>(R.id.currentDisplaySize).text = screenSizeStr ?: DefaultScreenSize;
    }

}