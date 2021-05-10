package com.joshuatz.nfceinkwriter

// Order matches WS SDK Enum
var ScreenSizes = arrayOf(
    "2.13\"",
    "2.9\"",
    "4.2\"",
    "7.5\"",
    "7.5\" HD",
    "2.7\"",
    "2.9\" v.B",
)

var DefaultScreenSize = ScreenSizes[1];

object Constants {
    var Preference_File_Key = "Preferences";
    var PreferenceKeys = PrefKeys;
}

object PrefKeys {
    var DisplaySize = "Display_Size";
}