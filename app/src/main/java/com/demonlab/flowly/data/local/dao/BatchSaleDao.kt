package com.demonlab.flowly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demonlab.flowly.data.local.entity.BatchSaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BatchSaleDao {
    @Query("SELECT * FROM batch_sales WHERE batchId = :batchId ORDER BY timestamp DESC")
    fun getSalesByBatchIdFlow(batchId: Long): Flow<List<BatchSaleEntity>>

    @Query("SELECT * FROM batch_sales ORDER BY timestamp DESC")
    fun getAllSalesFlow(): Flow<List<BatchSaleEntity>>

    @Query("SELECT SUM(amountPaid) FROM batch_sales")
    fun getTotalPaidFlow(): Flow<Double?>

    @Query("SELECT SUM(amountPending) FROM batch_sales")
    fun getTotalPendingFlow(): Flow<Double?>

    @Query("SELECT SUM(amountPaid) FROM batch_sales WHERE batchId = :batchId")
    fun getBatchPaidFlow(batchId: Long): Flow<Double?>

    @Query("SELECT SUM(amountPending) FROM batch_sales WHERE batchId = :batchId")
    fun getBatchPendingFlow(batchId: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sale: BatchSaleEntity): Long

    @Delete
    suspend fun delete(sale: BatchSaleEntity)
}
