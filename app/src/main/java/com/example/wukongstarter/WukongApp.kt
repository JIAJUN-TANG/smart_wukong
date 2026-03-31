package com.example.wukongstarter

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.SystemClock
import android.util.Log
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility
import com.iflytek.cloud.msc.util.log.DebugLog
import com.ubtech.utilcode.utils.Utils
import com.ubtech.utilcode.utils.thread.ThreadPool
import com.ubtrobot.master.log.InfrequentLoggerFactory
import com.ubtrobot.mini.speech.framework.AbstractSpeechApplication
import com.ubtrobot.speech.SpeechService
import com.ubtrobot.speech.SpeechSettings
import com.ubtrobot.service.ServiceModules
import com.ubtrobot.ulog.FwLoggerFactory2
import com.ubtrobot.ulog.logger.android.AndroidLoggerFactory

class WukongApp : AbstractSpeechApplication() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        initializeSpeechSdk()

        startService(Intent(this, CustomSpeechService::class.java))

        ServiceModules.declare(SpeechSettings::class.java) { _, notifier ->
            notifier.notifyModuleCreated(DemoSpeech.createSpeechSettings())
        }

        ServiceModules.declare(SpeechService::class.java) { _, notifier ->
            ThreadPool.runOnNonUIThread {
                while (DemoSpeech.createSpeechService() == null) {
                    SystemClock.sleep(5)
                }
                Log.d(TAG, "Speech service created")
                notifier.notifyModuleCreated(DemoSpeech.createSpeechService())
            }
        }

        try {
            startService(Intent(this, WukongService::class.java))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start WukongService", e)
        }
    }

    private fun initializeSpeechSdk() {
        val params = buildString {
            append("appid=${getString(R.string.app_id)}")
            append(",")
            append("${SpeechConstant.ENGINE_MODE}=${SpeechConstant.MODE_MSC}")
        }
        SpeechUtility.createUtility(this, params)
        DebugLog.setLogLevel(DebugLog.LOG_LEVEL.none)
        FwLoggerFactory2.setup(
            if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                AndroidLoggerFactory()
            } else {
                InfrequentLoggerFactory()
            }
        )
    }

    companion object {
        private const val TAG = "WukongApp"
    }
}
