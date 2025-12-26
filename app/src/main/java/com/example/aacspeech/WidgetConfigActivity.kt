package com.example.aacspeech

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class WidgetConfigActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var columnSpinner: Spinner
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(R.layout.activity_widget_config)

        // Get the widget ID from the intent
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        columnSpinner = findViewById(R.id.columnSpinner)
        btnSave = findViewById(R.id.btnSaveWidget)

        // Setup spinner with column options (1-5 columns)
        val columnOptions = arrayOf("1", "2", "3", "4", "5")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, columnOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        columnSpinner.adapter = adapter
        
        // Set default to 2 columns
        columnSpinner.setSelection(1)

        btnSave.setOnClickListener {
            val columnCount = columnSpinner.selectedItem.toString().toInt()
            saveColumnPref(this, appWidgetId, columnCount)

            val appWidgetManager = AppWidgetManager.getInstance(this)
            AppWidget.updateAppWidget(this, appWidgetManager, appWidgetId)

            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }

    companion object {
        private const val PREFS_NAME = "com.example.aacspeech.AppWidget"
        private const val PREF_PREFIX_KEY = "widget_column_"

        fun saveColumnPref(context: Context, appWidgetId: Int, columnCount: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            prefs.edit().putInt(PREF_PREFIX_KEY + appWidgetId, columnCount).apply()
        }

        fun loadColumnPref(context: Context, appWidgetId: Int): Int {
            val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            return prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 2) // Default to 2 columns
        }

        fun deleteColumnPref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            prefs.edit().remove(PREF_PREFIX_KEY + appWidgetId).apply()
        }
    }
}
