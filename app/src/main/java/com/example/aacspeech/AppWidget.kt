package com.example.aacspeech

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

class AppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            WidgetConfigActivity.deleteColumnPref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Called when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Called when the last widget is removed
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val columnCount = WidgetConfigActivity.loadColumnPref(context, appWidgetId)
            
            val intent = Intent(context, WidgetRemoteViewsService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }

            // Select the appropriate layout based on column count
            val layoutId = when (columnCount) {
                1 -> R.layout.widget_layout_1col
                2 -> R.layout.widget_layout_2col
                3 -> R.layout.widget_layout_3col
                4 -> R.layout.widget_layout_4col
                5 -> R.layout.widget_layout_5col
                else -> R.layout.widget_layout_2col
            }

            val views = RemoteViews(context.packageName, layoutId).apply {
                setRemoteAdapter(R.id.widgetGridView, intent)
                setEmptyView(R.id.widgetGridView, R.id.emptyView)
            }

            // Intent to launch main activity when grid item is clicked
            val clickIntent = Intent(context, MainActivity::class.java)
            val clickPendingIntent = PendingIntent.getActivity(
                context, 0, clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setPendingIntentTemplate(R.id.widgetGridView, clickPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetGridView)
        }
    }
}
