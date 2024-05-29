package com.sopt.smeem.domain.dto

data class WriteDiaryResponseDto(
    val diaryId: Long,
    val retrievedBadgeList: List<RetrievedBadgeDto>,
)