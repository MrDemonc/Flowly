package com.demonlab.flowly.data.repository

import com.demonlab.flowly.data.local.dao.PaymentMethodSummary
import com.demonlab.flowly.data.local.dao.SaleDao
import com.demonlab.flowly.data.local.dao.SaleSummary
import com.demonlab.flowly.data.local.dao.SaleWithRecipe
import com.demonlab.flowly.data.local.entity.SaleEntity
import kotlinx.coroutines.flow.Flow

class SaleRepository(private val dao: SaleDao) {

    fun getAllFlow(): Flow<List<SaleEntity>> = dao.getAllFlow()

    suspend fun getById(id: Long): SaleEntity? = dao.getById(id)

    suspend fun insert(sale: SaleEntity): Long = dao.insert(sale)

    suspend fun update(sale: SaleEntity) = dao.update(sale)

    suspend fun delete(sale: SaleEntity) = dao.delete(sale)

    fun getSalesWithRecipeFlow(): Flow<List<SaleWithRecipe>> = dao.getSalesWithRecipeFlow()

    fun getSalesInRangeFlow(startDate: Long, endDate: Long): Flow<List<SaleWithRecipe>> =
        dao.getSalesInRangeFlow(startDate, endDate)

    fun getTotalRevenueInRangeFlow(startDate: Long, endDate: Long): Flow<Double?> =
        dao.getTotalRevenueInRangeFlow(startDate, endDate)

    fun getTotalCostInRangeFlow(startDate: Long, endDate: Long): Flow<Double?> =
        dao.getTotalCostInRangeFlow(startDate, endDate)

    fun getTotalProfitInRangeFlow(startDate: Long, endDate: Long): Flow<Double?> =
        dao.getTotalProfitInRangeFlow(startDate, endDate)

    fun getSalesSummaryFlow(startDate: Long, endDate: Long): Flow<SaleSummary> =
        dao.getSalesSummaryFlow(startDate, endDate)

    fun getSalesByPaymentMethodFlow(startDate: Long, endDate: Long): Flow<List<PaymentMethodSummary>> =
        dao.getSalesByPaymentMethodFlow(startDate, endDate)
}
