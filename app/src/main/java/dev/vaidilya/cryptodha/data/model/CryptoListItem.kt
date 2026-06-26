package dev.vaidilya.cryptodha.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CryptoListItem(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    @SerializedName("current_price")
    val currentPrice: Double,
    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double,
    @SerializedName("market_cap")
    val marketCap : Double,
    @SerializedName("market_cap_change_24h")
    val marketCapChange24h : Double
)