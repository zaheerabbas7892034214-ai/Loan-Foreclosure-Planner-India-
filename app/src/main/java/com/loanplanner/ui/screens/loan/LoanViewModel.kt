package com.loanplanner.ui.screens.loan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanplanner.data.entities.LoanEntity
import com.loanplanner.data.repository.LoanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoanViewModel(
    private val loanRepository: LoanRepository
) : ViewModel() {
    
    private val _loanState = MutableStateFlow<LoanState>(LoanState.Idle)
    val loanState: StateFlow<LoanState> = _loanState
    
    sealed class LoanState {
        object Idle : LoanState()
        object Loading : LoanState()
        object Success : LoanState()
        data class Error(val message: String) : LoanState()
    }
    
    fun saveLoan(loan: LoanEntity) {
        viewModelScope.launch {
            _loanState.value = LoanState.Loading
            try {
                loanRepository.insertLoan(loan)
                _loanState.value = LoanState.Success
            } catch (e: Exception) {
                _loanState.value = LoanState.Error(e.message ?: "Failed to save loan")
            }
        }
    }
    
    fun updateLoan(loan: LoanEntity) {
        viewModelScope.launch {
            _loanState.value = LoanState.Loading
            try {
                loanRepository.updateLoan(loan)
                _loanState.value = LoanState.Success
            } catch (e: Exception) {
                _loanState.value = LoanState.Error(e.message ?: "Failed to update loan")
            }
        }
    }
    
    suspend fun getLoanById(id: Long): LoanEntity? {
        return loanRepository.getLoanById(id)
    }
    
    fun deleteLoan(loan: LoanEntity) {
        viewModelScope.launch {
            loanRepository.deleteLoan(loan)
        }
    }
    
    fun resetState() {
        _loanState.value = LoanState.Idle
    }
}
