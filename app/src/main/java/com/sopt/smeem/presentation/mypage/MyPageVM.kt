package com.sopt.smeem.presentation.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.sopt.smeem.domain.model.Day
import com.sopt.smeem.domain.model.MyPage
import com.sopt.smeem.domain.model.PushAlarm
import com.sopt.smeem.domain.repository.LocalRepository
import com.sopt.smeem.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MyPageVM @Inject constructor(
    private val localRepository: LocalRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _response: MutableLiveData<MyPage> = MutableLiveData<MyPage>()
    val response: LiveData<MyPage>
        get() = _response

    var isTimeSet: Boolean = false
    var days: MutableSet<Day> = mutableSetOf()

    fun getData(onError: (Throwable) -> Unit) {

    }

    fun changePushAlarm(hasAlarm: Boolean, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.editPushAlarm(PushAlarm(hasAlarm = hasAlarm))
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun isDaySelected(content: String) = days.contains(Day.from(content))

    fun clearLocal() {
        viewModelScope.launch {
            localRepository.clear()
        }
    }

    fun withdrawal() {
        viewModelScope.launch {
            userRepository.deleteUser()
            UserApiClient.instance.unlink {}
            localRepository.clear()
        }
    }
}