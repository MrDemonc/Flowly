package com.demonlab.flowly.presentation.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.dao.PaymentMethodSummary
import com.demonlab.flowly.data.repository.ExpenseRepository
import com.demonlab.flowly.data.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class StatisticsState(
    val salesByPaymentMethod: List<PaymentMethodSummary> = emptyList(),
    val totalRevenue: Double = 0.0,
    val totalProfit: Double = 0.0,
    val totalSales: Int = 0,
    val avgSaleValue: Double = 0.0,
    val isLoading: Boolean = true
)

class StatisticsViewModel(
    private val saleRepository: SaleRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    init { loadData() }

    private fun loadData() {
        val cal = Calendar.getInstance()
        val monthStart = cal.apply { set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
        val now = System.currentTimeMillis()

        viewModelScope.launch {
            combine(
                saleRepository.getTotalRevenueInRangeFlow(monthStart, now).map { it ?: 0.0 },
                saleRepository.getTotalProfitInRangeFlow(monthStart, now).map { it ?: 0.0 },
                saleRepository.getSalesSummaryFlow(monthStart, now),
                saleRepository.getSalesByPaymentMethodFlow(monthStart, now)
            ) { revenue: Double, profit: Double, summary: com.demonlab.flowly.data.local.dao.SaleSummary, paymentMethods: List<PaymentMethodSummary> ->
                _state.value = StatisticsState(
                    salesByPaymentMethod = paymentMethods,
                    totalRevenue = revenue,
                    totalProfit = profit,
                    totalSales = summary.count,
                    avgSaleValue = if (summary.count > 0) summary.totalAmount / summary.count else 0.0,
                    isLoading = false
                )
            }.launchIn(viewModelScope)
        }
    }

    class Factory(
        private val saleRepository: SaleRepository,
        private val expenseRepository: ExpenseRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = StatisticsViewModel(saleRepository, expenseRepository) as T
    }
}
