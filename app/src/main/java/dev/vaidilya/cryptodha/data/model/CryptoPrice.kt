package dev.vaidilya.cryptodha.data.model

data class CryptoPrice(
    val circulatingSupply: Int,
    val date: String,
    val priceUsd: String,
    val time: Long
)