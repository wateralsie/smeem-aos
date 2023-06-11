package com.sopt.smeem.presentation.auth.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sopt.smeem.Authenticated
import com.sopt.smeem.SmeemException
import com.sopt.smeem.SocialType
import com.sopt.smeem.domain.model.Authentication
import com.sopt.smeem.domain.model.LoginResult
import com.sopt.smeem.domain.repository.AuthRepository
import com.sopt.smeem.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO : 이후 social 로그인 많아지면 social 별 VM 분리

@HiltViewModel
internal class LoginVM @Inject constructor() : ViewModel() {
    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    @Authenticated(false)
    lateinit var loginRepository: LoginRepository

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult>
        get() = _loginResult

    fun login(
        kakaoAccessToken: String,
        kakaoRefreshToken: String,
        socialType: SocialType,
        onError: (SmeemException) -> Unit
    ) {
        viewModelScope.launch {
            loginRepository.execute(
                accessToken = kakaoAccessToken,
                socialType = socialType
            )
                .onSuccess {
                    // save on local storage
                    authRepository.setAuthentication(
                        Authentication(
                            accessToken = it.apiAccessToken,
                            refreshToken = it.apiRefreshToken
                        )
                    )

                    _loginResult.value = it
                }
                .onHttpFailure { e -> onError(e) }
        }

        /* TODO : server 로그인 api 개선 시 적용

            viewModelScope.launch {
                loginRepository.execute(idToken, socialType)
                    .onSuccess {
                        _loginResult.value = it;
                        authRepository.setAuthentication(
                            Authentication(
                                accessToken = it.apiAccessToken,
                                refreshToken = it.apiRefreshToken
                            )
                        )
                    }
                    .onHttpFailure { e -> onError(e) }

            }*/
    }
}