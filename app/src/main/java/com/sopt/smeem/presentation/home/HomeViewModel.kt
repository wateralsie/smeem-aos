package com.sopt.smeem.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sopt.smeem.domain.model.Date
import com.sopt.smeem.domain.model.DiarySummary
import com.sopt.smeem.domain.repository.DiaryRepository
import com.sopt.smeem.presentation.home.calendar.core.CalendarIntent
import com.sopt.smeem.presentation.home.calendar.core.Period
import com.sopt.smeem.util.DateUtil
import com.sopt.smeem.util.getNextDates
import com.sopt.smeem.util.getRemainingDatesInMonth
import com.sopt.smeem.util.getRemainingDatesInWeek
import com.sopt.smeem.util.getWeekStartDate
import com.sopt.smeem.util.toYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
) : ViewModel() {
    lateinit var nowBadge: String

    /***** variables *****/

    // badge
    var isFirstBadge: Boolean = true
    val badgeName = MutableLiveData<String>()
    val badgeImageUrl = MutableLiveData<String>()

    // diary
    private val _diaryDateList: MutableLiveData<List<LocalDate>> = MutableLiveData(emptyList())
    val diaryDateList: LiveData<List<LocalDate>>
        get() = _diaryDateList

    private val _diaryList: MutableLiveData<DiarySummary?> = MutableLiveData()
    val diaryList: LiveData<DiarySummary?>
        get() = _diaryList

//    val isCorrectionAvailable = MutableLiveData<Boolean>()

    // calendar
    private val _visibleDates =
        MutableStateFlow(
            calculateWeeklyCalendarDays(
                startDate = LocalDate.now().getWeekStartDate().minusWeeks(1)
            )
        )
    val visibleDates: StateFlow<Array<List<Date>>> = _visibleDates

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    val currentMonth: StateFlow<YearMonth>
        get() = isCalendarExpanded.zip(visibleDates) { isExpanded, dates ->
            when {
                isExpanded -> dates[1][dates[1].size / 2].day.toYearMonth()
                dates[1].count { it.day.month == dates[1][0].day.month } > 3 -> dates[1][0].day.toYearMonth()
                else -> dates[1][dates[1].size - 1].day.toYearMonth()
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, LocalDate.now().toYearMonth())

    private val _isCalendarExpanded = MutableStateFlow(false)
    val isCalendarExpanded: StateFlow<Boolean> = _isCalendarExpanded


    /***** functions *****/

    // diary
    suspend fun getDates(startDate: LocalDate, period: Period): List<LocalDate> {
        var diaryDates: List<LocalDate> = emptyList()
        val start = when(period) {
            Period.WEEK -> startDate.plusWeeks(1)
            Period.MONTH -> startDate.plusMonths(1)
        }
        val end = when(period) {
            Period.WEEK -> start.plusDays(6)
            Period.MONTH -> start.plusMonths(1).minusDays(1)
        }
        val startAsString = DateUtil.WithServer.asStringOnlyDate(start)
        val endAsString = DateUtil.WithServer.asStringOnlyDate(end)

        kotlin.runCatching {
            Timber.tag("server called!")
            diaryRepository.getDiaries(startAsString, endAsString)
        }.fold({
            diaryDates = it.getOrNull()?.diaries?.keys?.toList() ?: emptyList()
        }, {
            Timber.e(it.message.toString())
        })
        return diaryDates
    }

    suspend fun getDateDiary(date: LocalDate): DiarySummary? {
        var diaries: DiarySummary? = null
        val dateAsString = DateUtil.WithServer.asStringOnlyDate(date)
        kotlin.runCatching {
            diaryRepository.getDiaries(start = dateAsString, end = dateAsString)
        }.fold({
            diaries = it.getOrNull()?.diaries?.values?.firstOrNull()
        }, {
            Timber.e(it.message.toString())
        })
        return diaries
    }

    fun setBadgeInfo(name: String, imageUrl: String, isFirst: Boolean) {
        badgeName.value = name
        badgeImageUrl.value = imageUrl
        isFirstBadge = isFirst
    }

    // calendar
    fun onIntent(intent: CalendarIntent) {
        Timber.tag("what intent?").d(intent.toString())
        when (intent) {
            CalendarIntent.ExpandCalendar -> {
                calculateCalendarDates(
                    startDate = currentMonth.value.minusMonths(1).atDay(1),
                    period = Period.MONTH
                )
                _isCalendarExpanded.value = true
            }

            CalendarIntent.CollapseCalendar -> {
                calculateCalendarDates(
                    startDate = calculateWeeklyCalendarVisibleStartDay()
                        .getWeekStartDate()
                        .minusWeeks(1),
                    period = Period.WEEK
                )
                _isCalendarExpanded.value = false
            }

            is CalendarIntent.LoadNextDates -> {
                calculateCalendarDates(intent.startDate, intent.period)
            }

            is CalendarIntent.SelectDate -> {
                viewModelScope.launch {
                    Timber.d(intent.date.toString())
                    _diaryList.postValue(getDateDiary(intent.date))
                    _selectedDate.emit(intent.date)
                }
            }
        }
    }

    private fun calculateCalendarDates(
        startDate: LocalDate,
        period: Period = Period.WEEK
    ) {
        Timber.d("calculateCalendarDates invoked!")
        viewModelScope.launch(Dispatchers.IO) {
            _diaryDateList.postValue(
                when (period) {
                    Period.WEEK -> {
                        Timber.d("getDates(week)")
                        getDates(startDate, Period.WEEK)
                    }
                    Period.MONTH -> {
                        Timber.d("getDates(month)")
                        getDates(startDate, Period.MONTH)
                    }
                }
            )
            _visibleDates.emit(
                when (period) {
                    Period.WEEK -> {
                        Timber.d("calculateWeeklyCalendarDays invoked!")
                        calculateWeeklyCalendarDays(startDate)
                    }
                    Period.MONTH -> {
                        Timber.d("calculateMonthlyCalendarDays invoked!")
                        calculateMonthlyCalendarDays(startDate)
                    }
                }
            )
        }
    }

    private fun calculateWeeklyCalendarVisibleStartDay(): LocalDate {
        val halfOfMonth = visibleDates.value[1][visibleDates.value[1].size / 2]
        val visibleMonth = YearMonth.of(halfOfMonth.day.year, halfOfMonth.day.month)
        return if (selectedDate.value.month == visibleMonth.month && selectedDate.value.year == visibleMonth.year)
            selectedDate.value
        else visibleMonth.atDay(1)
    }

    private fun calculateWeeklyCalendarDays(startDate: LocalDate): Array<List<Date>> {
        val dateList = mutableListOf<Date>()

        startDate.getNextDates(21).map {
            dateList.add(Date(it, true, diaryDateList.value?.contains(it) == true ))
        }
        return Array(3) {
            dateList.slice(it * 7 until (it + 1) * 7)
        }
    }

    private fun calculateMonthlyCalendarDays(startDate: LocalDate): Array<List<Date>> {
        return Array(3) { monthIndex ->
            val monthFirstDate = startDate.plusMonths(monthIndex.toLong())
            val monthLastDate = monthFirstDate.plusMonths(1).minusDays(1)

            monthFirstDate.getWeekStartDate().let { weekBeginningDate ->
                if (weekBeginningDate != monthFirstDate) {
                    weekBeginningDate.getRemainingDatesInMonth().map {
                        Date(it, false, diaryDateList.value?.contains(it) == true)
                    }
                } else {
                    listOf()
                } +
                        monthFirstDate.getNextDates(monthFirstDate.month.length(monthFirstDate.isLeapYear))
                            .map {
                                Date(it, true, diaryDateList.value?.contains(it) == true )
                            } +
                        monthLastDate.getRemainingDatesInWeek().map {
                            Date(it, false, diaryDateList.value?.contains(it) == true )
                        }
            }
        }
    }
}
