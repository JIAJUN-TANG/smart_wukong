package com.example.wukongstarter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {
    private lateinit var startTermuxButton: Button
    private lateinit var statusTextView: TextView

    private val statusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra(WukongService.EXTRA_STATUS_MESSAGE) ?: return
            statusTextView.text = message
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startTermuxButton = findViewById(R.id.startTermuxButton)
        statusTextView = findViewById(R.id.statusTextView)
        statusTextView.text = getString(R.string.status_idle)

        startTermuxButton.setOnClickListener { startWukongService() }
        requestBatteryOptimizationIfNeeded()
        requestAudioPermissionIfNeeded()
        
        // App启动即刻执行心跳 (启动服务)
        startWukongService()
    }

    private fun requestAudioPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(WukongService.ACTION_STATUS_UPDATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(statusReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(statusReceiver, filter)
        }
    }

    override fun onStop() {
        unregisterReceiver(statusReceiver)
        super.onStop()
    }

    private fun startWukongService() {
        val serviceIntent = Intent(this, WukongService::class.java)
        try {
            ContextCompat.startForegroundService(this, serviceIntent)
            statusTextView.text = getString(R.string.status_request_sent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service", e)
            statusTextView.text = getString(R.string.status_start_failed)
            Toast.makeText(
                this,
                getString(R.string.start_termux_failed, e.message ?: "unknown"),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun requestBatteryOptimizationIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }

        val powerManager = getSystemService(PowerManager::class.java) ?: return
        if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
            return
        }

        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Battery optimization request failed", e)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
