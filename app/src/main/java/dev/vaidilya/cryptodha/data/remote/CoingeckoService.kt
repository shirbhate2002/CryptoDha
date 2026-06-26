package dev.vaidilya.cryptodha.data.remote

import dev.vaidilya.cryptodha.data.model.CryptoChartData
import dev.vaidilya.cryptodha.data.model.CryptoList
import dev.vaidilya.cryptodha.data.model.SimplePriceResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface CoingeckoService {

    @GET("coins/markets?vs_currency=usd&order=market_cap_desc&per_page=20&page=1&sparkline=false")
    suspend fun getCurrentMarketList() : CryptoList


    @GET("simple/price")
    suspend fun getCoinPrice(
        @Query("ids") ids: String,
        @Query("vs_currencies") currency: String = "usd",
        @Query("include_market_cap") includeMarketCap: Boolean = true,
        @Query("include_24hr_vol") include24hVol: Boolean = true,
        @Query("include_24hr_change") include24hChange: Boolean = true
    ): SimplePriceResponse

    @GET("coins/{id}/market_chart")
    suspend fun getCryptoPriceList(
        @Path("id") cryptoId: String,
        @Query("days") days:String,
        @Query("interval") interval: String,
        @Query("vs_currency") currency: String = "usd",
    ) : CryptoChartData
}