package com.example.wukongstarter

import android.app.Application
import com.ubtrobot.mini.SDKInit
import com.ubtrobot.mini.properties.sdk.Path
import com.ubtrobot.mini.properties.sdk.PropertiesApi

class WukongApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            PropertiesApi.setRootPath(Path.DIR_MINI_FILES_SDCARD_ROOT)
            SDKInit.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
