package com.example.wukongstarter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != ACTION_QUICKBOOT_POWERON &&
            action != Intent.ACTION_REBOOT
        ) {
            return
        }

        try {
            ContextCompat.startForegroundService(context, Intent(context, WukongService::class.java))
        } catch (e: Exception) {
            Log.e(TAG, "Unable to start WukongService from boot receiver", e)
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
        private const val ACTION_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"
    }
}
