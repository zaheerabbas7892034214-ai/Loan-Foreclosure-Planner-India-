package com.loanplanner.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lenderName: String,
    val principalAmount: Double,
    val interestRate: Double,
    val tenureMonths: Int,
    val emiAmount: Double,
    val startDate: Long, // Unix timestamp
    val createdAt: Long = System.currentTimeMillis()
)
