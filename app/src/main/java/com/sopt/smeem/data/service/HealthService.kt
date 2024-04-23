package com.sopt.smeem.data.service

import com.sopt.smeem.data.model.response.ApiResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET

interface HealthService {
    @GET("/api/v2/test")
    suspend fun getStatus(): Response<ApiResponse<Unit>>
}