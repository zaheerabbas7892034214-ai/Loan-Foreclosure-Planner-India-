package com.loanplanner.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loanplanner.data.entities.LoanEntity
import com.loanplanner.utils.ExportUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddLoan: () -> Unit,
    onNavigateToLoanDetail: (Long) -> Unit,
    onNavigateToPaywall: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStrategy: () -> Unit
) {
    val loans by viewModel.loans.collectAsState()
    val loanSummary by viewModel.loanSummary.collectAsState()
    val isPremium by viewModel.isPremiumUser.collectAsState()
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Loans") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        if (viewModel.canAddLoan()) {
                            onNavigateToAddLoan()
                        } else {
                            onNavigateToPaywall()
                        }
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Loan")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Summary Cards
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Loan Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryItem("Total Loans", loanSummary.totalLoans.toString())
                        SummaryItem("Total Principal", ExportUtils.formatCurrency(loanSummary.totalPrincipal))
                    }
                    SummaryItem("Total Interest", ExportUtils.formatCurrency(loanSummary.totalInterestPaid))
                    
                    if (!isPremium) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToPaywall() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upgrade to Pro - Unlimited Loans",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            // Strategy Comparison Button
            if (loans.size >= 2) {
                Button(
                    onClick = onNavigateToStrategy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Compare Strategies")
                }
            }
            
            // Loans List
            if (loans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No loans added yet.\nTap the + button to add your first loan.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(loans) { loan ->
                        LoanCard(
                            loan = loan,
                            onClick = { onNavigateToLoanDetail(loan.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LoanCard(loan: LoanEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = loan.lenderName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Principal",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = ExportUtils.formatCurrency(loan.principalAmount),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "Interest Rate",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${loan.interestRate}%",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "EMI",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = ExportUtils.formatCurrency(loan.emiAmount),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
