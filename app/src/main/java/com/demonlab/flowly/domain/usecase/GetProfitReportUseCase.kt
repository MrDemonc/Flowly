package com.demonlab.flowly.domain.usecase

import com.demonlab.flowly.data.repository.ExpenseRepository
import com.demonlab.flowly.data.repository.PurchaseRepository
import com.demonlab.flowly.data.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine

data class ProfitReport(
    val totalRevenue: Double,
    val totalCostOfSales: Double,
    val grossProfit: Double,
    val totalExpenses: Double,
    val totalPurchases: Double,
    val netProfit: Double,
    val salesCount: Int
)

class GetProfitReportUseCase(
    private val saleRepository: SaleRepository,
    private val expenseRepository: ExpenseRepository,
    private val purchaseRepository: PurchaseRepository
) {
    @OptIn(FlowPreview::class)
    fun execute(startDate: Long, endDate: Long): Flow<ProfitReport> {
        return combine(
            listOf<Flow<Any?>>(
                saleRepository.getTotalRevenueInRangeFlow(startDate, endDate),
                saleRepository.getTotalCostInRangeFlow(startDate, endDate),
                saleRepository.getTotalProfitInRangeFlow(startDate, endDate),
                expenseRepository.getTotalExpensesInRangeFlow(startDate, endDate),
                purchaseRepository.getTotalSpentInRangeFlow(startDate, endDate),
                saleRepository.getSalesSummaryFlow(startDate, endDate)
            )
        ) { values: Array<Any?> ->
            val revenue = values[0] as? Double ?: 0.0
            val costOfSales = values[1] as? Double ?: 0.0
            val grossProfit = values[2] as? Double ?: 0.0
            val expenses = values[3] as? Double ?: 0.0
            val purchases = values[4] as? Double ?: 0.0
            val summary = values[5] as? com.demonlab.flowly.data.local.dao.SaleSummary

            ProfitReport(
                totalRevenue = revenue,
                totalCostOfSales = costOfSales,
                grossProfit = grossProfit,
                totalExpenses = expenses,
                totalPurchases = purchases,
                netProfit = grossProfit - expenses,
                salesCount = summary?.count ?: 0
            )
        }
    }
}
