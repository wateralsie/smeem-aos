package com.sopt.smeem.domain.dto

import com.sopt.smeem.data.model.response.LoginResponse

data class LoginResultDto(
    val apiAccessToken: String,
    val apiRefreshToken: String,
    val isRegistered: Boolean,
    val isPlanRegistered: Boolean
) {
    companion object {
        fun from(response: LoginResponse): LoginResultDto =
            LoginResultDto(
                apiAccessToken = response.accessToken,
                apiRefreshToken = response.refreshToken,
                isRegistered = response.isRegistered,
                isPlanRegistered = response.hasPlan
            )
    }
}