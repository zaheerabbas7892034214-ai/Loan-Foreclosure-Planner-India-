package com.loanplanner.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey
    val productId: String,
    val purchaseToken: String,
    val purchaseTime: Long,
    val isAcknowledged: Boolean
)
