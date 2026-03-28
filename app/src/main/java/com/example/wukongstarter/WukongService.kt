package com.example.wukongstarter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class WukongService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startAsForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            launchTermux()
            stopSelf(startId)
        }.start()
        return START_NOT_STICKY
    }

    private fun startAsForegroundService() {
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun launchTermux() {
        val launchIntent = findTermuxLaunchIntent()
        if (launchIntent == null) {
            sendStatus(getString(R.string.status_termux_missing))
            Log.e(TAG, "Termux is not installed or launch intent is unavailable")
            return
        }

        try {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            startActivity(launchIntent)
            sendStatus(getString(R.string.status_termux_started))
            Log.i(TAG, "Termux launch requested")
        } catch (e: Exception) {
            sendStatus(getString(R.string.status_termux_start_failed, e.message ?: "unknown"))
            Log.e(TAG, "Failed to launch Termux", e)
        }
    }

    private fun findTermuxLaunchIntent(): Intent? {
        for (packageName in TERMUX_PACKAGE_CANDIDATES) {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                Log.i(TAG, "Found Termux package: $packageName")
                return intent
            }

            val fallbackIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = ComponentName(packageName, TERMUX_ACTIVITY_NAME)
            }
            if (fallbackIntent.resolveActivity(packageManager) != null) {
                Log.i(TAG, "Found explicit Termux activity for package: $packageName")
                return fallbackIntent
            }
        }
        return null
    }

    private fun sendStatus(message: String) {
        sendBroadcast(
            Intent(ACTION_STATUS_UPDATE).apply {
                setPackage(packageName)
                putExtra(EXTRA_STATUS_MESSAGE, message)
            }
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val activityIntent = Intent(this, MainActivity::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, flags)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        const val ACTION_STATUS_UPDATE = "com.example.wukongstarter.STATUS_UPDATE"
        const val EXTRA_STATUS_MESSAGE = "status_message"

        private const val TAG = "WukongService"
        private const val CHANNEL_ID = "wukong_service"
        private const val NOTIFICATION_ID = 1
        private val TERMUX_PACKAGE_CANDIDATES = listOf(
            "com.termux",
            "com.termux.nightly",
            "com.termux.boot"
        )
        private const val TERMUX_ACTIVITY_NAME = "com.termux.app.TermuxActivity"
    }
}
