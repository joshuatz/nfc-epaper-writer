package com.joshuatz.nfceinkwriter

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import waveshare.feng.nfctag.activity.WaveShareHandler
import java.io.IOException
import java.nio.charset.StandardCharsets

open class NfcFlasherBase : AppCompatActivity() {
    private var mIsFlashing = false
    private var mNfcAdapter: NfcAdapter? = null
    private var mPendingIntent: PendingIntent? = null
    private var mNfcTechList = arrayOf(arrayOf(NfcA::class.java.name))
    private var mNfcIntentFilters: Array<IntentFilter>? = null
    protected var mBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val originatingIntent = intent

        // Set up intent and intent filters for NFC / NDEF scanning
        // This is part of the setup for foreground dispatch system
        val nfcIntent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        this.mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        // Set up the filters
        var ndefIntentFilter: IntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        try {
            // android:host
            ndefIntentFilter.addDataAuthority("ext", null)

            // android:pathPattern
            // allow all data paths - see notes below
            ndefIntentFilter.addDataPath(".*", PatternMatcher.PATTERN_SIMPLE_GLOB)
            // NONE of the below work, although at least one or more should
            // I think because the payload isn't getting extracted out into the intent by Android
            // Debugging shows mData.path = null, which makes no sense (it definitely is not, and if
            // I don't intercept AAR, Android definitely tries to open the corresponding app...
            //ndefIntentFilter.addDataPath("waveshare.feng.nfctag.*", PatternMatcher.PATTERN_SIMPLE_GLOB);
            //ndefIntentFilter.addDataPath(".*waveshare\\.feng\\.nfctag.*", PatternMatcher.PATTERN_SIMPLE_GLOB);
            //ndefIntentFilter.addDataPath("waveshare.feng.nfctag", PatternMatcher.PATTERN_LITERAL);
            //ndefIntentFilter.addDataPath("waveshare\\.feng\\.nfctag", PatternMatcher.PATTERN_LITERAL);

            // android:scheme
            ndefIntentFilter.addDataScheme("vnd.android.nfc")
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            Log.e("mimeTypeException", "Invalid / Malformed mimeType")
        }
        mNfcIntentFilters = arrayOf(ndefIntentFilter)

        // Init NFC adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        this.mNfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onResume() {
        super.onResume()
        this.mNfcAdapter?.enableForegroundDispatch(this, this.mPendingIntent, this.mNfcIntentFilters, this.mNfcTechList )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.i("New intent", "New Intent: $intent")
        Log.v("Intent.action", intent.action ?: "no action")

        val preferences = Preferences(this)
        val screenSizeEnum = preferences.getScreenSizeEnum()

        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED || intent.action == NfcAdapter.ACTION_TAG_DISCOVERED || intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            val detectedTag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!
            val tagId = String(detectedTag.id, StandardCharsets.US_ASCII)
            val tagTechList = detectedTag.techList

            // Do we still have a bitmap to flash?
            val bitmap = this.mBitmap
            if (bitmap == null) {
                Log.v("Missing bitmap", "mBitmap = null")
                return
            }

            // Check for correct NFC type support
            if (tagTechList[0] != "android.nfc.tech.NfcA") {
                Log.v("Invalid tag type. Found:", tagTechList.toString())
                return
            }

            // Do an explicit check for the ID. This ID *appears* to be constant across all models
            if (tagId != WaveShareUID) {
                Log.v("Invalid tag ID", "$tagId != $WaveShareUID")
                return
            }

            // ACTION_NDEF_DISCOVERED has the filter applied for the AAR record *type*,
            // but the filter for the payload (dataPath / pathPattern) is not working, so as
            // an extra check, AAR payload will be manually checked, as well as ID
            if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
                var aarFound = false
                val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                if (rawMsgs != null) {
                    for (msg in rawMsgs) {
                        val ndefMessage: NdefMessage = msg as NdefMessage
                        val records = ndefMessage.records
                        for (record in records) {
                            var payloadStr = String(record.payload)
                            if (!aarFound) aarFound = payloadStr == "waveshare.feng.nfctag"
                            if (aarFound) break
                        }
                        if (aarFound) break
                    }
                }

                if (!aarFound) {
                    Log.v("Bad NDEFs:", "records found, but missing AAR")
                }
            }

            if (!mIsFlashing) {
                // Here we go!!!
                Log.v("Matched!", "Tag is a match! Preparing to flash...")
                this.mIsFlashing = true
                val waveshareHandler = WaveShareHandler(this)
                val nfcaObj = NfcA.get(detectedTag)
                try {
                    val success = waveshareHandler.sendBitmap(nfcaObj, screenSizeEnum, bitmap)
                    Log.v("Final success val", "Success = $success")
                } finally {
                    try {
                        nfcaObj.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.v("NfcFlasherBase, Exception", e.message ?: "No e.message")
                        Log.v("Flashing failed", "See trace above")
                    }
                    Log.v("Tag closed", "Setting flash in progress = false")
                    this.mIsFlashing = false
                }
            } else {
                Log.v("Not flashing", "Flashing already in progress!")
            }
        }
    }
}