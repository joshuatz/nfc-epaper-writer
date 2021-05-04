/**
 * @file This is a wrapper around the Waveshare JAR, since the main class
 * is marked as package-private, and with slightly obfuscated methods
 * See: https://www.waveshare.com/wiki/Android_SDK_for_NFC-Powered_e-Paper
 */

package waveshare.feng.nfctag.activity

import android.graphics.Bitmap
import android.nfc.tech.NfcA

class WaveshareHandler {
    private var isWriting = false;
    private var instance = a();

    /** Props with getters */
    val progress get() = instance.b();

    /**
     * Main sending function
     */
    fun sendBitmap(nfcTag: NfcA, ePaperSize: Int, bitmap: Bitmap) {
        //
    }
}