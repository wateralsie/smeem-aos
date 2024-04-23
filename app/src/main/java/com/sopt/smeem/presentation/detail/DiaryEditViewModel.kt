package com.sopt.smeem.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.sopt.smeem.domain.dto.PatchDiaryRequestDto
import com.sopt.smeem.domain.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryEditViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    var diaryId: Long = -1

    val diary = MutableLiveData<String>()
    val isValidDiary: LiveData<Boolean> = diary.map { isValidDiaryFormat(it) }

    fun editDiary(onSuccess: (Unit) -> Unit, onError: (Throwable) -> Unit) {
        diary.value?.let { content ->
            viewModelScope.launch {
                requestToServer(
                    PatchDiaryRequestDto(diaryId, content),
                    onSuccess,
                    onError
                )
            }
        }
    }

    private suspend fun requestToServer(
        dto: PatchDiaryRequestDto,
        onSuccess: (Unit) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            diaryRepository.patchDiary(dto).run { onSuccess(Unit) }
        } catch (t: Throwable) {
            onError(t)
        }
    }

    private fun isValidDiaryFormat(diary: String): Boolean {
        if (diary.replace("[^a-zA-Z]".toRegex(), "").isNotEmpty()) {
            return true
        }
        return false
    }
}