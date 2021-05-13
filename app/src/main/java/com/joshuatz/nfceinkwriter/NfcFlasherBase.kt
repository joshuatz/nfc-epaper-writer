package com.joshuatz.nfceinkwriter

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

open class NfcFlasherBase : AppCompatActivity() {
    private var mIsFlashing = false;
    private var mNfcAdapter: NfcAdapter? = null;
    private var mPendingIntent: PendingIntent? = null;
    private var mNfcTechList = arrayOf(arrayOf(NfcA::class.java.name));
    private var mNfcIntentFilters: Array<IntentFilter>? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        val originatingIntent = intent;

        // Set up intent and intent filters for NFC / NDEF scanning
        // This is part of the setup for foreground dispatch system
        val nfcIntent = Intent(this, javaClass).apply {
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
    }

    override fun onPause() {
        super.onPause();
        this.mNfcAdapter?.disableForegroundDispatch(this);
    }

    override fun onResume() {
        super.onResume();
        this.mNfcAdapter?.enableForegroundDispatch(this, this.mPendingIntent, this.mNfcIntentFilters, this.mNfcTechList );
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent);
        Log.i("New intent", "New Intent: $intent");

        if (!mIsFlashing && intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val detectedTag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!;
            val tagTechList = detectedTag.techList;
            if (tagTechList[0] == "android.nfc.tech.NfcA") {
                Log.v("tagTechList", tagTechList.toString());
            }
        }
    }
}