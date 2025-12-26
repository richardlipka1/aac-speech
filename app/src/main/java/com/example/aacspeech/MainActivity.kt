package com.example.aacspeech

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.GridView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import android.appwidget.AppWidgetManager
import android.content.ComponentName

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var gridView: GridView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: GridItemAdapter
    private var items = mutableListOf<GridItem>()
    private var tts: TextToSpeech? = null
    private val gson = Gson()
    private var selectedLanguage: String = "en"

    companion object {
        // For backward compatibility
        const val PREFS_NAME = Constants.PREFS_NAME
        const val ITEMS_KEY = Constants.ITEMS_KEY
        const val LANGUAGE_KEY = Constants.LANGUAGE_KEY
    }

    private val addItemLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val text = data.getStringExtra("text") ?: return@let
                val color = data.getIntExtra("color", 0)
                val newItem = GridItem(
                    id = UUID.randomUUID().toString(),
                    text = text,
                    backgroundColor = color,
                    textEn = text,
                    textEs = text,
                    textDe = text
                )
                items.add(newItem)
                adapter.updateItems(items)
                adapter.setLanguage(selectedLanguage)
                saveItems()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.gridView)
        fabAdd = findViewById(R.id.fabAdd)

        // Load language preference (default to English)
        loadLanguagePreference()

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)

        // Load saved items
        loadItems()

        // Check if this is first launch and add predefined words
        checkAndAddPredefinedWords()

        // Setup adapter
        adapter = GridItemAdapter(
            this,
            items,
            onItemClick = { item ->
                speakText(item.getTextForLanguage(selectedLanguage))
            },
            onDeleteClick = { item ->
                showDeleteConfirmation(item)
            },
            currentLanguage = selectedLanguage
        )
        gridView.adapter = adapter

        // Setup FAB click listener
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            addItemLauncher.launch(intent)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = getLocaleForLanguage(selectedLanguage)
            val result = tts?.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, R.string.language_not_supported, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, R.string.tts_init_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun showDeleteConfirmation(item: GridItem) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_confirmation_title)
            .setMessage(R.string.delete_confirmation_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteItem(item)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteItem(item: GridItem) {
        items.removeIf { it.id == item.id }
        adapter.updateItems(items)
        saveItems()
        Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show()
    }

    private fun loadItems() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        val json = prefs.getString(Constants.ITEMS_KEY, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<GridItem>>() {}.type
            items = gson.fromJson(json, type) ?: mutableListOf()
        }
    }

    private fun saveItems() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        val json = gson.toJson(items)
        prefs.edit().putString(Constants.ITEMS_KEY, json).apply()
        
        // Notify all widgets to update
        updateWidgets()
    }

    private fun updateWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(this, AppWidget::class.java)
        )
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetGridView)
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

    private fun checkAndAddPredefinedWords() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean(Constants.FIRST_LAUNCH_KEY, true)
        
        if (isFirstLaunch) {
            addPredefinedWords()
            prefs.edit().putBoolean(Constants.FIRST_LAUNCH_KEY, false).apply()
        }
    }

    private fun addPredefinedWords() {
        val predefinedWords = listOf(
            GridItem(
                id = UUID.randomUUID().toString(),
                text = "Hello",
                backgroundColor = android.graphics.Color.parseColor("#90CAF9"),
                textEn = "Hello",
                textEs = "Hola",
                textDe = "Hallo"
            ),
            GridItem(
                id = UUID.randomUUID().toString(),
                text = "Bye",
                backgroundColor = android.graphics.Color.parseColor("#CE93D8"),
                textEn = "Bye",
                textEs = "Adiós",
                textDe = "Tschüss"
            ),
            GridItem(
                id = UUID.randomUUID().toString(),
                text = "Yes",
                backgroundColor = android.graphics.Color.parseColor("#A5D6A7"),
                textEn = "Yes",
                textEs = "Sí",
                textDe = "Ja"
            ),
            GridItem(
                id = UUID.randomUUID().toString(),
                text = "No",
                backgroundColor = android.graphics.Color.parseColor("#EF9A9A"),
                textEn = "No",
                textEs = "No",
                textDe = "Nein"
            ),
            GridItem(
                id = UUID.randomUUID().toString(),
                text = "How are you",
                backgroundColor = android.graphics.Color.parseColor("#FFF59D"),
                textEn = "How are you",
                textEs = "¿Cómo estás?",
                textDe = "Wie geht es dir?"
            )
        )
        
        items.addAll(predefinedWords)
        saveItems()
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
