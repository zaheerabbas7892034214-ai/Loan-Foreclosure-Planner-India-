package com.loanplanner.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanplanner.billing.BillingManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val billingManager: BillingManager
) : ViewModel() {
    
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady
    
    init {
        checkEntitlement()
    }
    
    private fun checkEntitlement() {
        viewModelScope.launch {
            // Simulate entitlement check
            delay(2000)
            _isReady.value = true
        }
    }
}
