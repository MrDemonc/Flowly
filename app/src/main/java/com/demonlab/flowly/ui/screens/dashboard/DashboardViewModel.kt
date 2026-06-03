package com.demonlab.flowly.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

data class DashboardState(
    val todayRevenue: Double = 0.0,
    val todayProfit: Double = 0.0,
    val todaySalesCount: Int = 0,
    val todayExpenses: Double = 0.0,
    val monthRevenue: Double = 0.0,
    val monthProfit: Double = 0.0,
    val monthExpenses: Double = 0.0,
    val monthPurchases: Double = 0.0,
    val lowStockCount: Int = 0,
    val isLoading: Boolean = true
)

class DashboardViewModel(
    private val saleRepository: SaleRepository,
    private val expenseRepository: ExpenseRepository,
    private val purchaseRepository: PurchaseRepository,
    private val productionRepository: ProductionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadData()
    }

    @OptIn(FlowPreview::class)
    private fun loadData() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val dayStart = getDayStart(now)
            val monthStart = getMonthStart(now)

            combine(
                listOf<Flow<Any?>>(
                    saleRepository.getTotalRevenueInRangeFlow(dayStart, now),
                    saleRepository.getTotalProfitInRangeFlow(dayStart, now),
                    saleRepository.getSalesSummaryFlow(dayStart, now),
                    expenseRepository.getTotalExpensesInRangeFlow(dayStart, now),
                    saleRepository.getTotalRevenueInRangeFlow(monthStart, now),
                    saleRepository.getTotalProfitInRangeFlow(monthStart, now),
                    expenseRepository.getTotalExpensesInRangeFlow(monthStart, now),
                    purchaseRepository.getTotalSpentInRangeFlow(monthStart, now)
                )
            ) { values: Array<Any?> ->
                _state.value = DashboardState(
                    todayRevenue = values[0] as? Double ?: 0.0,
                    todayProfit = values[1] as? Double ?: 0.0,
                    todaySalesCount = (values[2] as? com.demonlab.flowly.data.local.dao.SaleSummary)?.count ?: 0,
                    todayExpenses = values[3] as? Double ?: 0.0,
                    monthRevenue = values[4] as? Double ?: 0.0,
                    monthProfit = values[5] as? Double ?: 0.0,
                    monthExpenses = values[6] as? Double ?: 0.0,
                    monthPurchases = values[7] as? Double ?: 0.0,
                    lowStockCount = 0,
                    isLoading = false
                )
            }.launchIn(viewModelScope)
        }
    }

    private fun getDayStart(time: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = time
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getMonthStart(time: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = time
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    class Factory(
        private val saleRepository: SaleRepository,
        private val expenseRepository: ExpenseRepository,
        private val purchaseRepository: PurchaseRepository,
        private val productionRepository: ProductionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(
                saleRepository, expenseRepository, purchaseRepository, productionRepository
            ) as T
        }
    }
}
