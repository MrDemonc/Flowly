package com.demonlab.flowly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.demonlab.flowly.data.local.entity.BatchProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BatchProductDao {
    @Query("SELECT * FROM batch_products WHERE batchId = :batchId")
    fun getProductsByBatchIdFlow(batchId: Long): Flow<List<BatchProductEntity>>

    @Query("SELECT * FROM batch_products WHERE batchId = :batchId")
    suspend fun getProductsByBatchId(batchId: Long): List<BatchProductEntity>

    @Query("SELECT * FROM batch_products WHERE id = :id")
    suspend fun getById(id: Long): BatchProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<BatchProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: BatchProductEntity): Long

    @Update
    suspend fun update(product: BatchProductEntity)

    @Delete
    suspend fun delete(product: BatchProductEntity)
}
