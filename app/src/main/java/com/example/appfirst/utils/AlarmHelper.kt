package com.example.appfirst.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.appfirst.data.local.entity.Nota
import java.util.Calendar

class AlarmHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "nota_alarm_channel"
        private const val TAG = "AlarmHelper"
        private const val REQUEST_CODE_ALARM_PERMISSION = 1001
    }

    fun programarAlarma(nota: Nota) {
        try {
            // Verificar permisos para Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (!notificationManager.areNotificationsEnabled()) {
                    Log.w(TAG, "Notificaciones no permitidas")
                    return
                }
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Verificar permisos para Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.w(TAG, "No se tienen permisos para alarmas exactas")
                    // Aquí podrías abrir la intent para solicitar permisos
                    val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                    return
                }
            }

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("titulo", nota.titulo)
                putExtra("descripcion", nota.descripcion ?: "")
                putExtra("nota_id", nota.id)
                putExtra("fecha", nota.fecha)
                putExtra("hora_inicio", nota.horaInicio)
            }

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                nota.id,
                intent,
                flags
            )

            val calendar = obtenerCalendarDesdeNota(nota)

            // Verificar si la fecha/hora ya pasó
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                Log.w(TAG, "No se programa alarma para fecha pasada: ${nota.fecha} ${nota.horaInicio}")
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }

            Log.d(TAG, "Alarma programada para: ${nota.titulo} a las ${nota.horaInicio}")

            // Programar recordatorio si está configurado
            if (nota.horaRecordatorio != null) {
                programarRecordatorio(nota)
            }

        } catch (e: SecurityException) {
            Log.e(TAG, "Error de permisos al programar alarma: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error programando alarma: ${e.message}")
        }
    }

    fun programarRecordatorio(nota: Nota) {
        try {
            // Programar recordatorio si está configurado
            nota.horaRecordatorio?.let { horaRecordatorio ->
                val alarmManager = ContextCompat.getSystemService(
                    context,
                    AlarmManager::class.java
                ) as? AlarmManager ?: return

                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("titulo", "Recordatorio: ${nota.titulo}")
                    putExtra("descripcion", "Evento en ${nota.minutosAntes} minutos")
                    putExtra("nota_id", nota.id + 1000) // ID diferente para recordatorio
                    putExtra("es_recordatorio", true)
                }

                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    nota.id + 1000, // ID diferente
                    intent,
                    flags
                )

                val calendar = obtenerCalendarDesdeNota(nota).apply {
                    // Restar minutos para el recordatorio
                    add(Calendar.MINUTE, -(nota.minutosAntes ?: 0))
                }

                if (calendar.timeInMillis > System.currentTimeMillis()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }
                    Log.d(TAG, "Recordatorio programado para: ${nota.titulo}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error programando recordatorio: ${e.message}")
        }
    }

    fun cancelarAlarma(notaId: Int) {
        try {
            val alarmManager = ContextCompat.getSystemService(
                context,
                AlarmManager::class.java
            ) as? AlarmManager ?: return

            // Cancelar alarma principal
            val intent = Intent(context, AlarmReceiver::class.java)
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notaId,
                intent,
                flags
            )
            alarmManager.cancel(pendingIntent)

            // Cancelar recordatorio también
            val pendingIntentRecordatorio = PendingIntent.getBroadcast(
                context,
                notaId + 1000,
                intent,
                flags
            )
            alarmManager.cancel(pendingIntentRecordatorio)

            Log.d(TAG, "Alarma cancelada para ID: $notaId")

        } catch (e: Exception) {
            Log.e(TAG, "Error cancelando alarma: ${e.message}")
        }
    }

    private fun obtenerCalendarDesdeNota(nota: Nota): Calendar {
        return Calendar.getInstance().apply {
            try {
                // Parsear fecha "yyyy-MM-dd"
                val fechaParts = nota.fecha.split("-")
                val año = fechaParts[0].toInt()
                val mes = fechaParts[1].toInt() - 1 // Meses: 0-11
                val dia = fechaParts[2].toInt()

                // Parsear hora "HH:mm"
                val horaParts = nota.horaInicio.split(":")
                val hora = horaParts[0].toInt()
                val minuto = horaParts[1].toInt()

                set(Calendar.YEAR, año)
                set(Calendar.MONTH, mes)
                set(Calendar.DAY_OF_MONTH, dia)
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minuto)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

            } catch (e: Exception) {
                Log.e(TAG, "Error parseando fecha/hora de la nota: ${e.message}")
                // Si hay error, establecer hora actual + 1 hora
                setTimeInMillis(System.currentTimeMillis() + 3600000)
            }
        }
    }

    fun reprogramarTodasLasAlarmas() {
        // Metodo opcional para reprogramar todas las alarmas al iniciar la app
        Log.d(TAG, "Reprogramando todas las alarmas...")
    }
}