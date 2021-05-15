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
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.joshuatz.nfceinkwriter.R
import java.io.IOException


class WaveShareHandler {
    private val mActivity: Activity
    private val mInstance: a

    constructor(activity: Activity) {
        this.mInstance = a()
        this.mInstance.a()
        this.mActivity = activity
    }

    /** Props with getters */
    val progress get() = this.mInstance.c

    /**
     * Main sending function
     */
    fun sendBitmap(nfcTag: NfcA, ePaperSize: Int, bitmap: Bitmap): Boolean {
        var done = false
        var failMsg = ""
        var success = false
        val progressDialogBuilder = AlertDialog.Builder(mActivity)
        progressDialogBuilder.setView(R.layout.nfc_write_dialog)
        progressDialogBuilder.setTitle("Flashing NFC")
        val progressDialog = progressDialogBuilder.create()
        progressDialog.show()
        // Track progress while running
        val progressBar: ProgressBar = progressDialog.findViewById(R.id.nfcFlashProgressbar)
        progressBar.min = 0
        progressBar.max = 100
        Thread {
            while (!done) {
                progressBar.progress = progress
                Thread.sleep(10L)
            }
        }.start()
        try {
            // Initialize
            val connectionSuccessInt = this.mInstance.a(nfcTag)
            // Override WaveShare's SDK default of 700
            nfcTag.timeout = 1200
            if (connectionSuccessInt != 1) {
                // IO exception in nfcTag.connect()
                failMsg = "Failed to connect to tag"
            } else {
                var flashSuccessInt = -1
                Thread {
                    flashSuccessInt = this.mInstance.a(ePaperSize, bitmap)
                }.apply {
                    start()
                    join()
                }
                if (flashSuccessInt == 1) {
                    // Success!
                    val toast = Toast.makeText(
                        mActivity.applicationContext,
                        "Flash Successful!",
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                } else if (flashSuccessInt == 2) {
                    failMsg = "Incorrect image resolution"
                } else {
                    failMsg = "Failed to write over NFC, unknown reason"
                }
            }
        } catch (e: IOException) {
            failMsg = e.toString()
            Log.v("WaveshareHandler, IO Exception", failMsg)
        }

        done = true
        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.hide()
        }, 2000L)
        if (!success) {
            val toast = Toast.makeText(mActivity.applicationContext, "FAILED to Flash :( $failMsg", Toast.LENGTH_LONG)
            toast.show()
        }

        return success
    }
}