package com.pinkmandarin.mathmove.di

import com.pinkmandarin.mathmove.data.repository.AuthRepositoryImpl
import com.pinkmandarin.mathmove.data.repository.GameRepositoryImpl
import com.pinkmandarin.mathmove.data.repository.RankingRepositoryImpl
import com.pinkmandarin.mathmove.domain.repository.AuthRepository
import com.pinkmandarin.mathmove.domain.repository.GameRepository
import com.pinkmandarin.mathmove.domain.repository.RankingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        gameRepositoryImpl: GameRepositoryImpl
    ): GameRepository

    @Binds
    @Singleton
    abstract fun bindRankingRepository(
        rankingRepositoryImpl: RankingRepositoryImpl
    ): RankingRepository
}
