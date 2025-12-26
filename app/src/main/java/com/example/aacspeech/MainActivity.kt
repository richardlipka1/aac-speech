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

    companion object {
        const val PREFS_NAME = "AACSpeechPrefs"
        const val ITEMS_KEY = "items"
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
                    backgroundColor = color
                )
                items.add(newItem)
                adapter.updateItems(items)
                saveItems()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.gridView)
        fabAdd = findViewById(R.id.fabAdd)

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)

        // Load saved items
        loadItems()

        // Setup adapter
        adapter = GridItemAdapter(
            this,
            items,
            onItemClick = { item ->
                speakText(item.text)
            },
            onDeleteClick = { item ->
                showDeleteConfirmation(item)
            }
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
            val result = tts?.setLanguage(Locale.US)
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
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val json = prefs.getString(ITEMS_KEY, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<GridItem>>() {}.type
            items = gson.fromJson(json, type) ?: mutableListOf()
        }
    }

    private fun saveItems() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val json = gson.toJson(items)
        prefs.edit().putString(ITEMS_KEY, json).apply()
        
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

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
