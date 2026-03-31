package com.example.wukongstarter

import android.content.Context
import com.ubtechinc.mini.weinalib.WeiNaRecorder
import com.ubtechinc.mini.weinalib.wakeup.WeiNaWakeUpDetector
import com.ubtrobot.mini.speech.framework.SpeechSettingStub
import com.ubtrobot.speech.CompositeSpeechService

class DemoSpeech private constructor() {
    companion object {
        val INSTANCE by lazy { DemoSpeech() }
    }

    @Volatile
    private var speechService: CompositeSpeechService? = null

    fun getSpeechService(context: Context): CompositeSpeechService {
        return speechService ?: synchronized(this) {
            speechService ?: createSpeechService(context.applicationContext).also {
                speechService = it
            }
        }
    }

    fun createSpeechSettings(context: Context): SpeechSettingStub {
        return SpeechSettingStub(context.applicationContext)
    }

    private fun createSpeechService(context: Context): CompositeSpeechService {
        val wakeUpDetector = WeiNaWakeUpDetector(WeiNaRecorder(false))

        return CompositeSpeechService.Builder()
            .setWakeUpDetector(wakeUpDetector)
            .setRecognizer(DevModeRecognizer(context.applicationContext))
            .setUnderstander(DevModeUnderstander())
            .build()
    }
}
