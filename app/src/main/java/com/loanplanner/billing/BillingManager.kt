package com.loanplanner.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.loanplanner.data.entities.PurchaseEntity
import com.loanplanner.data.repository.PurchaseRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BillingManager(
    private val context: Context,
    private val purchaseRepository: PurchaseRepository
) {
    companion object {
        const val PRODUCT_ID_PRO = "planner_pro_unlock"
        const val PRICE_INR = "₹299"
    }
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var billingClient: BillingClient? = null
    
    private val _isPremiumUser = MutableStateFlow(false)
    val isPremiumUser: StateFlow<Boolean> = _isPremiumUser
    
    private val _billingState = MutableStateFlow<BillingState>(BillingState.Idle)
    val billingState: StateFlow<BillingState> = _billingState
    
    sealed class BillingState {
        object Idle : BillingState()
        object Loading : BillingState()
        object PurchaseSuccess : BillingState()
        data class PurchaseFailed(val message: String) : BillingState()
        object PurchaseCancelled : BillingState()
        object RestoreSuccess : BillingState()
        object RestoreNoPurchases : BillingState()
        data class BillingError(val message: String) : BillingState()
    }
    
    fun initialize() {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    handlePurchases(purchases)
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    _billingState.value = BillingState.PurchaseCancelled
                } else {
                    _billingState.value = BillingState.PurchaseFailed(
                        billingResult.debugMessage
                    )
                }
            }
            .enablePendingPurchases()
            .build()
        
        connectToBillingService()
    }
    
    private fun connectToBillingService() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryPurchases()
                } else {
                    _billingState.value = BillingState.BillingError(
                        "Billing service unavailable"
                    )
                }
            }
            
            override fun onBillingServiceDisconnected() {
                // Try to reconnect
                connectToBillingService()
            }
        })
    }
    
    private fun queryPurchases() {
        scope.launch {
            try {
                val params = QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
                
                val purchasesResult = billingClient?.queryPurchasesAsync(params)
                val purchases = purchasesResult?.purchasesList ?: emptyList()
                
                if (purchases.isEmpty()) {
                    // Check local database
                    val localPurchase = purchaseRepository.getPurchase(PRODUCT_ID_PRO)
                    _isPremiumUser.value = localPurchase != null
                } else {
                    handlePurchases(purchases)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to local database
                val localPurchase = purchaseRepository.getPurchase(PRODUCT_ID_PRO)
                _isPremiumUser.value = localPurchase != null
            }
        }
    }
    
    private fun handlePurchases(purchases: List<Purchase>) {
        scope.launch {
            for (purchase in purchases) {
                if (purchase.products.contains(PRODUCT_ID_PRO) &&
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                ) {
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    }
                    
                    // Save to local database
                    purchaseRepository.savePurchase(
                        PurchaseEntity(
                            productId = PRODUCT_ID_PRO,
                            purchaseToken = purchase.purchaseToken,
                            purchaseTime = purchase.purchaseTime,
                            isAcknowledged = purchase.isAcknowledged
                        )
                    )
                    
                    _isPremiumUser.value = true
                    _billingState.value = BillingState.PurchaseSuccess
                }
            }
        }
    }
    
    private fun acknowledgePurchase(purchase: Purchase) {
        scope.launch {
            try {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun launchPurchaseFlow(activity: Activity) {
        _billingState.value = BillingState.Loading
        
        scope.launch {
            try {
                val productList = listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID_PRO)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
                
                val params = QueryProductDetailsParams.newBuilder()
                    .setProductList(productList)
                    .build()
                
                val productDetailsResult = billingClient?.queryProductDetails(params)
                val productDetails = productDetailsResult?.productDetailsList?.firstOrNull()
                
                if (productDetails != null) {
                    val flowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                            listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .build()
                            )
                        )
                        .build()
                    
                    billingClient?.launchBillingFlow(activity, flowParams)
                } else {
                    _billingState.value = BillingState.BillingError(
                        "Product not available"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _billingState.value = BillingState.BillingError(e.message ?: "Unknown error")
            }
        }
    }
    
    fun restorePurchases() {
        _billingState.value = BillingState.Loading
        
        scope.launch {
            try {
                val params = QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
                
                val purchasesResult = billingClient?.queryPurchasesAsync(params)
                val purchases = purchasesResult?.purchasesList ?: emptyList()
                
                if (purchases.isNotEmpty()) {
                    handlePurchases(purchases)
                    _billingState.value = BillingState.RestoreSuccess
                } else {
                    _billingState.value = BillingState.RestoreNoPurchases
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _billingState.value = BillingState.BillingError(
                    e.message ?: "Failed to restore purchases"
                )
            }
        }
    }
    
    fun resetBillingState() {
        _billingState.value = BillingState.Idle
    }
    
    fun dispose() {
        billingClient?.endConnection()
        scope.cancel()
    }
}
