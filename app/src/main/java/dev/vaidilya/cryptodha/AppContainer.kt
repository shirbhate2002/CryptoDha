package dev.vaidilya.cryptodha

import android.content.Context
import android.util.Log
import androidx.room.Room
import dev.vaidilya.cryptodha.data.local.AppDatabase
import dev.vaidilya.cryptodha.data.local.PortfolioPreferences
import dev.vaidilya.cryptodha.data.remote.CoingeckoService
import dev.vaidilya.cryptodha.data.repository.PortfolioRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(val context: Context) {

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-cg-demo-api-key", "")
                .build()
            val start = System.currentTimeMillis()
            val response = chain.proceed(request)
            val duration = System.currentTimeMillis() - start
            Log.d("OkHttp", "${request.url} -> ${response.code} in ${duration}ms")
            response
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val cryptoService = retrofit.create(CoingeckoService::class.java)

    val db= Room.databaseBuilder(context, AppDatabase::class.java,"CryptoDha-db").build()

    val portfolioPreferences=PortfolioPreferences(context)

    val portfolioRepository= PortfolioRepository(
        localDb = db,
        cryptoService = cryptoService,
        portfolioPreferences = portfolioPreferences
    )
}