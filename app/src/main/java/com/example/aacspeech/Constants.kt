package com.example.aacspeech

import java.util.*

object Constants {
    const val PREFS_NAME = "AACSpeechPrefs"
    const val ITEMS_KEY = "items"
    const val LANGUAGE_KEY = "selected_language"
    const val FIRST_LAUNCH_KEY = "first_launch"
    
    fun getLocaleForLanguage(language: String): Locale {
        return when (language) {
            "es" -> Locale("es")
            "de" -> Locale("de")
            else -> Locale.ENGLISH
        }
    }
}
