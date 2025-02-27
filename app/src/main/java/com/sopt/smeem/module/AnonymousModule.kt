package com.sopt.smeem.module

import com.sopt.smeem.data.repository.LoginRepositoryImpl
import com.sopt.smeem.data.repository.TrainingRepositoryImpl
import com.sopt.smeem.data.repository.UserRepositoryImpl
import com.sopt.smeem.data.repository.VersionRepositoryImpl
import com.sopt.smeem.data.service.LoginService
import com.sopt.smeem.data.service.MyBadgeService
import com.sopt.smeem.data.service.TrainingService
import com.sopt.smeem.data.service.UserService
import com.sopt.smeem.data.service.VersionService
import com.sopt.smeem.domain.repository.LoginRepository
import com.sopt.smeem.domain.repository.TrainingRepository
import com.sopt.smeem.domain.repository.UserRepository
import com.sopt.smeem.domain.repository.VersionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AnonymousModule {
    @Provides
    @ViewModelScoped
    @Anonymous
    fun anonymousMemberRepository(networkModule: NetworkModule): UserRepository =
        UserRepositoryImpl(
            userService = networkModule.apiServerRetrofitForAnonymous.create(UserService::class.java),
            myBadgeService = networkModule.apiServerRetrofitForAnonymous.create(MyBadgeService::class.java)
        )

    @Provides
    @ViewModelScoped
    @Anonymous
    fun loginRepository(networkModule: NetworkModule): LoginRepository =
        LoginRepositoryImpl(networkModule.apiServerRetrofitForAnonymous.create(LoginService::class.java))

    @Provides
    @ViewModelScoped
    @Anonymous
    fun trainingRepository(networkModule: NetworkModule): TrainingRepository =
        TrainingRepositoryImpl(
            networkModule.apiServerRetrofitForAnonymous.create(TrainingService::class.java)
        )

    @Provides
    @ViewModelScoped
    @Anonymous
    fun versionRepository(networkModule: NetworkModule): VersionRepository =
        VersionRepositoryImpl(
            networkModule.apiServerRetrofitForAnonymous.create(VersionService::class.java)
        )
}