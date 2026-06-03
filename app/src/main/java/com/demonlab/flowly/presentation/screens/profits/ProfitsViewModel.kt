package com.demonlab.flowly.presentation.screens.profits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.repository.ExpenseRepository
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
import java.util.Calendar

enum class ProfitPeriod { DAILY, WEEKLY, MONTHLY, YEARLY }

data class ProfitsState(
    val period: ProfitPeriod = ProfitPeriod.MONTHLY,
    val totalRevenue: Double = 0.0,
    val totalCost: Double = 0.0,
    val grossProfit: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val totalPurchases: Double = 0.0,
    val netProfit: Double = 0.0,
    val salesCount: Int = 0,
    val isLoading: Boolean = true
)

class ProfitsViewModel(
    private val saleRepository: SaleRepository,
    private val expenseRepository: ExpenseRepository,
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfitsState())
    val state: StateFlow<ProfitsState> = _state.asStateFlow()

    init { loadData() }

    fun setPeriod(period: ProfitPeriod) {
        _state.value = _state.value.copy(period = period, isLoading = true)
        loadData()
    }

    @OptIn(FlowPreview::class)
    private fun loadData() {
        val (start, end) = getDateRange(_state.value.period)
        viewModelScope.launch {
            combine(
                listOf<Flow<Any?>>(
                    saleRepository.getTotalRevenueInRangeFlow(start, end),
                    saleRepository.getTotalCostInRangeFlow(start, end),
                    saleRepository.getTotalProfitInRangeFlow(start, end),
                    expenseRepository.getTotalExpensesInRangeFlow(start, end),
                    purchaseRepository.getTotalSpentInRangeFlow(start, end),
                    saleRepository.getSalesSummaryFlow(start, end)
                )
            ) { values: Array<Any?> ->
                val revenue = values[0] as? Double ?: 0.0
                val cost = values[1] as? Double ?: 0.0
                val profit = values[2] as? Double ?: 0.0
                val expenses = values[3] as? Double ?: 0.0
                val purchases = values[4] as? Double ?: 0.0
                val summary = values[5] as? com.demonlab.flowly.data.local.dao.SaleSummary
                _state.value = ProfitsState(
                    period = _state.value.period,
                    totalRevenue = revenue, totalCost = cost, grossProfit = profit,
                    totalExpenses = expenses, totalPurchases = purchases,
                    netProfit = profit - expenses - purchases,
                    salesCount = summary?.count ?: 0, isLoading = false
                )
            }.launchIn(viewModelScope)
        }
    }

    private fun getDateRange(period: ProfitPeriod): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val end = cal.timeInMillis
        when (period) {
            ProfitPeriod.DAILY -> { cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0) }
            ProfitPeriod.WEEKLY -> { cal.set(Calendar.DAY_OF_WEEK, cal.getActualMinimum(Calendar.DAY_OF_WEEK)); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0) }
            ProfitPeriod.MONTHLY -> { cal.set(Calendar.DAY_OF_MONTH, 1); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0) }
            ProfitPeriod.YEARLY -> { cal.set(Calendar.DAY_OF_YEAR, 1); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0) }
        }
        return cal.timeInMillis to end
    }

    class Factory(
        private val saleRepository: SaleRepository,
        private val expenseRepository: ExpenseRepository,
        private val purchaseRepository: PurchaseRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ProfitsViewModel(saleRepository, expenseRepository, purchaseRepository) as T
    }
}
