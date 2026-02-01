package com.loanplanner.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanplanner.billing.BillingManager
import com.loanplanner.data.entities.LoanEntity
import com.loanplanner.data.repository.LoanRepository
import com.loanplanner.domain.models.LoanSummary
import com.loanplanner.domain.usecases.LoanCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loanRepository: LoanRepository,
    private val billingManager: BillingManager
) : ViewModel() {
    
    val loans: StateFlow<List<LoanEntity>> = loanRepository.allLoans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val isPremiumUser: StateFlow<Boolean> = billingManager.isPremiumUser
    
    val loanSummary: StateFlow<LoanSummary> = loans.map { loanList ->
        val totalPrincipal = loanList.sumOf { it.principalAmount }
        val totalInterest = loanList.sumOf { loan ->
            val schedule = LoanCalculator.generateEMISchedule(
                loan.principalAmount,
                loan.interestRate,
                loan.tenureMonths,
                loan.emiAmount
            )
            LoanCalculator.calculateTotalInterest(schedule)
        }
        
        LoanSummary(
            totalLoans = loanList.size,
            totalPrincipal = totalPrincipal,
            totalInterestPaid = totalInterest
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoanSummary(0, 0.0, 0.0)
    )
    
    suspend fun canAddLoan(): Boolean {
        val count = loanRepository.getLoanCount()
        val isPremium = isPremiumUser.value
        return isPremium || count < 2
    }
    
    fun deleteLoan(loan: LoanEntity) {
        viewModelScope.launch {
            loanRepository.deleteLoan(loan)
        }
    }
}
