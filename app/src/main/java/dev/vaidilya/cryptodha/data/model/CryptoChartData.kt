package dev.vaidilya.cryptodha.data.model

data class CryptoChartData(
    val market_caps: List<List<Double>>,
    val prices: List<List<Double>>,
    val total_volumes: List<List<Double>>
)