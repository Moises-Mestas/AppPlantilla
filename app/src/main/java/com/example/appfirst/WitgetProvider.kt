package com.example.appfirst

import android.app.PendingIntent
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast

class MyWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // Establecer la imagen
            views.setImageViewResource(R.id.widgetImage, R.drawable.prueba1)

            // Acción cuando se haga clic en el widget
            val toastIntent = Intent(context, MyWidgetProvider::class.java)
            toastIntent.action = "com.example.appfirst.CLICK_ACTION"
            val pendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.widgetImage, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // Manejar la acción del clic
        if (intent.action == "com.example.appfirst.CLICK_ACTION") {
            Toast.makeText(context, "¡Botón del Widget clickeado!", Toast.LENGTH_SHORT).show()
        }
    }
}
