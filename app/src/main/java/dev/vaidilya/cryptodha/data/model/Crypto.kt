package dev.vaidilya.cryptodha.data.model

data class Crypto(
    val changePercent24Hr: String,
    val id: String,
    val marketCapUsd: String,
    val name: String,
    val priceUsd: String,
    val rank: String,
    val symbol: String,
    val volumeUsd24Hr: String,
    val delta: String?
)
