package com.demonlab.flowly.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.dao.ExpenseByCategory
import com.demonlab.flowly.data.repository.ExpenseRepository
import com.demonlab.flowly.data.repository.ProductionRepository
import com.demonlab.flowly.data.repository.PurchaseRepository
import com.demonlab.flowly.data.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

data class ReportsState(
    val totalRevenue: Double = 0.0,
    val totalCost: Double = 0.0,
    val totalProfit: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val totalPurchases: Double = 0.0,
    val netProfit: Double = 0.0,
    val salesCount: Int = 0,
    val expensesByCategory: List<ExpenseByCategory> = emptyList(),
    val startDate: Long = getMonthStart(System.currentTimeMillis()),
    val endDate: Long = System.currentTimeMillis(),
    val isLoading: Boolean = true
) {
    companion object {
        fun getMonthStart(time: Long): Long {
            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = time
            cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }
    }
}

class ReportsViewModel(
    private val saleRepository: SaleRepository,
    private val expenseRepository: ExpenseRepository,
    private val purchaseRepository: PurchaseRepository,
    private val productionRepository: ProductionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsState())
    val state: StateFlow<ReportsState> = _state.asStateFlow()

    init { loadData() }

    @OptIn(FlowPreview::class)
    private fun loadData() {
        viewModelScope.launch {
            val s = _state.value
            combine(
                listOf<Flow<Any?>>(
                    saleRepository.getTotalRevenueInRangeFlow(s.startDate, s.endDate),
                    saleRepository.getTotalCostInRangeFlow(s.startDate, s.endDate),
                    saleRepository.getTotalProfitInRangeFlow(s.startDate, s.endDate),
                    expenseRepository.getTotalExpensesInRangeFlow(s.startDate, s.endDate),
                    purchaseRepository.getTotalSpentInRangeFlow(s.startDate, s.endDate),
                    saleRepository.getSalesSummaryFlow(s.startDate, s.endDate),
                    expenseRepository.getExpensesByCategoryFlow(s.startDate, s.endDate)
                )
            ) { values: Array<Any?> ->
                val revenue = values[0] as? Double ?: 0.0
                val cost = values[1] as? Double ?: 0.0
                val profit = values[2] as? Double ?: 0.0
                val expenses = values[3] as? Double ?: 0.0
                val purchases = values[4] as? Double ?: 0.0
                val summary = values[5] as? com.demonlab.flowly.data.local.dao.SaleSummary
                val byCategory = values[6] as? List<ExpenseByCategory> ?: emptyList()

                _state.value = ReportsState(
                    totalRevenue = revenue,
                    totalCost = cost,
                    totalProfit = profit,
                    totalExpenses = expenses,
                    totalPurchases = purchases,
                    netProfit = profit - expenses - purchases,
                    salesCount = summary?.count ?: 0,
                    expensesByCategory = byCategory,
                    startDate = s.startDate,
                    endDate = s.endDate,
                    isLoading = false
                )
            }.launchIn(viewModelScope)
        }
    }

    class Factory(
        private val saleRepository: SaleRepository,
        private val expenseRepository: ExpenseRepository,
        private val purchaseRepository: PurchaseRepository,
        private val productionRepository: ProductionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ReportsViewModel(saleRepository, expenseRepository, purchaseRepository, productionRepository) as T
    }
}
