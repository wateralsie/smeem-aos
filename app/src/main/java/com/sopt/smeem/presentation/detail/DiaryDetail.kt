package com.sopt.smeem.presentation.detail

data class DiaryDetail(
    val diaryId: Long,
    val topic: String?,
    val content: String,
    val createdAt: String,
    val writerUsername: String,
)