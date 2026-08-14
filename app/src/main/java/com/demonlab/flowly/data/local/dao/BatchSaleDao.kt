package com.demonlab.flowly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.demonlab.flowly.data.local.entity.BatchSaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BatchSaleDao {
    @Query("SELECT * FROM batch_sales WHERE batchId = :batchId ORDER BY timestamp DESC")
    fun getSalesByBatchIdFlow(batchId: Long): Flow<List<BatchSaleEntity>>

    @Query("SELECT * FROM batch_sales WHERE batchId = :batchId AND isFiado = 1 ORDER BY isPaid ASC, timestamp DESC")
    fun getFiadosByBatchIdFlow(batchId: Long): Flow<List<BatchSaleEntity>>

    @Query("SELECT * FROM batch_sales ORDER BY timestamp DESC")
    fun getAllSalesFlow(): Flow<List<BatchSaleEntity>>

    @Query("SELECT * FROM batch_sales WHERE isFiado = 1 AND isPaid = 0 ORDER BY timestamp DESC")
    fun getAllPendingFiadosFlow(): Flow<List<BatchSaleEntity>>

    @Query("SELECT SUM(amountPaid) FROM batch_sales")
    fun getTotalPaidFlow(): Flow<Double?>

    @Query("SELECT SUM(amountPending) FROM batch_sales")
    fun getTotalPendingFlow(): Flow<Double?>

    @Query("SELECT SUM(amountPaid) FROM batch_sales WHERE batchId = :batchId")
    fun getBatchPaidFlow(batchId: Long): Flow<Double?>

    @Query("SELECT SUM(amountPending) FROM batch_sales WHERE batchId = :batchId")
    fun getBatchPendingFlow(batchId: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM batch_sales WHERE isFiado = 1 AND isPaid = 0")
    fun getPendingFiadosCountFlow(): Flow<Int>

    @Query("SELECT * FROM batch_sales WHERE id = :id")
    suspend fun getById(id: Long): BatchSaleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sale: BatchSaleEntity): Long

    @Update
    suspend fun update(sale: BatchSaleEntity)

    @Delete
    suspend fun delete(sale: BatchSaleEntity)
}
