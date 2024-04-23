package com.sopt.smeem.domain.dto

import java.time.LocalDateTime

data class GetDiaryResponseDto(
    val id: Long,
    val content: String,
    val createdAt: LocalDateTime,
    val username: String,
    val topic: String? = null,
)