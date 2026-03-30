package com.example.wukongstarter.model

import com.google.gson.annotations.SerializedName

data class DataSubmit(
    @SerializedName("robot_id")
    val robotId: String,
    
    @SerializedName("data_type")
    val dataType: String,
    
    @SerializedName("content")
    val content: String
)
