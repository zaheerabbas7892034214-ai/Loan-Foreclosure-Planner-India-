package com.loanplanner.data.dao

import androidx.room.*
import com.loanplanner.data.entities.PurchaseEntity

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases WHERE productId = :productId")
    suspend fun getPurchase(productId: String): PurchaseEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseEntity)
    
    @Query("DELETE FROM purchases WHERE productId = :productId")
    suspend fun deletePurchase(productId: String)
    
    @Query("DELETE FROM purchases")
    suspend fun deleteAllPurchases()
}
