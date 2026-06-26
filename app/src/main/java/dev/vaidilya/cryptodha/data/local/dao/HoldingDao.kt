package dev.vaidilya.cryptodha.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.vaidilya.cryptodha.data.local.entity.HoldingEntity

@Dao
interface HoldingDao{

    @Query("SELECT * FROM HoldingEntity")
    fun getAllHoldings() : List<HoldingEntity>

    @Query("SELECT * FROM HoldingEntity WHERE coinId= :coinId")
    fun getHoldingByName(coinId: String):HoldingEntity?

    //To-DO when if the coin already exist then
    //We have to make the quantity plus and avg the value.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addHoldings(holding: HoldingEntity)

    @Delete
    fun removeHoldings(holding: HoldingEntity)

}