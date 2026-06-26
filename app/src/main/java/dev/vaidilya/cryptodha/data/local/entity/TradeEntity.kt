package dev.vaidilya.cryptodha.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.vaidilya.cryptodha.data.local.utility.Status
import java.util.UUID


@Entity
data class TradeEntity(
    @PrimaryKey
    val uid: UUID=UUID.randomUUID(),
    val transactionDate: Long,
    val transactionAmount: Double,
    val noOfCoins: Double,
    val coinId: String,
    val coinTransactionPrice : Double,
    val buySell: Status
)
