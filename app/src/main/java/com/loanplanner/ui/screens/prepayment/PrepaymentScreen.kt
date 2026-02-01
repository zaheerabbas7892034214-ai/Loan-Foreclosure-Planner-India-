package com.loanplanner.ui.screens.prepayment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loanplanner.data.entities.LoanEntity
import com.loanplanner.domain.usecases.LoanCalculator
import com.loanplanner.ui.screens.loan.LoanViewModel
import com.loanplanner.utils.ExportUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrepaymentScreen(
    loanId: Long,
    viewModel: LoanViewModel,
    onNavigateBack: () -> Unit
) {
    var loan by remember { mutableStateOf<LoanEntity?>(null) }
    var lumpSum by remember { mutableStateOf("") }
    var extraMonthly by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<com.loanplanner.domain.models.PrepaymentResult?>(null) }
    
    LaunchedEffect(loanId) {
        loan = viewModel.getLoanById(loanId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prepayment Simulation") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        loan?.let { currentLoan ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = lumpSum,
                    onValueChange = { lumpSum = it },
                    label = { Text("Lump Sum Payment (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = extraMonthly,
                    onValueChange = { extraMonthly = it },
                    label = { Text("Extra Monthly Payment (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Button(
                    onClick = {
                        val lumpSumAmt = lumpSum.toDoubleOrNull() ?: 0.0
                        val extraMonthlyAmt = extraMonthly.toDoubleOrNull() ?: 0.0
                        
                        result = LoanCalculator.simulatePrepayment(
                            currentLoan.principalAmount,
                            currentLoan.interestRate,
                            currentLoan.tenureMonths,
                            currentLoan.emiAmount,
                            lumpSumAmt,
                            extraMonthlyAmt
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Calculate Savings")
                }
                
                result?.let { prepaymentResult ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Savings Summary",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Divider()
                            ResultRow("Interest Saved", ExportUtils.formatCurrency(prepaymentResult.interestSaved))
                            ResultRow("Tenure Reduced", "${prepaymentResult.tenureReduced} months")
                            ResultRow("New Tenure", "${prepaymentResult.newEMISchedule.size} months")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}
