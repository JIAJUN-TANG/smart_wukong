package com.example.wukongstarter.model

import com.google.gson.annotations.SerializedName

data class RobotStatus(
    @SerializedName("batteryLevel")
    val batteryLevel: Int
)
