package dev.vaidilya.cryptodha.feature.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vaidilya.cryptodha.core.UiState
import dev.vaidilya.cryptodha.core.formatCryptoPrice
import dev.vaidilya.cryptodha.core.formateToCompactNumber
import dev.vaidilya.cryptodha.data.remote.CryptoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    val slug: String,
    private val cryptoService: CryptoService
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _priceChartState : MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)

    val priceChartStare=_priceChartState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val data = cryptoService.getCryptoDetails(slug)
                val result = data.copy(data = data.data.copy(
                    priceUsd = formatCryptoPrice(data.data.priceUsd),
                    marketCapUsd = formateToCompactNumber(data.data.marketCapUsd),
                    volumeUsd24Hr = formateToCompactNumber(data.data.volumeUsd24Hr)
                ))
                Log.d("DetailViewModel", "Received: $result")
                _uiState.update { UiState.Success(result) }
            } catch (e: Exception) {
                _uiState.update { UiState.Error("Something went wrong! $e $slug") }
            }
        }
        getPriceWithInterval()
    }

    fun getPriceWithInterval(interval:String="h1"){
        viewModelScope.launch {
            try {
                _priceChartState.update { UiState.Loading}
                val priceList=cryptoService.getCryptoPriceList(slug,interval)
                _priceChartState.update { UiState.Success(priceList) }
                Log.d("DetailViewModel",priceList.toString())
            }
            catch (e:Exception){
                _uiState.update { UiState.Error("Something went wrong when fetching price list! $e $slug") }
            }
        }
    }


}
