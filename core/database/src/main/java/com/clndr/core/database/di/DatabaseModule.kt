package com.clndr.core.database.di

import android.content.Context
import androidx.room.Room
import com.clndr.core.database.ClndrDatabase
import com.clndr.core.database.Migrations
import com.clndr.core.database.MilestoneDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideClndrDatabase(@ApplicationContext context: Context): ClndrDatabase =
        Room.databaseBuilder(context, ClndrDatabase::class.java, ClndrDatabase.NAME)
            .addMigrations(*Migrations.ALL)
            .build()

    @Provides
    fun provideMilestoneDao(database: ClndrDatabase): MilestoneDao = database.milestones()
}
