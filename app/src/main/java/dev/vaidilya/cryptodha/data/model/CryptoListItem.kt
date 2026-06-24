package dev.vaidilya.cryptodha.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CryptoListItem(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val price_change_percentage_24h: Double,
    val market_cap : Double,
    val market_cap_change_24h : Double
)