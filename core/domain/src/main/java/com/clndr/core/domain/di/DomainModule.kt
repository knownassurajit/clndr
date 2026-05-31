package com.clndr.core.domain.di

import com.clndr.core.datetime.LifeGridCalculator
import com.clndr.core.datetime.ProgressEngine
import com.clndr.core.datetime.SunriseSunsetEngine
import com.clndr.core.domain.repository.MilestonesRepository
import com.clndr.core.domain.repository.MilestonesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    @Singleton
    abstract fun bindMilestonesRepository(impl: MilestonesRepositoryImpl): MilestonesRepository

    companion object {

        @Provides
        @Singleton
        fun provideClock(): Clock = Clock.systemDefaultZone()

        @Provides
        @Singleton
        fun provideLifeGridCalculator(clock: Clock): LifeGridCalculator = LifeGridCalculator(clock)

        @Provides
        @Singleton
        fun provideProgressEngine(clock: Clock): ProgressEngine = ProgressEngine(clock)

        @Provides
        @Singleton
        fun provideSunriseSunsetEngine(): SunriseSunsetEngine = SunriseSunsetEngine()
    }
}
