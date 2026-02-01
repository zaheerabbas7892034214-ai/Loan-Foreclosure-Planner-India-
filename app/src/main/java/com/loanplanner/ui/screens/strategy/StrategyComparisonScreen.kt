package com.loanplanner.ui.screens.strategy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.loanplanner.domain.models.Loan
import com.loanplanner.domain.usecases.StrategyComparator
import com.loanplanner.ui.screens.home.HomeViewModel
import com.loanplanner.utils.ExportUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrategyComparisonScreen(
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit
) {
    val loans by viewModel.loans.collectAsState()
    var extraPayment by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<Pair<com.loanplanner.domain.models.StrategyResult, 
        com.loanplanner.domain.models.StrategyResult>?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Strategy Comparison") },
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
            Text(
                text = "Compare prepayment strategies for ${loans.size} loans",
                fontSize = 16.sp
            )
            
            OutlinedTextField(
                value = extraPayment,
                onValueChange = { extraPayment = it },
                label = { Text("Extra Payment Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = {
                    val extra = extraPayment.toDoubleOrNull() ?: 0.0
                    if (extra > 0 && loans.isNotEmpty()) {
                        val domainLoans = loans.map { 
                            Loan(
                                id = it.id,
                                lenderName = it.lenderName,
                                principalAmount = it.principalAmount,
                                interestRate = it.interestRate,
                                tenureMonths = it.tenureMonths,
                                emiAmount = it.emiAmount,
                                startDate = it.startDate
                            )
                        }
                        results = StrategyComparator.compareStrategies(domainLoans, extra)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = loans.isNotEmpty()
            ) {
                Text("Compare Strategies")
            }
            
            results?.let { (snowball, avalanche) ->
                StrategyCard("Snowball Method", snowball)
                StrategyCard("Avalanche Method", avalanche)
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        val betterStrategy = if (snowball.totalInterestSaved > avalanche.totalInterestSaved) 
                            "Snowball" else "Avalanche"
                        Text(
                            text = "Recommended: $betterStrategy Method",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StrategyCard(name: String, result: com.loanplanner.domain.models.StrategyResult) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Divider()
            Text("Interest Saved: ${ExportUtils.formatCurrency(result.totalInterestSaved)}")
            Text("Time Saved: ${result.totalMonthsSaved} months")
            Text("Payoff Order:", fontWeight = FontWeight.SemiBold)
            result.payoffOrder.forEachIndexed { index, lenderName ->
                Text("  ${index + 1}. $lenderName")
            }
        }
    }
}
