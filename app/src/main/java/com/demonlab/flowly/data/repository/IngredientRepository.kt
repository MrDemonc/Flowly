package com.demonlab.flowly.data.repository

import com.demonlab.flowly.data.local.dao.IngredientDao
import com.demonlab.flowly.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

class IngredientRepository(private val dao: IngredientDao) {

    fun getAllFlow(): Flow<List<IngredientEntity>> = dao.getAllFlow()

    suspend fun getById(id: Long): IngredientEntity? = dao.getById(id)

    suspend fun insert(ingredient: IngredientEntity): Long = dao.insert(ingredient)

    suspend fun update(ingredient: IngredientEntity) = dao.update(ingredient)

    suspend fun delete(ingredient: IngredientEntity) = dao.delete(ingredient)

    fun searchFlow(query: String): Flow<List<IngredientEntity>> = dao.searchFlow(query)

    fun getCategoriesFlow(): Flow<List<String>> = dao.getCategoriesFlow()
}
