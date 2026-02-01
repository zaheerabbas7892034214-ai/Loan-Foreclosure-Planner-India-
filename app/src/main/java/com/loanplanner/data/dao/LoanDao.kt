package com.loanplanner.data.dao

import androidx.room.*
import com.loanplanner.data.entities.LoanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY createdAt DESC")
    fun getAllLoans(): Flow<List<LoanEntity>>
    
    @Query("SELECT * FROM loans WHERE id = :loanId")
    suspend fun getLoanById(loanId: Long): LoanEntity?
    
    @Query("SELECT COUNT(*) FROM loans")
    suspend fun getLoanCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long
    
    @Update
    suspend fun updateLoan(loan: LoanEntity)
    
    @Delete
    suspend fun deleteLoan(loan: LoanEntity)
    
    @Query("DELETE FROM loans")
    suspend fun deleteAllLoans()
}
