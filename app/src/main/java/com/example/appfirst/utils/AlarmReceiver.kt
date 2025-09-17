package com.example.appfirst.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val titulo = intent.getStringExtra("titulo") ?: "Recordatorio"
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val notaId = intent.getIntExtra("nota_id", 0)
        val esRecordatorio = intent.getBooleanExtra("es_recordatorio", false)

        mostrarNotificacion(context, titulo, descripcion, notaId, esRecordatorio)
    }

    private fun mostrarNotificacion(
        context: Context,
        titulo: String,
        descripcion: String,
        notaId: Int,
        esRecordatorio: Boolean
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación (necesario para Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AlarmHelper.CHANNEL_ID,
                "Recordatorios de Eventos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para recordatorios de eventos del calendario"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val icono = if (esRecordatorio) android.R.drawable.ic_popup_reminder
        else android.R.drawable.ic_dialog_info

        val notification = NotificationCompat.Builder(context, AlarmHelper.CHANNEL_ID)
            .setSmallIcon(icono)
            .setContentTitle(if (esRecordatorio) "⏰ Recordatorio: $titulo" else "⏰ $titulo")
            .setContentText(descripcion.ifEmpty { "Es hora de tu evento" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .build()

        notificationManager.notify(notaId, notification)
    }
}