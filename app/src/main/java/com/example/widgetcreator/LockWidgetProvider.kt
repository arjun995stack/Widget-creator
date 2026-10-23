package com.example.widgetcreator

import android.app.admin.DevicePolicyManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.widget.RemoteViews
import android.widget.Toast

class LockWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        manager: AppWidgetManager,
        widgetIds: IntArray
    ) {
        widgetIds.forEach { id ->
            val intent = Intent(context, LockWidgetProvider::class.java).apply {
                action = ACTION_LOCK
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val views = RemoteViews(context.packageName, R.layout.lock_widget)
            views.setOnClickPendingIntent(R.id.lock_button, pendingIntent)
            manager.updateAppWidget(id, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action != ACTION_LOCK) return

        val policyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE)
                as DevicePolicyManager
        val admin = ComponentName(context, LockAdminReceiver::class.java)

        if (policyManager.isAdminActive(admin)) {
            policyManager.lockNow()
        } else {
            Toast.makeText(
                context,
                "Open Widget Creator and enable screen-lock permission.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        private const val ACTION_LOCK =
            "com.example.widgetcreator.ACTION_LOCK_PHONE"
    }
}
