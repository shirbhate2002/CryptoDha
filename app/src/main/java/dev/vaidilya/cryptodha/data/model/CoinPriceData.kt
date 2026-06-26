package dev.vaidilya.cryptodha.data.model

import com.google.gson.annotations.SerializedName

data class CoinPriceData(
    @SerializedName("usd")
    val usd: Double,

    @SerializedName("usd_market_cap")
    val usdMarketCap: Double,

    @SerializedName("usd_24h_vol")
    val usd24hVol: Double,

    @SerializedName("usd_24h_change")
    val usd24hChange: Double
)

typealias SimplePriceResponse = Map<String, CoinPriceData>
