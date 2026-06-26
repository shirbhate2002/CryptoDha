package dev.vaidilya.cryptodha.data.repository

import dev.vaidilya.cryptodha.data.local.AppDatabase
import dev.vaidilya.cryptodha.data.local.PortfolioPreferences
import dev.vaidilya.cryptodha.data.local.entity.HoldingEntity
import dev.vaidilya.cryptodha.data.local.entity.TradeEntity
import dev.vaidilya.cryptodha.data.local.utility.Status
import dev.vaidilya.cryptodha.data.remote.CoingeckoService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class PortfolioRepository(
    private val localDb: AppDatabase,
    private val cryptoService: CoingeckoService,
    private val portfolioPreferences: PortfolioPreferences,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
){

    suspend fun buyAsset(coinId:String, coinName: String, investAmount: Double)= withContext(defaultDispatcher){
        if(investAmount>portfolioPreferences.cashBalance){
            return@withContext
        }
        val currentCoinPrice= cryptoService.getCoinPrice(coinId)[coinId]
        val price = currentCoinPrice?.usd
        if(price!=null){//Transaction is successful.
            val coinsReceived = investAmount / price
            localDb.getTradeDao().addTread(
                TradeEntity(
                    transactionDate = System.currentTimeMillis(),
                    transactionAmount = investAmount,
                    noOfCoins = coinsReceived,
                    coinId = coinId,
                    coinTransactionPrice = price,
                    buySell = Status.BUY,
                )
            )
            val currentHolding=localDb.getHoldingDao().getHoldingByName(coinId)
            var newHolding=HoldingEntity(
                coinId = coinId,
                coinName = coinName,
                avgBuyValue = price,
                quantity = coinsReceived,
            )
            if(currentHolding!=null){
                newHolding=currentHolding+newHolding;
            }
            localDb.getHoldingDao().addHoldings(newHolding)
            portfolioPreferences.cashBalance-=investAmount;
        }else{
            //Transection was un-successful
        }
    }

}