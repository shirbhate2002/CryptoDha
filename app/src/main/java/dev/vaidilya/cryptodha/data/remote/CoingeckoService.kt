package dev.vaidilya.cryptodha.data.remote

import dev.vaidilya.cryptodha.data.model.CryptoChartData
import dev.vaidilya.cryptodha.data.model.CryptoList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface CoingeckoService {

    @GET("coins/markets?vs_currency=usd&order=market_cap_desc&per_page=20&page=1&sparkline=false")
    suspend fun getCurrentMarketList() : CryptoList


    @GET("coins/markets?vs_currency=usd&order=market_cap_desc&per_page=20&page=1&sparkline=false")
    suspend fun getCryptoPrice() : CryptoList

    @GET("coins/{id}/market_chart?vs_currency=usd")
    suspend fun getCryptoPriceList(
        @Path("id") cryptoId: String,
        @Query("days") days:String,
        @Query("interval") interval: String
    ) : CryptoChartData
}