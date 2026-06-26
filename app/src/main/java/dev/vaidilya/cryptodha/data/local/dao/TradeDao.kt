package dev.vaidilya.cryptodha.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.vaidilya.cryptodha.data.local.entity.TradeEntity


@Dao
interface TradeDao{

    @Insert
    fun addTread(trade: TradeEntity)

    @Query("SELECT * FROM TradeEntity WHERE coinId = :id")
    fun getTradeHistoryById(id: String): List<TradeEntity>

}