package com.loanplanner

import android.app.Application
import com.loanplanner.billing.BillingManager
import com.loanplanner.data.AppDatabase
import com.loanplanner.data.repository.LoanRepository
import com.loanplanner.data.repository.PurchaseRepository

class LoanPlannerApplication : Application() {
    
    lateinit var database: AppDatabase
        private set
    
    lateinit var loanRepository: LoanRepository
        private set
    
    lateinit var purchaseRepository: PurchaseRepository
        private set
    
    lateinit var billingManager: BillingManager
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        database = AppDatabase.getDatabase(this)
        loanRepository = LoanRepository(database.loanDao())
        purchaseRepository = PurchaseRepository(database.purchaseDao())
        billingManager = BillingManager(this, purchaseRepository)
        
        billingManager.initialize()
    }
}
