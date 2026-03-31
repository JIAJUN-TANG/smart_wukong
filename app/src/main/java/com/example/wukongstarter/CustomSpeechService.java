package com.example.wukongstarter;

import com.ubtechinc.mini.weinalib.wakeup.WeiNaWakeUpHelper;
import com.ubtrobot.master.service.MasterSystemService;

public class CustomSpeechService extends MasterSystemService {
  @Override protected void onServiceCreate() {
    super.onServiceCreate();
    //If you want to use the WeiNa wake-up module(wakeup-5.0.0.aar), please call the initialization method first
    // The initialization of the wake-up module requires an available network
    WeiNaWakeUpHelper.get().initialize();

    //init speech modules
    // Note: Assuming CustomSpeech is available in this package or imported.
    // In the previous version it was com.example.wukongstarter.CustomSpeechSevice.java 
    // but the package was com.ubtrobot.mini.speech.framework.demo.
    // Adjusting to current package.
    CustomSpeech.INSTANCE.init(this);
  }

  @Override protected void onServiceDestroy() {
    super.onServiceDestroy();
  }
}