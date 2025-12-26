package com.example.aacspeech

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WidgetRemoteViewsFactory(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val appWidgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )
    private var items = mutableListOf<GridItem>()
    private val gson = Gson()
    private var selectedLanguage: String = "en"

    override fun onCreate() {
        loadLanguagePreference()
        loadItems()
    }

    override fun onDataSetChanged() {
        loadLanguagePreference()
        loadItems()
    }

    override fun onDestroy() {
        items.clear()
    }

    override fun getCount(): Int = items.size

    override fun getViewAt(position: Int): RemoteViews? {
        if (position >= items.size) {
            return null
        }

        val item = items[position]
        val views = RemoteViews(context.packageName, R.layout.widget_grid_item)
        
        val displayText = item.getTextForLanguage(selectedLanguage)
        views.setTextViewText(R.id.widgetItemText, displayText)
        views.setInt(R.id.widgetItemContainer, "setBackgroundColor", item.backgroundColor)

        // Intent for item click to trigger TTS
        val fillInIntent = Intent().apply {
            putExtra(WidgetTTSService.EXTRA_TEXT, displayText)
        }
        views.setOnClickFillInIntent(R.id.widgetItemContainer, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true

    private fun loadItems() {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(Constants.ITEMS_KEY, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<GridItem>>() {}.type
            val loadedItems: MutableList<GridItem>? = gson.fromJson(json, type)
            items.clear()
            loadedItems?.let { items.addAll(it) }
        }
    }

    private fun loadLanguagePreference() {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        selectedLanguage = prefs.getString(Constants.LANGUAGE_KEY, "en") ?: "en"
    }
}
