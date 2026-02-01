package com.loanplanner.ui.screens.loan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.loanplanner.data.entities.LoanEntity
import com.loanplanner.domain.usecases.LoanCalculator
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLoanScreen(
    viewModel: LoanViewModel,
    onNavigateBack: () -> Unit
) {
    var lenderName by remember { mutableStateOf("") }
    var principal by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var tenure by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    val loanState by viewModel.loanState.collectAsState()
    
    LaunchedEffect(loanState) {
        if (loanState is LoanViewModel.LoanState.Success) {
            onNavigateBack()
            viewModel.resetState()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Loan") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = lenderName,
                onValueChange = { lenderName = it },
                label = { Text("Lender Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = principal,
                onValueChange = { principal = it },
                label = { Text("Principal Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = interestRate,
                onValueChange = { interestRate = it },
                label = { Text("Interest Rate (% per annum)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = tenure,
                onValueChange = { tenure = it },
                label = { Text("Loan Tenure (months)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = {
                    scope.launch {
                        val p = principal.toDoubleOrNull() ?: 0.0
                        val r = interestRate.toDoubleOrNull() ?: 0.0
                        val t = tenure.toIntOrNull() ?: 0
                        
                        if (lenderName.isNotBlank() && p > 0 && r > 0 && t > 0) {
                            val emi = LoanCalculator.calculateEMI(p, r, t)
                            
                            val loan = LoanEntity(
                                lenderName = lenderName,
                                principalAmount = p,
                                interestRate = r,
                                tenureMonths = t,
                                emiAmount = emi,
                                startDate = System.currentTimeMillis()
                            )
                            
                            viewModel.saveLoan(loan)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = loanState !is LoanViewModel.LoanState.Loading
            ) {
                if (loanState is LoanViewModel.LoanState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Loan")
                }
            }
            
            if (loanState is LoanViewModel.LoanState.Error) {
                Text(
                    text = (loanState as LoanViewModel.LoanState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
