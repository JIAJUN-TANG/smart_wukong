package com.example.wukongstarter

import android.content.Intent
import com.ubtrobot.mini.speech.framework.AbstractSpeechApplication
import com.ubtrobot.speech.SpeechService
import com.ubtrobot.speech.SpeechSettings
import com.ubtrobot.service.ServiceModules

class WukongApp : AbstractSpeechApplication() {
    override fun onCreate() {
        super.onCreate()

        ServiceModules.declare(SpeechSettings::class.java) { _, notifier ->
            notifier.notifyModuleCreated(DemoSpeech.INSTANCE.createSpeechSettings(this))
        }

        ServiceModules.declare(SpeechService::class.java) { _, notifier ->
            notifier.notifyModuleCreated(DemoSpeech.INSTANCE.getSpeechService(this))
        }

        try {
            startService(Intent(this, DemoMasterService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
