package dev.vaidilya.cryptodha.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.vaidilya.cryptodha.data.local.dao.HoldingDao
import dev.vaidilya.cryptodha.data.local.dao.TradeDao
import dev.vaidilya.cryptodha.data.local.entity.HoldingEntity
import dev.vaidilya.cryptodha.data.local.entity.TradeEntity

@Database(entities = [TradeEntity::class, HoldingEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun getTradeDao(): TradeDao
    abstract fun getHoldingDao(): HoldingDao
}