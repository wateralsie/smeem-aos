package com.sopt.smeem.domain.dto

import com.sopt.smeem.domain.model.Day

data class MyInfoDto(
    val username: String,
    val way: String,
    val detail: String,
    val targetLang: String,
    val hasPushAlarm: Boolean,
    val trainingTime: MyTrainingTime?,
) {
    data class MyTrainingTime(
        val day: Set<Day>,
        val hour: Int,
        val minute: Int,
    )
}