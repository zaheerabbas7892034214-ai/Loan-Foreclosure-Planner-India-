package com.loanplanner.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.loanplanner.domain.models.EMIScheduleItem
import com.loanplanner.domain.models.Loan
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {
    
    fun exportToCSV(context: Context, loan: Loan, schedule: List<EMIScheduleItem>): File? {
        try {
            val fileName = "loan_${loan.lenderName.replace(" ", "_")}_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            val csvContent = StringBuilder()
            csvContent.append("Loan Details\n")
            csvContent.append("Lender,${loan.lenderName}\n")
            csvContent.append("Principal,${loan.principalAmount}\n")
            csvContent.append("Interest Rate,${loan.interestRate}%\n")
            csvContent.append("Tenure,${loan.tenureMonths} months\n")
            csvContent.append("EMI,${loan.emiAmount}\n")
            csvContent.append("\nEMI Schedule\n")
            csvContent.append("Month,EMI Amount,Principal Paid,Interest Paid,Remaining Balance\n")
            
            schedule.forEach { item ->
                csvContent.append("${item.month},${item.emiAmount},${item.principalPaid},${item.interestPaid},${item.remainingBalance}\n")
            }
            
            file.writeText(csvContent.toString())
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    fun exportToPDF(context: Context, loan: Loan, schedule: List<EMIScheduleItem>): File? {
        try {
            val fileName = "loan_${loan.lenderName.replace(" ", "_")}_${System.currentTimeMillis()}.txt"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            val pdfContent = StringBuilder()
            pdfContent.append("LOAN FORECLOSURE PLANNER\n")
            pdfContent.append("========================\n\n")
            pdfContent.append("Loan Details:\n")
            pdfContent.append("--------------\n")
            pdfContent.append("Lender: ${loan.lenderName}\n")
            pdfContent.append("Principal: ₹${String.format("%.2f", loan.principalAmount)}\n")
            pdfContent.append("Interest Rate: ${loan.interestRate}% per annum\n")
            pdfContent.append("Tenure: ${loan.tenureMonths} months\n")
            pdfContent.append("EMI: ₹${String.format("%.2f", loan.emiAmount)}\n")
            pdfContent.append("\n\nEMI Schedule:\n")
            pdfContent.append("=============\n\n")
            pdfContent.append(String.format("%-6s %-12s %-15s %-15s %-15s\n", 
                "Month", "EMI", "Principal", "Interest", "Balance"))
            pdfContent.append("-".repeat(70) + "\n")
            
            schedule.forEach { item ->
                pdfContent.append(String.format("%-6d %-12.2f %-15.2f %-15.2f %-15.2f\n",
                    item.month,
                    item.emiAmount,
                    item.principalPaid,
                    item.interestPaid,
                    item.remainingBalance
                ))
            }
            
            val totalInterest = schedule.sumOf { it.interestPaid }
            pdfContent.append("\n" + "-".repeat(70) + "\n")
            pdfContent.append("Total Interest Paid: ₹${String.format("%.2f", totalInterest)}\n")
            
            file.writeText(pdfContent.toString())
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    fun shareFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = if (file.extension == "csv") "text/csv" else "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(intent, "Share file"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun formatCurrency(amount: Double): String {
        return "₹${String.format("%,.2f", amount)}"
    }
}
