package com.demonlab.flowly.core.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun format(amount: Double, symbol: String): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 0
        return "$symbol ${formatter.format(amount)}"
    }
}
