package com.loanplanner.domain.usecases

import com.loanplanner.domain.models.EMIScheduleItem
import com.loanplanner.domain.models.PrepaymentResult
import kotlin.math.pow

object LoanCalculator {
    
    fun calculateEMI(principal: Double, annualRate: Double, tenureMonths: Int): Double {
        if (annualRate == 0.0) {
            return principal / tenureMonths
        }
        
        val monthlyRate = annualRate / 12 / 100
        val emi = principal * monthlyRate * (1 + monthlyRate).pow(tenureMonths) /
                ((1 + monthlyRate).pow(tenureMonths) - 1)
        return emi
    }
    
    fun generateEMISchedule(
        principal: Double,
        annualRate: Double,
        tenureMonths: Int,
        emi: Double
    ): List<EMIScheduleItem> {
        val schedule = mutableListOf<EMIScheduleItem>()
        var remainingBalance = principal
        val monthlyRate = annualRate / 12 / 100
        
        for (month in 1..tenureMonths) {
            val interestForMonth = remainingBalance * monthlyRate
            val principalForMonth = emi - interestForMonth
            remainingBalance -= principalForMonth
            
            // Avoid negative balance due to rounding
            if (remainingBalance < 0) remainingBalance = 0.0
            
            schedule.add(
                EMIScheduleItem(
                    month = month,
                    emiAmount = emi,
                    principalPaid = principalForMonth,
                    interestPaid = interestForMonth,
                    remainingBalance = remainingBalance
                )
            )
            
            if (remainingBalance <= 0) break
        }
        
        return schedule
    }
    
    fun calculateTotalInterest(schedule: List<EMIScheduleItem>): Double {
        return schedule.sumOf { it.interestPaid }
    }
    
    fun simulatePrepayment(
        principal: Double,
        annualRate: Double,
        tenureMonths: Int,
        emi: Double,
        lumpSumPayment: Double = 0.0,
        extraMonthlyPayment: Double = 0.0
    ): PrepaymentResult {
        val originalSchedule = generateEMISchedule(principal, annualRate, tenureMonths, emi)
        val originalInterest = calculateTotalInterest(originalSchedule)
        
        // Simulate with prepayments
        val newSchedule = mutableListOf<EMIScheduleItem>()
        var remainingBalance = principal - lumpSumPayment
        if (remainingBalance < 0) remainingBalance = 0.0
        
        val monthlyRate = annualRate / 12 / 100
        var month = 1
        
        while (remainingBalance > 0 && month <= tenureMonths) {
            val interestForMonth = remainingBalance * monthlyRate
            val principalForMonth = (emi + extraMonthlyPayment) - interestForMonth
            remainingBalance -= principalForMonth
            
            if (remainingBalance < 0) remainingBalance = 0.0
            
            newSchedule.add(
                EMIScheduleItem(
                    month = month,
                    emiAmount = emi + extraMonthlyPayment,
                    principalPaid = principalForMonth,
                    interestPaid = interestForMonth,
                    remainingBalance = remainingBalance
                )
            )
            
            month++
        }
        
        val newInterest = calculateTotalInterest(newSchedule)
        val interestSaved = originalInterest - newInterest
        val tenureReduced = tenureMonths - newSchedule.size
        
        return PrepaymentResult(
            interestSaved = interestSaved,
            tenureReduced = tenureReduced,
            newEMISchedule = newSchedule
        )
    }
}
