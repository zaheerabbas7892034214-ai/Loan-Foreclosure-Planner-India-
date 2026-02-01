package com.loanplanner.domain.usecases

import com.loanplanner.domain.models.Loan
import com.loanplanner.domain.models.MonthlyProgress
import com.loanplanner.domain.models.StrategyResult

object StrategyComparator {
    
    fun compareStrategies(
        loans: List<Loan>,
        extraPaymentAmount: Double
    ): Pair<StrategyResult, StrategyResult> {
        val snowballResult = simulateSnowball(loans, extraPaymentAmount)
        val avalancheResult = simulateAvalanche(loans, extraPaymentAmount)
        return Pair(snowballResult, avalancheResult)
    }
    
    private fun simulateSnowball(loans: List<Loan>, extraPayment: Double): StrategyResult {
        // Snowball: Pay off smallest balance first
        val sortedLoans = loans.sortedBy { it.principalAmount }
        return simulateStrategy("Snowball Method", sortedLoans, extraPayment)
    }
    
    private fun simulateAvalanche(loans: List<Loan>, extraPayment: Double): StrategyResult {
        // Avalanche: Pay off highest interest rate first
        val sortedLoans = loans.sortedByDescending { it.interestRate }
        return simulateStrategy("Avalanche Method", sortedLoans, extraPayment)
    }
    
    private fun simulateStrategy(
        strategyName: String,
        orderedLoans: List<Loan>,
        extraPayment: Double
    ): StrategyResult {
        val loanBalances = orderedLoans.map { it.principalAmount }.toMutableList()
        val loanEMIs = orderedLoans.map { it.emiAmount }.toMutableList()
        val monthlyRates = orderedLoans.map { it.interestRate / 12 / 100 }
        
        var totalInterestPaid = 0.0
        var month = 0
        val monthlyProgress = mutableListOf<MonthlyProgress>()
        var extraPaymentAvailable = extraPayment
        
        while (loanBalances.any { it > 0 }) {
            month++
            
            // Pay regular EMIs for all active loans
            loanBalances.forEachIndexed { index, balance ->
                if (balance > 0) {
                    val interest = balance * monthlyRates[index]
                    val principal = loanEMIs[index] - interest
                    totalInterestPaid += interest
                    loanBalances[index] = (balance - principal).coerceAtLeast(0.0)
                }
            }
            
            // Apply extra payment to the first active loan in the strategy order
            for (i in loanBalances.indices) {
                if (loanBalances[i] > 0 && extraPaymentAvailable > 0) {
                    val payment = minOf(extraPaymentAvailable, loanBalances[i])
                    loanBalances[i] -= payment
                    break
                }
            }
            
            val totalRemaining = loanBalances.sum()
            val loansRemaining = loanBalances.count { it > 0 }
            
            monthlyProgress.add(
                MonthlyProgress(
                    month = month,
                    totalRemainingBalance = totalRemaining,
                    loansRemaining = loansRemaining
                )
            )
            
            // Safety check to prevent infinite loops
            if (month > 1000) break
        }
        
        // Calculate baseline interest (without extra payments)
        val baselineInterest = orderedLoans.sumOf { loan ->
            val schedule = LoanCalculator.generateEMISchedule(
                loan.principalAmount,
                loan.interestRate,
                loan.tenureMonths,
                loan.emiAmount
            )
            LoanCalculator.calculateTotalInterest(schedule)
        }
        
        val interestSaved = baselineInterest - totalInterestPaid
        val baselineMonths = orderedLoans.maxOf { it.tenureMonths }
        val monthsSaved = baselineMonths - month
        
        return StrategyResult(
            strategyName = strategyName,
            totalInterestSaved = interestSaved.coerceAtLeast(0.0),
            totalMonthsSaved = monthsSaved.coerceAtLeast(0),
            payoffOrder = orderedLoans.map { it.lenderName },
            monthlyProgress = monthlyProgress
        )
    }
}
