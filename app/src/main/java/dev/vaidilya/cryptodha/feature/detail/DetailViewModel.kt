package dev.vaidilya.cryptodha.feature.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vaidilya.cryptodha.core.UiState
import dev.vaidilya.cryptodha.data.model.CryptoListItem
import dev.vaidilya.cryptodha.data.remote.CoingeckoService
import dev.vaidilya.cryptodha.data.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class TradeResult {
    data class Success(val message: String) : TradeResult()
    data class Failure(val message: String) : TradeResult()
}
class DetailViewModel(
    private val slug: CryptoListItem,
    private val cryptoService: CoingeckoService,
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _priceChartState : MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)

    val priceChartStare=_priceChartState.asStateFlow()

    private val _tradeState = MutableStateFlow<TradeResult?>(null)
    val tradeState = _tradeState.asStateFlow()

    fun clearTradeState() {
        _tradeState.update { null }
    }

    fun getPriceWithInterval(days:String){
        viewModelScope.launch {
            try {
                _priceChartState.update { UiState.Loading}
                val interval=if(days.toInt()<7) "hourly" else "daily";
                val priceList=cryptoService.getCryptoPriceList(slug.id,days,interval)
                _priceChartState.update { UiState.Success(priceList) }
                Log.d("DetailViewModel",priceList.toString())
            }
            catch (e:Exception){
                _priceChartState.update { UiState.Error("Something went wrong when fetching price list! $e $slug") }
            }
        }
    }

    private val _cashBalance = MutableStateFlow(portfolioRepository.getCashBalance())
    val cashBalance: StateFlow<Double> = _cashBalance.asStateFlow()

    private val _holdingQuantity = MutableStateFlow(0.0)
    val holdingQuantity: StateFlow<Double> = _holdingQuantity.asStateFlow()

    init {
        _uiState.update { UiState.Success(slug) }
        getPriceWithInterval("1")
        viewModelScope.launch { refreshHolding() }
    }

    private suspend fun refreshHolding() {
        _holdingQuantity.update { portfolioRepository.getHoldingQuantity(slug.id) }
    }

    fun buyAsset(investAmount: Double) {
        viewModelScope.launch {
            val result = portfolioRepository.buyAsset(slug.id, slug.name, investAmount)
            result.fold(
                onSuccess = {
                    _cashBalance.update { portfolioRepository.getCashBalance() }
                    refreshHolding()
                    _tradeState.update { TradeResult.Success("Purchase successful!") }
                },
                onFailure = { error -> _tradeState.update { TradeResult.Failure(error.message ?: "Buy failed") } }
            )
        }
    }

    fun sellAsset(noOfCoins: Double) {
        viewModelScope.launch {
            val result = portfolioRepository.sellAsset(slug.id, slug.name, noOfCoins)
            result.fold(
                onSuccess = {
                    _cashBalance.update { portfolioRepository.getCashBalance() }
                    refreshHolding()
                    _tradeState.update { TradeResult.Success("Sold successfully!") }
                },
                onFailure = { error -> _tradeState.update { TradeResult.Failure(error.message ?: "Sell failed") } }
            )
        }
    }

}
