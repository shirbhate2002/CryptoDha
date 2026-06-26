package dev.vaidilya.cryptodha.data.local

import android.content.Context

class PortfolioPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("portfolio", Context.MODE_PRIVATE)

    var cashBalance: Double
        get() = prefs.getFloat("cash_balance", 10_000f).toDouble()
        set(value) {
            prefs.edit().putFloat("cash_balance", value.toFloat()).apply()
        }

    fun reset() { cashBalance = 10_000.0 }
}