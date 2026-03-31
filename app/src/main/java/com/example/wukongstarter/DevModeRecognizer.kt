package com.example.wukongstarter

import android.content.Context
import com.ubtrobot.speech.AbstractRecognizer
import com.ubtrobot.speech.RecognitionOption
import com.ubtrobot.speech.RecognitionResult
import kotlin.concurrent.thread

class DevModeRecognizer(
    private val context: Context
) : AbstractRecognizer() {
    @Volatile
    private var stopped = false

    override fun startRecognizing(option: RecognitionOption) {
        stopped = false

        thread(name = "dev-mode-recognizer") {
            Thread.sleep(120)
            if (stopped) {
                return@thread
            }

            // Development placeholder: wire your ASR/LLM pipeline here later.
            resolveRecognizing(
                RecognitionResult.Builder(DEV_MODE_TEXT).build()
            )
        }
    }

    override fun stopRecognizing() {
        stopped = true
    }

    companion object {
        const val DEV_MODE_TEXT = "开发模式"
    }
}
