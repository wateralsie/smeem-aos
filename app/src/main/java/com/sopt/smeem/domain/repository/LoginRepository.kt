package com.sopt.smeem.domain.repository

import com.sopt.smeem.SocialType
import com.sopt.smeem.domain.ApiResult
import com.sopt.smeem.domain.dto.LoginResultDto

interface LoginRepository {
    suspend fun execute(
        accessToken: String,
        socialType: SocialType,
        fcmToken: String
    ): ApiResult<LoginResultDto>

    suspend fun checkNicknameDuplicated(nickname: String): ApiResult<Boolean>
}