package com.example.wukongstarter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.wukongstarter.api.RetrofitClient
import com.example.wukongstarter.manager.EventManager
import com.example.wukongstarter.model.DataSubmit
import com.google.gson.Gson
import com.ubtrobot.sys.SysApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WukongService : Service() {
    private lateinit var eventManager: EventManager
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var heartbeatJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startAsForegroundService()
        eventManager = EventManager(this)
        eventManager.startSubscribe()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startHeartbeat()
        sendStatus(getString(R.string.status_request_sent))
        return START_STICKY
    }

    private fun startHeartbeat() {
        if (heartbeatJob?.isActive == true) {
            return
        }

        val gson = Gson()
        heartbeatJob = serviceScope.launch {
            var isFirstRun = true
            while (isActive) {
                try {
                    val status = eventManager.getCurrentStatus()
                    val realRobotId = try {
                        val fullId = SysApi.get().readRobotSid() ?: "unknown"
                        if (fullId.length > 5) fullId.takeLast(5) else fullId
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to get robot ID", e)
                        "unknown"
                    }

                    val submitData = DataSubmit(
                        robotId = realRobotId,
                        dataType = if (isFirstRun) "boot" else "heartbeat",
                        content = gson.toJson(status)
                    )
                    isFirstRun = false

                    val response = RetrofitClient.api.uploadHeartbeat(submitData)
                    if (response.isSuccessful) {
                        Log.i(TAG, "Heartbeat sent successfully: ${status.batteryLevel}%")
                    } else {
                        Log.e(TAG, "Heartbeat failed with code: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to send heartbeat", e)
                }

                delay(10 * 60 * 1000L)
            }
        }
    }

    override fun onDestroy() {
        eventManager.stopSubscribe()
        serviceJob.cancel()
        super.onDestroy()
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
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
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
    }
}
