package com.example.wukongstarter

import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.ubtrobot.mini.speech.framework.AbstractSpeechApplication
import com.ubtrobot.speech.SpeechService
import com.ubtrobot.speech.SpeechSettings
import com.ubtrobot.service.ServiceModules

class WukongApp : AbstractSpeechApplication() {
    override fun onCreate() {
        super.onCreate()

        // Start DemoMasterService first - it calls DemoSpeech.init(service)
        // which initializes WakeUpDetector and creates CompositeSpeechService
        startService(Intent(this, DemoMasterService::class.java))

        ServiceModules.declare(SpeechSettings::class.java) { _, notifier ->
            notifier.notifyModuleCreated(DemoSpeech.createSpeechSettings(this))
        }

        // Speech service is created asynchronously in DemoSpeech.init(),
        // so we need to wait for it on a background thread (following official demo pattern)
        ServiceModules.declare(SpeechService::class.java) { _, notifier ->
            Thread {
                while (DemoSpeech.createSpeechService() == null) {
                    SystemClock.sleep(5)
                }
                Log.d("WukongApp", "Speech Service create ok..")
                notifier.notifyModuleCreated(DemoSpeech.createSpeechService())
            }.start()
        }

        try {
            startService(Intent(this, WukongService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
