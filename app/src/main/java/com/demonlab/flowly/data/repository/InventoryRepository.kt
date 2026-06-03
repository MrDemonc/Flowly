package com.demonlab.flowly.data.repository

import com.demonlab.flowly.data.local.dao.InventoryItemDao
import com.demonlab.flowly.data.local.entity.InventoryItemEntity
import kotlinx.coroutines.flow.Flow

class InventoryRepository(private val dao: InventoryItemDao) {

    fun getAllFlow(): Flow<List<InventoryItemEntity>> = dao.getAllFlow()

    fun getByUnitFlow(unit: String): Flow<List<InventoryItemEntity>> =
        dao.getByUnitFlow(unit)

    fun getLowStockFlow(): Flow<List<InventoryItemEntity>> = dao.getLowStockFlow()

    suspend fun getById(id: Long): InventoryItemEntity? = dao.getById(id)

    suspend fun insert(item: InventoryItemEntity): Long = dao.insert(item)

    suspend fun update(item: InventoryItemEntity) = dao.update(item)

    suspend fun delete(item: InventoryItemEntity) = dao.delete(item)

    fun searchFlow(query: String): Flow<List<InventoryItemEntity>> = dao.searchFlow(query)

    fun getDistinctUnitsFlow(): Flow<List<String>> = dao.getDistinctUnitsFlow()
}
