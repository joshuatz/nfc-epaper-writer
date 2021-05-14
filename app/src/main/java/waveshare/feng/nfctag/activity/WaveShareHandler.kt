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
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.joshuatz.nfceinkwriter.R
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.concurrent.thread
import waveshare.feng.nfctag.activity.a;


class WaveShareHandler {
    private val mActivity: Activity;
    private val mInstance: a;

    constructor(activity: Activity) {
        this.mInstance = a();
        this.mInstance.a();
        this.mActivity = activity;
    }

    /** Props with getters */
    val progress get() = this.mInstance.c;

    /**
     * Main sending function
     */
    fun sendBitmap(nfcTag: NfcA, ePaperSize: Int, bitmap: Bitmap): Boolean {
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
        thread {
            while (!done) {
                progressBar.progress = progress;
                Thread.sleep(10L);
            }
        }
        try {
            // For some reason, a() is heavily overloaded, and signature with no args
            // is used as "initialization" method
            instance.a();
            val successInt = instance.a(nfcTag, ePaperSize, bitmap);
            if (successInt == 1) {
                // Success!
                success = true;
                val toast = Toast.makeText(mActivity.applicationContext, "Flash Successful!", Toast.LENGTH_LONG);
                toast.show();
            } else {
                // Hmm... not sure where they were getting txfail in their sample SDK code
                // val failMsg = getString(R.string.txfail);
                failMsg = "Failed to write over NFC";
            }
        } catch (e: IOException) {
            failMsg = e.toString();
            Log.v("WaveshareHandler, IO Exception", failMsg);
        }

        done = true;
        progressDialog.hide();
        if (!success) {
            val toast = Toast.makeText(mActivity.applicationContext, "FAILED to Flash :( $failMsg", Toast.LENGTH_LONG);
            toast.show();
        }

        return success;
    }
}