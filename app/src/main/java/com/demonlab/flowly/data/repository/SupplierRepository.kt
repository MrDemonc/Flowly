package com.demonlab.flowly.data.repository

import com.demonlab.flowly.data.local.dao.SupplierDao
import com.demonlab.flowly.data.local.entity.SupplierEntity
import kotlinx.coroutines.flow.Flow

class SupplierRepository(private val dao: SupplierDao) {
    fun getAllFlow(): Flow<List<SupplierEntity>> = dao.getAllFlow()
    suspend fun getById(id: Long): SupplierEntity? = dao.getById(id)
    suspend fun insert(supplier: SupplierEntity): Long = dao.insert(supplier)
    suspend fun update(supplier: SupplierEntity) = dao.update(supplier)
    suspend fun delete(supplier: SupplierEntity) = dao.delete(supplier)
    fun searchFlow(query: String): Flow<List<SupplierEntity>> = dao.searchFlow(query)
}
