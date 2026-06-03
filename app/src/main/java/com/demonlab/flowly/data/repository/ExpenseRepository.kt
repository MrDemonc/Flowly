package com.demonlab.flowly.data.repository

import com.demonlab.flowly.data.local.dao.ExpenseByCategory
import com.demonlab.flowly.data.local.dao.ExpenseDao
import com.demonlab.flowly.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {

    fun getAllFlow(): Flow<List<ExpenseEntity>> = dao.getAllFlow()

    suspend fun getById(id: Long): ExpenseEntity? = dao.getById(id)

    suspend fun insert(expense: ExpenseEntity): Long = dao.insert(expense)

    suspend fun update(expense: ExpenseEntity) = dao.update(expense)

    suspend fun delete(expense: ExpenseEntity) = dao.delete(expense)

    fun getExpensesInRangeFlow(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>> =
        dao.getExpensesInRangeFlow(startDate, endDate)

    fun getTotalExpensesInRangeFlow(startDate: Long, endDate: Long): Flow<Double?> =
        dao.getTotalExpensesInRangeFlow(startDate, endDate)

    fun getExpensesByCategoryFlow(startDate: Long, endDate: Long): Flow<List<ExpenseByCategory>> =
        dao.getExpensesByCategoryFlow(startDate, endDate)

    fun getCategoriesFlow(): Flow<List<String>> = dao.getCategoriesFlow()

    fun getByCategoryFlow(category: String): Flow<List<ExpenseEntity>> =
        dao.getByCategoryFlow(category)
}
