package com.example.wukongstarter

import android.util.Log
import com.ubtechinc.mini.weinalib.wakeup.WeiNaWakeUpHelper
import com.ubtrobot.master.service.MasterSystemService

class DemoMasterService : MasterSystemService() {

    override fun onServiceCreate() {
        super.onServiceCreate()
        Log.d(TAG, "DemoMasterService onServiceCreate, initializing WeiNaWakeUpHelper...")

        // If you want to use the WeiNa wake-up module(wakeup-5.0.0.aar),
        // please call the initialization method first.
        // The initialization of the wake-up module requires an available network.
        WeiNaWakeUpHelper.get().initialize()

        // init speech modules
        DemoSpeech.init(this)
    }

    override fun onServiceDestroy() {
        super.onServiceDestroy()
        Log.d(TAG, "DemoMasterService onServiceDestroy")
    }

    companion object {
        private const val TAG = "DemoMasterService"
    }
}
