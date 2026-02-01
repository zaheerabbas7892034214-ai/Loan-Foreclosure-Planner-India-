package com.loanplanner.ui.screens.loan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loanplanner.data.entities.LoanEntity
import com.loanplanner.domain.usecases.LoanCalculator
import com.loanplanner.utils.ExportUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailScreen(
    loanId: Long,
    viewModel: LoanViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPrepayment: (Long) -> Unit,
    onNavigateToExport: (Long) -> Unit,
    isPremium: Boolean
) {
    var loan by remember { mutableStateOf<LoanEntity?>(null) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(loanId) {
        loan = viewModel.getLoanById(loanId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loan Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        loan?.let { currentLoan ->
            val schedule = remember(currentLoan) {
                LoanCalculator.generateEMISchedule(
                    currentLoan.principalAmount,
                    currentLoan.interestRate,
                    currentLoan.tenureMonths,
                    currentLoan.emiAmount
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Loan Summary Card
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
                            text = currentLoan.lenderName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Divider()
                        LoanDetailRow("Principal", ExportUtils.formatCurrency(currentLoan.principalAmount))
                        LoanDetailRow("Interest Rate", "${currentLoan.interestRate}%")
                        LoanDetailRow("Tenure", "${currentLoan.tenureMonths} months")
                        LoanDetailRow("EMI", ExportUtils.formatCurrency(currentLoan.emiAmount))
                        LoanDetailRow(
                            "Total Interest",
                            ExportUtils.formatCurrency(LoanCalculator.calculateTotalInterest(schedule))
                        )
                    }
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onNavigateToPrepayment(loanId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Prepayment")
                    }
                    
                    Button(
                        onClick = { if (isPremium) onNavigateToExport(loanId) },
                        modifier = Modifier.weight(1f),
                        enabled = isPremium
                    ) {
                        Text("Export")
                    }
                }
                
                // EMI Schedule
                Text(
                    text = "EMI Schedule",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Month", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text("Principal", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                            Text("Interest", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                            Text("Balance", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                        }
                        Divider()
                    }
                    
                    itemsIndexed(schedule) { _, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.month}", modifier = Modifier.weight(1f))
                            Text("₹${String.format("%.0f", item.principalPaid)}", 
                                fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                            Text("₹${String.format("%.0f", item.interestPaid)}", 
                                fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                            Text("₹${String.format("%.0f", item.remainingBalance)}", 
                                fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoanDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}
