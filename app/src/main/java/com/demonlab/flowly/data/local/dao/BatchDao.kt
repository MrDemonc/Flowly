package com.demonlab.flowly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.demonlab.flowly.data.local.entity.BatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BatchDao {
    @Query("SELECT * FROM batches ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<BatchEntity>>

    @Query("SELECT * FROM batches WHERE id = :id")
    suspend fun getById(id: Long): BatchEntity?

    @Query("SELECT * FROM batches WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<BatchEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(batch: BatchEntity): Long

    @Update
    suspend fun update(batch: BatchEntity)

    @Delete
    suspend fun delete(batch: BatchEntity)
}
