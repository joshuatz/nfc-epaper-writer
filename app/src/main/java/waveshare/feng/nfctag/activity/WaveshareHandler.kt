/**
 * @file This is a wrapper around the Waveshare JAR, since the main class
 * is marked as package-private, and with slightly obfuscated methods
 * See: https://www.waveshare.com/wiki/Android_SDK_for_NFC-Powered_e-Paper
 */

package waveshare.feng.nfctag.activity

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.nfc.tech.NfcA
import android.widget.ProgressBar
import android.widget.Toast
import com.joshuatz.nfceinkwriter.R
import kotlinx.coroutines.*
import java.io.IOException

class WaveshareHandler {
    private var isWriting = false;
    private var isScanning = false;
    private var instance = a();
    private val mActivity: Activity;

    constructor(activity: Activity) {
        this.mActivity = activity;
    }

    /** Props with getters */
    val progress get() = instance.b();

    /**
     * Main sending function
     */
    fun sendBitmap(nfcTag: NfcA, ePaperSize: Int, bitmap: Bitmap) = runBlocking {
        var done = false;
        var failMsg = "";
        var success = false;
        val progressDialogBuilder = AlertDialog.Builder(mActivity);
        progressDialogBuilder.setView(R.layout.nfc_write_dialog);
        val progressDialog = progressDialogBuilder.create();
        progressDialog.show();
        // Track progress while running
        val progressBar: ProgressBar = progressDialog.findViewById(R.id.nfcFlashProgressbar);
        progressBar.min = 0;
        progressBar.max = 100;
        launch {
            while (!done) {
                progressBar.progress = progress;
                delay(10L);
            }
        }
        try {
            //
            val successInt = instance.a(nfcTag, ePaperSize, bitmap);
            if (successInt == 1) {
                // Success!
                success = true;
                val toast = Toast.makeText(mActivity.applicationContext, "Flash Successful!", Toast.LENGTH_LONG);
                toast.show();
            } else {
                // Hmm... not sure where they were getting txfail in their sample SDK code
                // val failMsg = getString(R.string.txfail);
                val failMsg = "Failed to write over NFC";
            }
        } catch (e: IOException) {
            //
            failMsg = e.toString();
        }

        done = true;
        progressDialog.hide();
        if (!success) {
            val toast = Toast.makeText(mActivity.applicationContext, "FAILED to Flash :( $failMsg", Toast.LENGTH_LONG);
            toast.show();
        }

        return@runBlocking success;
    }
}