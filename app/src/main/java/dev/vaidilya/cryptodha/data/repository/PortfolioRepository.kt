package dev.vaidilya.cryptodha.data.repository

import dev.vaidilya.cryptodha.data.local.AppDatabase
import dev.vaidilya.cryptodha.data.local.PortfolioPreferences
import dev.vaidilya.cryptodha.data.local.entity.HoldingEntity
import dev.vaidilya.cryptodha.data.local.entity.TradeEntity
import dev.vaidilya.cryptodha.data.local.utility.Status
import dev.vaidilya.cryptodha.data.remote.CoingeckoService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class PortfolioRepository(
    private val localDb: AppDatabase,
    private val cryptoService: CoingeckoService,
    private val portfolioPreferences: PortfolioPreferences,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
){

    suspend fun buyAsset(coinId:String, coinName: String, investAmount: Double):Result<Unit>{
        return withContext(defaultDispatcher) {
            if (investAmount > portfolioPreferences.cashBalance) {
                return@withContext Result.failure(Exception("Insufficient balance"))
            }
            val price = cryptoService.getCoinPrice(coinId)[coinId]?.usd
                ?: return@withContext Result.failure(Exception("Could not fetch price"))
            try {
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
                val currentHolding = localDb.getHoldingDao().getHoldingByName(coinId)
                var newHolding = HoldingEntity(
                    coinId = coinId,
                    coinName = coinName,
                    avgBuyValue = price,
                    quantity = coinsReceived,
                    invested = investAmount
                )
                if (currentHolding != null) {
                    newHolding = currentHolding + newHolding;
                }
                localDb.getHoldingDao().addHoldings(newHolding)
                portfolioPreferences.cashBalance -= investAmount
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun sellAsset(coinId:String, coinName: String, noOfCoinsWithdraw: Double): Result<Unit> {
        return withContext(defaultDispatcher){
            val currentHolding=localDb.getHoldingDao().getHoldingByName(coinId)
            if (currentHolding == null || currentHolding.quantity < noOfCoinsWithdraw){
                return@withContext Result.failure(Exception("Insufficient coin"))
            }
            try{
                val price= cryptoService.getCoinPrice(coinId)[coinId]?.usd
                    ?: return@withContext Result.failure(Exception("Could not fetch price"))
                val amountReceived = noOfCoinsWithdraw * price
                localDb.getTradeDao().addTread(
                    TradeEntity(
                        transactionDate = System.currentTimeMillis(),
                        transactionAmount = amountReceived,
                        noOfCoins = noOfCoinsWithdraw,
                        coinId = coinId,
                        coinTransactionPrice = price,
                        buySell = Status.SELL,
                    )
                )
                var newHolding=HoldingEntity(
                    coinId = coinId,
                    coinName = coinName,
                    avgBuyValue = price,
                    quantity = noOfCoinsWithdraw,
                    invested = amountReceived
                )
                if(currentHolding?.quantity==noOfCoinsWithdraw){
                    localDb.getHoldingDao().removeHoldings(currentHolding)
                }else {
                    currentHolding?.let { newHolding = it - newHolding };
                }
                localDb.getHoldingDao().addHoldings(newHolding)
                portfolioPreferences.cashBalance+=amountReceived
                Result.success(Unit)
            }
            catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    suspend fun resetPortfolio(){
        //not needed now
    }

    fun getCashBalance() = portfolioPreferences.cashBalance

    suspend fun getHoldingQuantity(coinId: String): Double = withContext(defaultDispatcher) {
        localDb.getHoldingDao().getHoldingByName(coinId)?.quantity ?: 0.0
    }

    fun getAllHoldings(): Flow<List<HoldingEntity>>{
        return localDb.getHoldingDao().getAllHoldings();
    }

}