package com.joshuatz.nfceinkwriter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    private var mPreferencesController: Preferences? = null
    private var mHasReFlashableImage: Boolean = false
    private val mReFlashButton: CardView get() = findViewById(R.id.reflashButton)

    private enum class IntentCodes {
        ImageFilePicked
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register action bar / toolbar
        setSupportActionBar(findViewById(R.id.main_toolbar))

        // Get user preferences
        this.mPreferencesController = Preferences(this)
        this.updateScreenSizeDisplay(null)

        // Setup screen size changer
        val screenSizeChangeInvite: Button = findViewById(R.id.changeDisplaySizeInvite)
        screenSizeChangeInvite.setOnClickListener {
            this.mPreferencesController?.showScreenSizePicker(fun(updated: String): Void? {
                this.updateScreenSizeDisplay(updated)
                return null
            })
        }

        // Check for previously generated image, enable re-flash button if available
        checkReFlashAbility()

        mReFlashButton.setOnClickListener {
            if (mHasReFlashableImage) {
                val navIntent = Intent(this, NfcFlasher::class.java)
                startActivity(navIntent)
            } else {
                val toast = Toast.makeText(this, "There is no image to re-flash!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }


        // Setup image file picker
        val imageFilePickerCTA: Button = findViewById(R.id.cta_pick_image_file)
        imageFilePickerCTA.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type="*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/x-ms-bmp", "image/bmp", "image/x-bmp"))
            }
            startActivityForResult(intent, IntentCodes.ImageFilePicked.ordinal)
        }

        // Setup WYSIWYG button click
        val wysiwygEditButtonInvite: Button = findViewById(R.id.cta_new_graphic)
        wysiwygEditButtonInvite.setOnClickListener {
            val intent = Intent(this, WysiwygEditor::class.java)
            startActivity(intent)
        }

        // Setup text button click
        val textEditButtonInvite: Button = findViewById(R.id.cta_new_text)
        textEditButtonInvite.setOnClickListener {
            val intent = Intent(this, TextEditor::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        checkReFlashAbility()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        // Local image file picked for flashing
        if (requestCode == IntentCodes.ImageFilePicked.ordinal && resultCode == Activity.RESULT_OK) {
            if (resultData?.data != null) {
                Log.v("Result Data", resultData.data?.path ?: "No result data path")
                val uri: Uri = resultData.data!!.normalizeScheme()
                val type = applicationContext.contentResolver.getType(uri)
                Log.v("Type", type!!)

                // @TODO - crop and then flash
            }
        }
    }

    private fun updateScreenSizeDisplay(updated: String?) {
        var screenSizeStr = updated
        if (screenSizeStr == null) {
            screenSizeStr = this.mPreferencesController?.getPreferences()
                ?.getString(Constants.PreferenceKeys.DisplaySize, DefaultScreenSize)
        }
        findViewById<TextView>(R.id.currentDisplaySize).text = screenSizeStr ?: DefaultScreenSize
    }

    private fun checkReFlashAbility() {
        val lastGeneratedFile = getFileStreamPath(GeneratedImageFilename)
        val reFlashImagePreview: ImageView = findViewById(R.id.reflashButtonImage)
        if (lastGeneratedFile.exists()) {
            mHasReFlashableImage = true
            // Need to set null first, or else Android will cache previous image
            reFlashImagePreview.setImageURI(null)
            reFlashImagePreview.setImageURI(Uri.fromFile((lastGeneratedFile)))
        } else {
            // Grey out button
            mReFlashButton.setCardBackgroundColor(Color.DKGRAY)
            val drawableImg = resources.getDrawable(android.R.drawable.stat_sys_warning, null)
            reFlashImagePreview.setImageDrawable(drawableImg)
        }
    }

}