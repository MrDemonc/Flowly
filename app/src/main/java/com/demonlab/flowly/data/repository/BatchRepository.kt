package com.demonlab.flowly.data.repository

import com.demonlab.flowly.data.local.dao.BatchDao
import com.demonlab.flowly.data.local.dao.BatchProductDao
import com.demonlab.flowly.data.local.dao.BatchSaleDao
import com.demonlab.flowly.data.local.entity.BatchEntity
import com.demonlab.flowly.data.local.entity.BatchProductEntity
import com.demonlab.flowly.data.local.entity.BatchSaleEntity
import kotlinx.coroutines.flow.Flow

class BatchRepository(
    private val batchDao: BatchDao,
    private val batchProductDao: BatchProductDao,
    private val batchSaleDao: BatchSaleDao
) {
    val batchesFlow: Flow<List<BatchEntity>> = batchDao.getAllFlow()
    val totalPaidFlow: Flow<Double?> = batchSaleDao.getTotalPaidFlow()
    val totalPendingFlow: Flow<Double?> = batchSaleDao.getTotalPendingFlow()
    val allSalesFlow: Flow<List<BatchSaleEntity>> = batchSaleDao.getAllSalesFlow()

    fun getBatchByIdFlow(batchId: Long): Flow<BatchEntity?> = batchDao.getByIdFlow(batchId)
    fun getProductsByBatchIdFlow(batchId: Long): Flow<List<BatchProductEntity>> = batchProductDao.getProductsByBatchIdFlow(batchId)
    fun getSalesByBatchIdFlow(batchId: Long): Flow<List<BatchSaleEntity>> = batchSaleDao.getSalesByBatchIdFlow(batchId)
    fun getBatchPaidFlow(batchId: Long): Flow<Double?> = batchSaleDao.getBatchPaidFlow(batchId)
    fun getBatchPendingFlow(batchId: Long): Flow<Double?> = batchSaleDao.getBatchPendingFlow(batchId)

    suspend fun createBatch(
        date: String,
        local1Name: String,
        local2Name: String,
        items: List<BatchProductInput>
    ): Long {
        val batchId = batchDao.insert(
            BatchEntity(
                date = date,
                local1Name = local1Name,
                local2Name = local2Name
            )
        )
        val entities = items.map { item ->
            BatchProductEntity(
                batchId = batchId,
                productId = item.productId,
                productName = item.productName,
                productPrice = item.productPrice,
                local1InitialQty = item.local1Qty,
                local1CurrentQty = item.local1Qty,
                local2InitialQty = item.local2Qty,
                local2CurrentQty = item.local2Qty
            )
        }
        batchProductDao.insertAll(entities)
        return batchId
    }

    suspend fun transferProducts(
        batchProductId: Long,
        fromLocal: String, // "LOCAL_1" or "LOCAL_2"
        quantity: Int
    ): Boolean {
        val product = batchProductDao.getById(batchProductId) ?: return false
        if (fromLocal == "LOCAL_1") {
            if (product.local1CurrentQty < quantity) return false
            val updated = product.copy(
                local1CurrentQty = product.local1CurrentQty - quantity,
                local2CurrentQty = product.local2CurrentQty + quantity
            )
            batchProductDao.update(updated)
        } else {
            if (product.local2CurrentQty < quantity) return false
            val updated = product.copy(
                local2CurrentQty = product.local2CurrentQty - quantity,
                local1CurrentQty = product.local1CurrentQty + quantity
            )
            batchProductDao.update(updated)
        }
        return true
    }

    suspend fun registerSale(
        batchId: Long,
        batchProductId: Long,
        local: String, // "LOCAL_1" or "LOCAL_2"
        quantity: Int,
        amountPaid: Double,
        amountPending: Double
    ): Boolean {
        val product = batchProductDao.getById(batchProductId) ?: return false
        val availableQty = if (local == "LOCAL_1") product.local1CurrentQty else product.local2CurrentQty
        if (availableQty < quantity) return false

        val updatedProduct = if (local == "LOCAL_1") {
            product.copy(local1CurrentQty = product.local1CurrentQty - quantity)
        } else {
            product.copy(local2CurrentQty = product.local2CurrentQty - quantity)
        }
        batchProductDao.update(updatedProduct)

        batchSaleDao.insert(
            BatchSaleEntity(
                batchId = batchId,
                batchProductId = batchProductId,
                local = local,
                quantity = quantity,
                amountPaid = amountPaid,
                amountPending = amountPending
            )
        )
        return true
    }

    suspend fun deleteBatch(batch: BatchEntity) {
        batchDao.delete(batch)
    }
}

data class BatchProductInput(
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val local1Qty: Int,
    val local2Qty: Int
)
