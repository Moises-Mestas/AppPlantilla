package com.example.appfirst

import android.app.PendingIntent
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast


class YourWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Para cada widget, actualizamos su vista
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // Establecer la imagen en el widget
            views.setImageViewResource(R.id.widgetImage, R.drawable.prueba1)

            // Crea un PendingIntent para abrir la aplicación cuando el widget sea clickeado
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Asociamos el PendingIntent al widget (asegurándonos de que el ID de la imagen esté correcto)
            views.setOnClickPendingIntent(R.id.widgetImage, pendingIntent)

            // Actualizamos el widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }



    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // Acción cuando se crea el widget por primera vez
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        // Acción cuando se elimina el widget
    }
}
