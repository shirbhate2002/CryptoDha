package dev.vaidilya.cryptodha.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class HoldingEntity(
    @PrimaryKey val coinId: String,
    val coinName: String,
    val avgBuyValue: Double,
    val quantity: Double,
    val invested: Double
){

    operator fun plus(other: HoldingEntity): HoldingEntity {
        val newQuantity=this.quantity+other.quantity;
        val newAvgBuyValue=(this.avgBuyValue*this.quantity+other.avgBuyValue*other.quantity)/(newQuantity)
        return HoldingEntity(this.coinId , other.coinName,newAvgBuyValue,newQuantity,this.invested+other.invested)
    }

    operator fun minus(other: HoldingEntity): HoldingEntity {
        val newQuantity=this.quantity-other.quantity
        return HoldingEntity(this.coinId , other.coinName,this.avgBuyValue,newQuantity,this.invested-other.invested)
    }
}
