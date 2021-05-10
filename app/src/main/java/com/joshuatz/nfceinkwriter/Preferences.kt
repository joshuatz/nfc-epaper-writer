package com.joshuatz.nfceinkwriter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import com.joshuatz.nfceinkwriter.Constants.PreferenceKeys
import com.joshuatz.nfceinkwriter.Constants.Preference_File_Key

class Preferences {
    private var mActivity: Activity;
    private var mAppContext: Context;

    constructor(activity: Activity) {
        this.mActivity = activity;
        this.mAppContext = activity.applicationContext;
    }

    public fun getPreferences(): SharedPreferences {
        return this.mAppContext.getSharedPreferences(Preference_File_Key, Context.MODE_PRIVATE);
    }

    public fun showScreenSizePicker(callback: (String) -> Void?) {
        val alertBuilder = AlertDialog.Builder(this.mActivity);
        alertBuilder
            .setTitle("Pick Your Screen Size")
            .setItems(ScreenSizes, DialogInterface.OnClickListener { dialog, which ->
                val selectedSize = ScreenSizes[which];
                with (this.getPreferences().edit()) {
                    putString(PreferenceKeys.DisplaySize, selectedSize);
                    apply();
                }
                callback(selectedSize);
            });
        alertBuilder.show();
    }
}