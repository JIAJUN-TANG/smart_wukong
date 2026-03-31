package com.example.wukongstarter.manager

import android.content.Context
import android.util.Log
import com.example.wukongstarter.model.RobotStatus
import com.ubtrobot.masterevent.protos.SysMasterEvent
import com.ubtrobot.mini.sysevent.SysEventApi
import com.ubtrobot.mini.sysevent.event.BatteryEvent
import com.ubtrobot.mini.sysevent.receiver.BatteryEventReceiver
// Try to import SysActive related classes, if they don't exist in the official AAR, you might need to comment them out.
// import com.ubtrobot.mini.sysevent.event.SysActiveEvent
// import com.ubtrobot.mini.sysevent.receiver.SysActiveStatusEventReceiver

class EventManager(private val context: Context) {

    private var latestBatteryLevel: Int = 0

    private var batteryReceiver: BatteryEventReceiver? = null

    init {
        try {
            val data = SysEventApi.get().currentBatteryInfoSync
            latestBatteryLevel = data.level
        } catch (e: Exception) {
            Log.e("EventManager", "Failed to get initial battery info", e)
        }
    }

    // Subscribe to robot hardware and system events
    fun startSubscribe() {
        val batteryEvent = BatteryEvent.newInstance()
        batteryReceiver = object : BatteryEventReceiver() {
            override fun onReceive(event: BatteryEvent): Boolean {
                // You can pull data from event or SysEventApi depending on exact event structure
                // Official demo just returns true
                Log.d("EventManager", "Battery changed event received")
                try {
                    val data = SysEventApi.get().currentBatteryInfoSync
                    latestBatteryLevel = data.level
                } catch (e: Exception) {
                    Log.e("EventManager", "Update battery failed", e)
                }
                return true
            }
        }
        SysEventApi.get().subscribe(batteryEvent, batteryReceiver)

    }

    // Unsubscribe to avoid memory leaks
    fun stopSubscribe() {
        batteryReceiver?.let { SysEventApi.get().unsubscribe(it) }
        // activeReceiver?.let { SysEventApi.get().unsubscribe(it) }
    }

    // Get current synchronized status
    fun getCurrentStatus(): RobotStatus {
        return RobotStatus(
            batteryLevel = latestBatteryLevel
        )
    }
}
