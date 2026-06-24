package dev.vaidilya.cryptodha.feature.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vaidilya.cryptodha.core.UiState
import dev.vaidilya.cryptodha.data.model.CryptoListItem
import dev.vaidilya.cryptodha.data.remote.CoingeckoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    val slug: CryptoListItem,
    private val cryptoService: CoingeckoService
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _priceChartState : MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)

    val priceChartStare=_priceChartState.asStateFlow()

    init {
        _uiState.update { UiState.Success(slug) }
        getPriceWithInterval("1")
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

}
