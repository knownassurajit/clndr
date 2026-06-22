package com.knownassurajit.clndr_widget.core.domain.di

import com.knownassurajit.clndr_widget.core.datetime.LifeGridCalculator
import com.knownassurajit.clndr_widget.core.datetime.ProgressEngine
import com.knownassurajit.clndr_widget.core.datetime.SunriseSunsetEngine
import com.knownassurajit.clndr_widget.core.domain.repository.MilestonesRepository
import com.knownassurajit.clndr_widget.core.domain.repository.MilestonesRepositoryImpl
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
