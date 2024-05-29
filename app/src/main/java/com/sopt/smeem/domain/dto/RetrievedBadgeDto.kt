package com.sopt.smeem.domain.dto

import java.io.Serializable

data class RetrievedBadgeDto(
    val name: String,
    val imageUrl: String,
) : Serializable