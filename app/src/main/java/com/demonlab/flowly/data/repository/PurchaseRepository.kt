package com.demonlab.flowly.data.repository

import com.demonlab.flowly.data.local.dao.PurchaseDao
import com.demonlab.flowly.data.local.dao.PurchaseWithIngredient
import com.demonlab.flowly.data.local.entity.PurchaseEntity
import kotlinx.coroutines.flow.Flow

class PurchaseRepository(private val dao: PurchaseDao) {

    fun getAllFlow(): Flow<List<PurchaseEntity>> = dao.getAllFlow()

    suspend fun getById(id: Long): PurchaseEntity? = dao.getById(id)

    suspend fun insert(purchase: PurchaseEntity): Long = dao.insert(purchase)

    suspend fun update(purchase: PurchaseEntity) = dao.update(purchase)

    suspend fun delete(purchase: PurchaseEntity) = dao.delete(purchase)

    fun getPurchasesWithIngredientFlow(): Flow<List<PurchaseWithIngredient>> =
        dao.getPurchasesWithIngredientFlow()

    fun getPurchasesInRangeFlow(startDate: Long, endDate: Long): Flow<List<PurchaseWithIngredient>> =
        dao.getPurchasesInRangeFlow(startDate, endDate)

    fun getTotalSpentInRangeFlow(startDate: Long, endDate: Long): Flow<Double?> =
        dao.getTotalSpentInRangeFlow(startDate, endDate)
}
