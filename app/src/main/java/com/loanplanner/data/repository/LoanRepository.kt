package com.loanplanner.data.repository

import com.loanplanner.data.dao.LoanDao
import com.loanplanner.data.entities.LoanEntity
import kotlinx.coroutines.flow.Flow

class LoanRepository(private val loanDao: LoanDao) {
    
    val allLoans: Flow<List<LoanEntity>> = loanDao.getAllLoans()
    
    suspend fun getLoanById(id: Long): LoanEntity? {
        return loanDao.getLoanById(id)
    }
    
    suspend fun getLoanCount(): Int {
        return loanDao.getLoanCount()
    }
    
    suspend fun insertLoan(loan: LoanEntity): Long {
        return loanDao.insertLoan(loan)
    }
    
    suspend fun updateLoan(loan: LoanEntity) {
        loanDao.updateLoan(loan)
    }
    
    suspend fun deleteLoan(loan: LoanEntity) {
        loanDao.deleteLoan(loan)
    }
    
    suspend fun deleteAllLoans() {
        loanDao.deleteAllLoans()
    }
}
