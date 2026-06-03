package com.demonlab.flowly.data.repository

import com.demonlab.flowly.data.local.dao.CategoryDao
import com.demonlab.flowly.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val dao: CategoryDao) {
    fun getAllFlow(): Flow<List<CategoryEntity>> = dao.getAllFlow()
    suspend fun getById(id: Long): CategoryEntity? = dao.getById(id)
    suspend fun insert(category: CategoryEntity): Long = dao.insert(category)
    suspend fun update(category: CategoryEntity) = dao.update(category)
    suspend fun delete(category: CategoryEntity) = dao.delete(category)
}
