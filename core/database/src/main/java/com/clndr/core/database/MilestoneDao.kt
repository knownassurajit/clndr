package com.clndr.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MilestoneDao {

    @Query("SELECT * FROM milestones ORDER BY targetEpochDay ASC")
    fun observeAll(): Flow<List<MilestoneEntity>>

    @Query("SELECT * FROM milestones WHERE targetEpochDay >= :todayEpochDay ORDER BY targetEpochDay ASC")
    fun observeUpcoming(todayEpochDay: Long): Flow<List<MilestoneEntity>>

    @Query("SELECT * FROM milestones WHERE targetEpochDay < :todayEpochDay ORDER BY targetEpochDay DESC")
    fun observePast(todayEpochDay: Long): Flow<List<MilestoneEntity>>

    @Query("SELECT * FROM milestones WHERE id = :id")
    suspend fun getById(id: Long): MilestoneEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(milestone: MilestoneEntity): Long

    @Update
    suspend fun update(milestone: MilestoneEntity)

    @Delete
    suspend fun delete(milestone: MilestoneEntity)

    @Query("SELECT * FROM milestones WHERE reminderEnabled = 1 AND targetEpochDay >= :todayEpochDay")
    suspend fun allActiveReminders(todayEpochDay: Long): List<MilestoneEntity>
}
