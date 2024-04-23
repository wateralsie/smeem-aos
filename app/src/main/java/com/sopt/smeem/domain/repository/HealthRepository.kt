package com.sopt.smeem.domain.repository

import com.sopt.smeem.domain.ApiResult

interface HealthRepository {
    suspend fun getHealth(): ApiResult<Unit>
}