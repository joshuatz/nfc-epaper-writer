/**
 * @file This is a wrapper around the Waveshare JAR, since the main class
 * is marked as package-private, and with slightly obfuscated methods
 * See: https://www.waveshare.com/wiki/Android_SDK_for_NFC-Powered_e-Paper
 */

package waveshare.feng.nfctag.activity

import android.app.Activity
import android.graphics.Bitmap
import android.nfc.tech.NfcA
import android.util.Log
import java.io.IOException

interface FlashResult {
    val success: Boolean
    val errMessage: String
}


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
    fun sendBitmap(nfcTag: NfcA, ePaperSize: Int, bitmap: Bitmap): FlashResult {
        var failMsg = ""
        var success = false

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
                flashSuccessInt = this.mInstance.a(ePaperSize, bitmap)
                if (flashSuccessInt == 1) {
                    // Success!
                    success = true
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

        return object : FlashResult {
            override val success = success
            override val errMessage = failMsg
        }
    }
}