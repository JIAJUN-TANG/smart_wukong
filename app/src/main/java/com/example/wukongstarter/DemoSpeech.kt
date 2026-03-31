package com.example.wukongstarter

import android.util.Log
import com.ubtechinc.mini.weinalib.WeiNaRecorder
import com.ubtechinc.mini.weinalib.wakeup.WeiNaWakeUpDetector
import com.ubtrobot.master.param.ProtoParam
import com.ubtrobot.master.service.MasterSystemService
import com.ubtrobot.master.transport.message.parcel.ParcelableParam
import com.ubtrobot.mini.speech.framework.ServiceConstants
import com.ubtrobot.mini.speech.framework.SpeechSettingStub
import com.ubtrobot.mini.speech.framework.WakeupAudioPlayer
import com.ubtrobot.speech.CompositeSpeechService
import com.ubtrobot.speech.SpeechConstants
import com.ubtrobot.speech.WakeUp
import com.ubtrobot.speech.parcelable.InitResult
import com.ubtrobot.speech.protos.Speech.WakeupParam

object DemoSpeech {
    private const val TAG = "DemoSpeech"

    @Volatile
    private var speechService: CompositeSpeechService? = null

    fun createSpeechSettings(context: android.content.Context): SpeechSettingStub {
        return SpeechSettingStub(context.applicationContext)
    }

    fun createSpeechService(): CompositeSpeechService? {
        return speechService
    }

    fun init(service: MasterSystemService) {
        Log.d(TAG, "init() called with MasterSystemService")

        val appContext = service.applicationContext

        // Load the wake-up sound effect in advance
        WakeupAudioPlayer.get(appContext)

        // Use WeiNa wake-up module, Awakening word is "Hey, Mini"
        val wakeUpDetector = WeiNaWakeUpDetector(WeiNaRecorder(false))
        wakeUpDetector.registerListener { wakeUp ->
            Log.d(TAG, "WeiNaWakeUpDetector WakeUp triggered: $wakeUp")
            handleWakeup(service, wakeUp)
        }

        speechService = CompositeSpeechService.Builder()
            .setWakeUpDetector(wakeUpDetector)
            .setRecognizer(DevModeRecognizer(appContext))
            .setUnderstander(DevModeUnderstander())
            .build()

        Log.d(TAG, "CompositeSpeechService created successfully")

        // Notify the framework that speech service is ready
        try {
            service.publishCarefully(
                ServiceConstants.ACTION_SPEECH_INIT_RESULT,
                ParcelableParam.create(InitResult(0))
            )
            Log.d(TAG, "Published ACTION_SPEECH_INIT_RESULT")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to publish init result", e)
        }
    }

    private fun handleWakeup(service: MasterSystemService, wakeUp: WakeUp?) {
        Log.d(TAG, "handleWakeup - publishing wakeup events to master framework")

        try {
            // This wake-up event must be published and will be used by other built-in applications
            service.publishCarefully(
                ServiceConstants.ACTION_SPEECH_WAKEUP,
                ProtoParam.create(WakeupParam.newBuilder().build())
            )
            Log.d(TAG, "Published ACTION_SPEECH_WAKEUP")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to publish ACTION_SPEECH_WAKEUP", e)
        }

        try {
            // Publish wake-up event to start the voice recognition link
            service.publishCarefully(
                SpeechConstants.ACTION_WAKE_UP,
                ParcelableParam.create(wakeUp)
            )
            Log.d(TAG, "Published ACTION_WAKE_UP")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to publish ACTION_WAKE_UP", e)
        }

        // Play the wake-up sound effect
        try {
            WakeupAudioPlayer.get(service.applicationContext).play()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to play wakeup audio", e)
        }
    }
}
