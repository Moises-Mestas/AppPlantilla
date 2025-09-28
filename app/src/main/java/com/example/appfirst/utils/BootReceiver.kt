package com.example.appfirst.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_REBOOT ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {

            Log.d("BootReceiver", "Dispositivo reiniciado, reprogramando alarmas...")

            val alarmHelper = AlarmHelper(context)
            alarmHelper.reprogramarTodasLasAlarmas()
        }
    }
}