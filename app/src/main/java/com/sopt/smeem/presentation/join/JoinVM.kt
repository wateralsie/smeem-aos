package com.sopt.smeem.presentation.join

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sopt.smeem.domain.model.Authentication
import com.sopt.smeem.domain.repository.LocalRepository
import com.sopt.smeem.domain.repository.UserRepository
import com.sopt.smeem.module.Anonymous
import com.sopt.smeem.presentation.join.EntranceSelection.MARKETING
import com.sopt.smeem.presentation.join.EntranceSelection.PERSONAL
import com.sopt.smeem.presentation.join.EntranceSelection.SERVICE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinVM @Inject constructor(
    private val localRepository: LocalRepository,
    @Anonymous private val userRepository: UserRepository,
) : ViewModel() {
    private val _joinSucceed = MutableLiveData(false)
    val joinSucceed: LiveData<Boolean>
        get() = _joinSucceed


    private val selections = mutableSetOf<EntranceSelection>()
    fun selectAll() {
        selections.addAll(EntranceSelection.getAll())
    }

    fun cancelAll() {
        selections.clear()
    }

    fun cancel(selection: EntranceSelection) {
        selections.remove(selection)
    }

    fun add(selection: EntranceSelection) {
        selections.add(selection)
    }

    fun allSelected() = selections.size == EntranceSelection.getAll().size
    fun canGoNext() = selections.containsAll(setOf(SERVICE, PERSONAL))
    fun getSelected() = selections
    fun registerNicknameAndAcceptance(
        nickname: String,
        selected: Set<EntranceSelection>,
        accessToken: String,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                userRepository.registerUserInfo(accessToken, nickname, selected.contains(MARKETING))
                    .run { _joinSucceed.value = true }
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun saveTokenInLocal(accessToken: String, refreshToken: String) {
        viewModelScope.launch {
            localRepository.setAuthentication(
                Authentication(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    isAnonymous = false,
                )
            )
        }
    }
}

enum class EntranceSelection {
    SERVICE,
    PERSONAL,
    MARKETING,
    ;

    var id: Int = 0
    var selected: Boolean = false

    companion object {
        fun getAll(): Set<EntranceSelection> = setOf(SERVICE, PERSONAL, MARKETING)
        fun findById(id: Int) = getAll().find { it.id == id } ?: throw IllegalStateException()
    }
}