package com.example.aacspeech

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import android.webkit.ConsoleMessage
import java.util.*

class WidgetTTSService : Service(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var ttsInitialized = false
    private val pendingTexts = mutableListOf<String>()
    private var selectedLanguage: String = "en"

    companion object {
        const val EXTRA_TEXT = "extra_text"
    }

    override fun onCreate() {
        super.onCreate()
        loadLanguagePreference()
        tts = TextToSpeech(this, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("WidgetTTSService", "TTS initialized successfully (status=$startId)")

        intent?.getStringExtra(EXTRA_TEXT)?.let { text ->
            if (ttsInitialized) {
                speakText(text)
            } else {
                pendingTexts.add(text)
            }
        }
        return START_REDELIVER_INTENT
    }

    override fun onInit(status: Int) {
        Log.d("WidgetTTSService", "TTS initialized successfully (status=$status)")

        if (status == TextToSpeech.SUCCESS) {
            val locale = getLocaleForLanguage(selectedLanguage)
            val result = tts?.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language not supported, cleanup
                ttsInitialized = false
                pendingTexts.clear()
                return
            }
            ttsInitialized = true
            
            // Speak the most recent pending text (if any)
            if (pendingTexts.isNotEmpty()) {
                speakText(pendingTexts.last())
                pendingTexts.clear()
            }
        } else {
            // TTS initialization failed, cleanup pending texts
            ttsInitialized = false
            pendingTexts.clear()
        }
    }

    private fun speakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun loadLanguagePreference() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        selectedLanguage = prefs.getString(Constants.LANGUAGE_KEY, "en") ?: "en"
    }

    private fun getLocaleForLanguage(language: String): Locale {
        return when (language) {
            "es" -> Locale("es")
            "de" -> Locale("de")
            else -> Locale.ENGLISH
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
