package com.loanplanner.data.repository

import com.loanplanner.data.dao.PurchaseDao
import com.loanplanner.data.entities.PurchaseEntity

class PurchaseRepository(private val purchaseDao: PurchaseDao) {
    
    suspend fun getPurchase(productId: String): PurchaseEntity? {
        return purchaseDao.getPurchase(productId)
    }
    
    suspend fun savePurchase(purchase: PurchaseEntity) {
        purchaseDao.insertPurchase(purchase)
    }
    
    suspend fun deletePurchase(productId: String) {
        purchaseDao.deletePurchase(productId)
    }
    
    suspend fun deleteAllPurchases() {
        purchaseDao.deleteAllPurchases()
    }
}
