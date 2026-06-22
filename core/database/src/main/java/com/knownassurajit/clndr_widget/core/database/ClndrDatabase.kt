package com.knownassurajit.clndr_widget.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [MilestoneEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class ClndrDatabase : RoomDatabase() {

    abstract fun milestones(): MilestoneDao

    companion object {
        const val NAME = "clndr.db"
    }
}
