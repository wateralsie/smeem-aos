package com.sopt.smeem.data.model.response

data class ApiResponse<DATA>(
    val status: Int?,
    val success: Boolean,
    val message: String?,
    val data: DATA,
)
