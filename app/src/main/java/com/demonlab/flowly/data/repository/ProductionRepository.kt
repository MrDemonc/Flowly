package com.demonlab.flowly.data.repository

import com.demonlab.flowly.data.local.dao.ProductionDao
import com.demonlab.flowly.data.local.dao.ProductionWithRecipe
import com.demonlab.flowly.data.local.entity.ProductionEntity
import kotlinx.coroutines.flow.Flow

class ProductionRepository(private val dao: ProductionDao) {

    fun getAllFlow(): Flow<List<ProductionEntity>> = dao.getAllFlow()

    suspend fun getById(id: Long): ProductionEntity? = dao.getById(id)

    suspend fun insert(production: ProductionEntity): Long = dao.insert(production)

    suspend fun update(production: ProductionEntity) = dao.update(production)

    suspend fun delete(production: ProductionEntity) = dao.delete(production)

    fun getProductionsWithRecipeFlow(): Flow<List<ProductionWithRecipe>> =
        dao.getProductionsWithRecipeFlow()

    fun getProductionsInRangeFlow(startDate: Long, endDate: Long): Flow<List<ProductionWithRecipe>> =
        dao.getProductionsInRangeFlow(startDate, endDate)

    fun getTotalCostInRangeFlow(startDate: Long, endDate: Long): Flow<Double?> =
        dao.getTotalCostInRangeFlow(startDate, endDate)

    suspend fun getTotalProducedQuantity(recipeId: Long): Int =
        dao.getTotalProducedQuantity(recipeId)

    suspend fun updateSold(id: Long, sold: Int) = dao.updateSold(id, sold)
}
