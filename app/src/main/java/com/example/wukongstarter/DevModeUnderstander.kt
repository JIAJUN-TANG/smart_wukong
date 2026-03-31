package com.example.wukongstarter

import com.ubtrobot.async.Deferred
import com.ubtrobot.speech.AbstractUnderstander
import com.ubtrobot.speech.SpeechFulfillment
import com.ubtrobot.speech.SpeechIntent
import com.ubtrobot.speech.UnderstandingException
import com.ubtrobot.speech.UnderstandingOption
import com.ubtrobot.speech.UnderstandingResult

class DevModeUnderstander : AbstractUnderstander() {
    override fun understand(
        option: UnderstandingOption,
        deferred: Deferred<UnderstandingResult, UnderstandingException>
    ) {
        val result = UnderstandingResult.Builder()
            .setInputText(option.inputText ?: DevModeRecognizer.DEV_MODE_TEXT)
            .setSource("dev-mode")
            .setIntent(
                SpeechIntent.Builder("dev_mode")
                    .setScore(1.0f)
                    .build()
            )
            .setSpeechFulfillment(
                SpeechFulfillment.Builder(SpeechFulfillment.TYPE_TEXT)
                    .setText("你好，我是您的悟空机器人。如果您能听到这个自定义回复，说明唤醒和识别均已成功！")
                    .build()
            )
            .build()

        notifyUnderstandingResult(result)
        deferred.resolve(result)
    }
}
