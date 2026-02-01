package com.loanplanner.ui.screens.export

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.loanplanner.data.entities.LoanEntity
import com.loanplanner.domain.usecases.LoanCalculator
import com.loanplanner.ui.screens.loan.LoanViewModel
import com.loanplanner.utils.ExportUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    loanId: Long,
    viewModel: LoanViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var loan by remember { mutableStateOf<LoanEntity?>(null) }
    
    LaunchedEffect(loanId) {
        loan = viewModel.getLoanById(loanId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export Options") },
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Export loan data for ${currentLoan.lenderName}")
                
                Button(
                    onClick = {
                        val domainLoan = com.loanplanner.domain.models.Loan(
                            id = currentLoan.id,
                            lenderName = currentLoan.lenderName,
                            principalAmount = currentLoan.principalAmount,
                            interestRate = currentLoan.interestRate,
                            tenureMonths = currentLoan.tenureMonths,
                            emiAmount = currentLoan.emiAmount,
                            startDate = currentLoan.startDate
                        )
                        
                        val file = ExportUtils.exportToCSV(context, domainLoan, schedule)
                        if (file != null) {
                            ExportUtils.shareFile(context, file)
                            Toast.makeText(context, "CSV exported successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export as CSV")
                }
                
                Button(
                    onClick = {
                        val domainLoan = com.loanplanner.domain.models.Loan(
                            id = currentLoan.id,
                            lenderName = currentLoan.lenderName,
                            principalAmount = currentLoan.principalAmount,
                            interestRate = currentLoan.interestRate,
                            tenureMonths = currentLoan.tenureMonths,
                            emiAmount = currentLoan.emiAmount,
                            startDate = currentLoan.startDate
                        )
                        
                        val file = ExportUtils.exportToPDF(context, domainLoan, schedule)
                        if (file != null) {
                            ExportUtils.shareFile(context, file)
                            Toast.makeText(context, "PDF exported successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export as PDF")
                }
            }
        }
    }
}
