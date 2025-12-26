package com.example.aacspeech

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import java.util.*

class WidgetTTSService : Service(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var ttsInitialized = false
    private val pendingTexts = mutableListOf<String>()

    companion object {
        const val EXTRA_TEXT = "extra_text"
    }

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra(EXTRA_TEXT)?.let { text ->
            if (ttsInitialized) {
                speakText(text)
            } else {
                pendingTexts.add(text)
            }
        }
        return START_NOT_STICKY
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language not supported, cleanup
                ttsInitialized = false
                pendingTexts.clear()
                return
            }
            ttsInitialized = true
            
            // Speak any pending texts
            pendingTexts.forEach { text ->
                speakText(text)
            }
            pendingTexts.clear()
        } else {
            // TTS initialization failed, cleanup pending texts
            ttsInitialized = false
            pendingTexts.clear()
        }
    }

    private fun speakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
