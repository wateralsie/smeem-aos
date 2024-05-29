package com.sopt.smeem.domain.dto

import java.time.LocalDate
import java.time.LocalTime

data class GetDiarySummariesDto(
    val diaries: Map<LocalDate, GetDiarySummaryDto>,
    val has30Past: Boolean
)

data class GetDiarySummaryDto(
    val id: Long,
    val content: String,
    val createdAt: LocalTime,
)