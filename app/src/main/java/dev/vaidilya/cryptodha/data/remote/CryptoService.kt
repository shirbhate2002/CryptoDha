package dev.vaidilya.cryptodha.data.remote

import dev.vaidilya.cryptodha.data.model.CryptoDetailResponse
import dev.vaidilya.cryptodha.data.model.CryptoListResponse
import dev.vaidilya.cryptodha.data.model.CryptoPriceList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptoService {
    @Headers("Authorization: Bearer 9fd5bafa6776c9a837837082373c578aae3a12ad4fba0b3970ecc49abb2e5887")
    @GET("assets?limit=20&offset=0")
    suspend fun getCurrentMarketList(): CryptoListResponse

    @Headers("Authorization: Bearer 9fd5bafa6776c9a837837082373c578aae3a12ad4fba0b3970ecc49abb2e5887")
    @GET("assets/{slug}")
    suspend fun getCryptoDetails(@Path("slug") cryptoName: String): CryptoDetailResponse

    @Headers("Authorization: Bearer 9fd5bafa6776c9a837837082373c578aae3a12ad4fba0b3970ecc49abb2e5887")
    @GET("assets/{slug}/history/")
    suspend fun getCryptoPriceList(@Path("slug") cryptoName: String, @Query("interval") interval:String): CryptoPriceList
}
