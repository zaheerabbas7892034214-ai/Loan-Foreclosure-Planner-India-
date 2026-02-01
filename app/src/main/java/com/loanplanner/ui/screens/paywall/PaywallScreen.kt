package com.loanplanner.ui.screens.paywall

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loanplanner.billing.BillingManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    billingManager: BillingManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val billingState by billingManager.billingState.collectAsState()
    val isPremium by billingManager.isPremiumUser.collectAsState()
    
    LaunchedEffect(billingState) {
        when (billingState) {
            is BillingManager.BillingState.PurchaseSuccess -> {
                onNavigateBack()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unlock Premium") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isPremium) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You're a Premium User!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Text(
                    text = "Premium Features",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                FeatureItem(
                    icon = Icons.Default.Star,
                    title = "Unlimited Loans",
                    description = "Add as many loans as you need"
                )
                
                FeatureItem(
                    icon = Icons.Default.AccountBalance,
                    title = "Advanced Foreclosure Simulation",
                    description = "Run detailed prepayment scenarios"
                )
                
                FeatureItem(
                    icon = Icons.Default.Face,
                    title = "Export to CSV & PDF",
                    description = "Export your loan schedules and reports"
                )
                
                FeatureItem(
                    icon = Icons.Default.Share,
                    title = "Share Reports",
                    description = "Share your financial plans with others"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "One-time Purchase",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹299",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("Lifetime access to all premium features")
                    }
                }
                
                Button(
                    onClick = {
                        billingManager.launchPurchaseFlow(context as Activity)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = billingState !is BillingManager.BillingState.Loading
                ) {
                    if (billingState is BillingManager.BillingState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Unlock Now")
                    }
                }
                
                TextButton(
                    onClick = { billingManager.restorePurchases() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restore Purchases")
                }
                
                when (billingState) {
                    is BillingManager.BillingState.PurchaseFailed -> {
                        Text(
                            text = "Purchase failed. Please try again.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    is BillingManager.BillingState.PurchaseCancelled -> {
                        Text("Purchase cancelled")
                    }
                    is BillingManager.BillingState.RestoreSuccess -> {
                        Text(
                            text = "Purchases restored successfully!",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is BillingManager.BillingState.RestoreNoPurchases -> {
                        Text("No previous purchases found")
                    }
                    is BillingManager.BillingState.BillingError -> {
                        Text(
                            text = (billingState as BillingManager.BillingState.BillingError).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun FeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}
