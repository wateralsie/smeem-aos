package com.sopt.smeem.presentation.mypage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sopt.smeem.domain.model.Day
import com.sopt.smeem.domain.model.Training
import com.sopt.smeem.domain.model.TrainingTime
import com.sopt.smeem.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTrainingTimeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var originalTime: TrainingTime = TrainingTime(
        setOf(Day.MON, Day.TUE, Day.WED, Day.THU, Day.FRI),
        DEFAULT_HOUR,
        DEFAULT_MINUTE
    )

    // 서버로 보내기 위한 선택시간 저장 변수
    var days: MutableSet<Day> = mutableSetOf()
    var hour = MutableLiveData<Int>()
    var minute = MutableLiveData<Int>()

    fun isDaySelected(content: String) = days.contains(Day.from(content))
    fun addDay(content: String) = days.add(Day.from(content))
    fun removeDay(content: String) = days.remove(Day.from(content))

    fun canConfirmEdit() =
        days.isNotEmpty() && (TrainingTime(days, hour.value!!, minute.value!!) != originalTime)

    fun sendServer(onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.editTraining(
                    Training(
                        trainingTime = TrainingTime(
                            days,
                            hour.value!!,
                            minute.value!!
                        )
                    )
                )
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    companion object {
        const val DEFAULT_HOUR = 22
        const val DEFAULT_MINUTE = 0
    }
}