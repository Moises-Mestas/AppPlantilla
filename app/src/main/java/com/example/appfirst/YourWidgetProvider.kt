package com.example.appfirst

import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast

class YourWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Para cada widget, actualizamos su vista
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // Aquí puedes actualizar el widget o configurar eventos
            views.setTextViewText(R.id.widgetText, "¡Actualización exitosa!")

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
