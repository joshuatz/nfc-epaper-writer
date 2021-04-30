package com.joshuatz.nfceinkwriter

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.tech.NfcA
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var mNfcAdapter: NfcAdapter? = null;
    private var mPendingIntent: PendingIntent? = null;
    private var mNfcTechList = arrayOf(arrayOf(NfcA::class.java.name));
    private var mNfcIntentFilters: Array<IntentFilter>? = null;

    private enum class IntentCodes {
        ImageFilePicked
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up intent and intent filters for NFC / NDEF scanning
        // This is part of the setup for foreground dispatch system
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        this.mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // Set up the filters
        var ndefIntentFilter: IntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntentFilter.addDataType("*/*");
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            Log.e("mimeTypeException", "Invalid / Malformed mimeType");
        }
        mNfcIntentFilters = arrayOf(ndefIntentFilter);

        // Init NFC adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_LONG).show();
        }


        // Setup image file picker
        val imageFilePickerCTA: Button = findViewById(R.id.pick_image_file_cta)
        imageFilePickerCTA.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type="*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/x-ms-bmp", "image/bmp", "image/x-bmp"))
            }
            startActivityForResult(intent, IntentCodes.ImageFilePicked.ordinal);
        }
    }

    override fun onPause() {
        super.onPause();
        this.mNfcAdapter?.disableForegroundDispatch(this);
    }

    override fun onResume() {
        super.onResume();
        this.mNfcAdapter?.enableForegroundDispatch(this, this.mPendingIntent, this.mNfcIntentFilters, this.mNfcTechList );
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent);
        Log.i("New intent", "New Intent: $intent");
    }

}