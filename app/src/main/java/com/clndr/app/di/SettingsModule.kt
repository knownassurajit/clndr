package com.clndr.app.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext ctx: Context): AlarmManager =
        ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext ctx: Context): NotificationManager =
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun provideMilestoneScheduler(
        impl: com.clndr.app.reminders.MilestoneReminderScheduler
    ): com.clndr.core.domain.scheduler.MilestoneScheduler = impl

    @Provides
    @Singleton
    fun provideCalendarManager(
        impl: com.clndr.app.reminders.CalendarMirror
    ): com.clndr.core.domain.calendar.CalendarManager = impl
}
