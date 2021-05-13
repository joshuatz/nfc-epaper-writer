package com.joshuatz.nfceinkwriter

const val PackageName = "com.joshuatz.nfceinkwriter";

// Order matches WS SDK Enum
// @see https://www.waveshare.com/wiki/Android_SDK_for_NFC-Powered_e-Paper
val ScreenSizes = arrayOf(
    "2.13\"",
    "2.9\"",
    "4.2\"",
    "7.5\"",
    "7.5\" HD",
    "2.7\"",
    "2.9\" v.B",
)

val DefaultScreenSize = ScreenSizes[1];

val ScreenSizesInPixels = mapOf(
    "2.13\"" to Pair(250, 122),
    "2.9\"" to Pair(296, 128),
    "4.2\"" to Pair(400, 300),
    "7.5\"" to Pair(800, 480),
    "7.5\" HD" to Pair(880, 528),
    "2.7\"" to Pair(264, 176),
    "2.9\" v.B" to Pair(296, 128),
)

object Constants {
    var Preference_File_Key = "Preferences";
    var PreferenceKeys = PrefKeys;
}

object PrefKeys {
    var DisplaySize = "Display_Size";
}

object IntentKeys {
    var GeneratedImgPath = "$PackageName.imgUri";
    var GeneratedImgMime = "$PackageName.imgMime";
}

val GeneratedImageFilename = "generated.png";