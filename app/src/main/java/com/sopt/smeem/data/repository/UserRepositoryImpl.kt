package com.sopt.smeem.data.repository

import com.sopt.smeem.data.datasource.MyBadgeRetriever
import com.sopt.smeem.data.model.request.PushRequest
import com.sopt.smeem.data.model.request.TrainingRequest
import com.sopt.smeem.data.model.request.UserInfoModifyingRequest
import com.sopt.smeem.data.service.UserService
import com.sopt.smeem.domain.ApiResult
import com.sopt.smeem.domain.dto.LoginResultDto
import com.sopt.smeem.domain.dto.MyInfoDto
import com.sopt.smeem.domain.dto.MyPlanDto
import com.sopt.smeem.domain.dto.MySmeemDataDto
import com.sopt.smeem.domain.dto.PostOnBoardingDto
import com.sopt.smeem.domain.model.Badge
import com.sopt.smeem.domain.model.Day
import com.sopt.smeem.domain.model.PushAlarm
import com.sopt.smeem.domain.model.Training
import com.sopt.smeem.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userService: UserService,

    private val myBadgeRetriever: MyBadgeRetriever,
) : UserRepository {
    override suspend fun registerOnBoarding(
        onBoardingDto: PostOnBoardingDto,
        loginResult: LoginResultDto
    ): ApiResult<Unit> =
        userService.patchPlanOnAnonymous(
            request = TrainingRequest(
                target = onBoardingDto.trainingGoalType,
                trainingTime = onBoardingDto.extractTime(),
                hasAlarm = onBoardingDto.hasAlarm,
            ),
            token = "Bearer ${loginResult.apiAccessToken}",
        ).let { response ->
            if (response.isSuccessful) {
                ApiResult(response.code(), Unit)
            } else {
                throw response.code().handleStatusCode()
            }
        }

    override suspend fun modifyUsername(nickname: String): ApiResult<Boolean> =
        userService.patchUserInfo(
            UserInfoModifyingRequest(username = nickname)
        ).let { response ->
            if (response.isSuccessful) {
                ApiResult(response.code(), true)
            } else {
                throw response.code().handleStatusCode()
            }
        }

    override suspend fun registerUserInfo(
        accessToken: String,
        nickname: String,
        marketingAcceptance: Boolean
    ): ApiResult<Boolean> =
        userService.patchUserInfoWithTokenFixed(
            token = "Bearer $accessToken",
            UserInfoModifyingRequest(
                username = nickname, termAccepted = marketingAcceptance
            )
        ).let { response ->
            if (response.isSuccessful) {
                ApiResult(response.code(), true)
            } else {
                throw response.code().handleStatusCode()
            }
        }

    override suspend fun getMySmeemData(): ApiResult<MySmeemDataDto> =
        userService.getMySmeemData().let { response ->
            if (response.isSuccessful) {
                response.body()!!.data.let { data ->
                    ApiResult(
                        response.code(), MySmeemDataDto(
                            visitDays = data.visitDays,
                            diaryCount = data.diaryCount,
                            diaryComboCount = data.diaryComboCount,
                            badgeCount = data.badgeCount,
                        )
                    )
                }
            } else {
                throw response.code().handleStatusCode()
            }
        }


    override suspend fun getMyPlanData(): ApiResult<MyPlanDto> =
        userService.getMyPlanData()
            .let { response ->
                if (response.isSuccessful) {
                    response.body()!!.data.let { data ->
                        ApiResult(
                            response.code(), MyPlanDto(
                                plan = data.plan,
                                goal = data.goal,
                                clearedCount = data.clearedCount,
                                clearCount = data.clearCount,
                            )
                        )
                    }
                } else {
                    throw response.code().handleStatusCode()
                }
            }


    override suspend fun getMyInfo(): ApiResult<MyInfoDto> =
        userService.getMyInfo().let { response ->
            if (response.isSuccessful) {
                response.body()!!.data.let { data ->
                    ApiResult(
                        response.code(), MyInfoDto(
                            username = data.username,
                            way = data.way,
                            detail = data.detail,
                            targetLang = data.targetLang,
                            hasPushAlarm = data.hasPushAlarm,
                            trainingTime = data.trainingTime?.let {
                                MyInfoDto.MyTrainingTime(
                                    day = it.day.split(",").map { Day.valueOf(it) }
                                        .toSet(),
                                    hour = it.hour,
                                    minute = it.minute
                                )
                            }
                        )
                    )
                }
            } else {
                throw response.code().handleStatusCode()
            }
        }

    override suspend fun registerTraining(
        accessToken: String,
        training: Training
    ): ApiResult<Unit> =
        userService.patchPlanWithFixedToken(
            token = "Bearer $accessToken",
            request = TrainingRequest(
                target = training.type,
                trainingTime = training.extractTime(),
                hasAlarm = training.hasAlarm
            )
        ).let { response ->
            if (response.isSuccessful) {
                ApiResult(response.code(), Unit)
            } else {
                throw response.code().handleStatusCode()
            }

        }

    override suspend fun editTraining(training: Training): ApiResult<Unit> =
        userService.patchPlan(
            request = TrainingRequest(
                target = training.type,
                trainingTime = training.extractTime(),
                hasAlarm = training.hasAlarm
            )
        ).let { resposne ->
            if (resposne.isSuccessful) {
                ApiResult(resposne.code(), Unit)
            } else {
                throw resposne.code().handleStatusCode()
            }
        }

    override suspend fun registerPushAlarm(
        accessToken: String,
        push: PushAlarm
    ): ApiResult<Unit> =
        userService.patchPushWithFixedToken(
            token = "Bearer $accessToken",
            request = PushRequest(push.hasAlarm)
        ).let { response ->
            if (response.isSuccessful) {
                ApiResult(response.code(), Unit)
            } else {
                throw response.code().handleStatusCode()
            }
        }

    override suspend fun editPushAlarm(push: PushAlarm): ApiResult<Unit> =
        userService.patchPush(
            request = PushRequest(push.hasAlarm)
        ).let { response ->
            if (response.isSuccessful) {
                ApiResult(response.code(), Unit)
            } else {
                throw response.code().handleStatusCode()
            }
        }

    override suspend fun deleteUser(): ApiResult<Unit> =
        userService.delete().let { response ->
            if (response.isSuccessful) {
                ApiResult(response.code(), Unit)
            } else {
                throw response.code().handleStatusCode()
            }
        }


    override suspend fun getMyBadges(): Result<List<Badge>> =
        kotlin.runCatching {
            myBadgeRetriever.getResponse()
        }.map { response ->
            response.data?.badgeTypes
                ?.flatMap { it.badges }
                ?.map { badgeResponse ->
                    Badge(
                        name = badgeResponse.name,
                        imageUrl = badgeResponse.imageUrl,
                        type = badgeResponse.type,
                    )
                }
                ?: throw IllegalArgumentException("내부 로직 구현 오류")
        }


}