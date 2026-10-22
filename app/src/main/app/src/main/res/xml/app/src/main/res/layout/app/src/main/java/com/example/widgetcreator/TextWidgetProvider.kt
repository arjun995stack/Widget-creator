package com.example.widgetcreator

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews

class TextWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        manager: AppWidgetManager,
        widgetIds: IntArray
    ) {
        widgetIds.forEach { id -> updateWidget(context, manager, id) }
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val provider = ComponentName(context, TextWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(provider)
            ids.forEach { id -> updateWidget(context, manager, id) }
        }

        private fun updateWidget(
            context: Context,
            manager: AppWidgetManager,
            id: Int
        ) {
            val text = context.getSharedPreferences("widget_creator", Context.MODE_PRIVATE)
                .getString("widget_text", "Hello, Android!") ?: "Hello, Android!"

            val views = RemoteViews(context.packageName, R.layout.widget_text)
            views.setTextViewText(R.id.widget_text, text)
            manager.updateAppWidget(id, views)
        }
    }
}
