package com.loanplanner.domain.models

data class Loan(
    val id: Long = 0,
    val lenderName: String,
    val principalAmount: Double,
    val interestRate: Double,
    val tenureMonths: Int,
    val emiAmount: Double,
    val startDate: Long
)

data class EMIScheduleItem(
    val month: Int,
    val emiAmount: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)

data class PrepaymentResult(
    val interestSaved: Double,
    val tenureReduced: Int,
    val newEMISchedule: List<EMIScheduleItem>
)

data class StrategyResult(
    val strategyName: String,
    val totalInterestSaved: Double,
    val totalMonthsSaved: Int,
    val payoffOrder: List<String>,
    val monthlyProgress: List<MonthlyProgress>
)

data class MonthlyProgress(
    val month: Int,
    val totalRemainingBalance: Double,
    val loansRemaining: Int
)

data class LoanSummary(
    val totalLoans: Int,
    val totalPrincipal: Double,
    val totalInterestPaid: Double
)
