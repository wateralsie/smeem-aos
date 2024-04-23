package com.sopt.smeem.domain.model

import com.sopt.smeem.domain.dto.TrainingGoalDto

data class MyPage(
    val username: String,
    val myPageBadge: MyPageBadge,
    val hasPushAlarm: Boolean,
    val goal: TrainingGoalDto,
    val language: Language,
    val trainingTime: TrainingTime
)
