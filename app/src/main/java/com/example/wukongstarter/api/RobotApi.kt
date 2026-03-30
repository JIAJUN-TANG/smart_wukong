package com.example.wukongstarter.api

import com.example.wukongstarter.model.DataSubmit
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RobotApi {
    @POST("/api/v1/data")
    suspend fun uploadHeartbeat(@Body data: DataSubmit): Response<Void>
}
