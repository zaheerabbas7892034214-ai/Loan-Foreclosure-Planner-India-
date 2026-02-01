package com.loanplanner.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object AddLoan : Screen("add_loan")
    object EditLoan : Screen("edit_loan/{loanId}") {
        fun createRoute(loanId: Long) = "edit_loan/$loanId"
    }
    object LoanDetail : Screen("loan_detail/{loanId}") {
        fun createRoute(loanId: Long) = "loan_detail/$loanId"
    }
    object Prepayment : Screen("prepayment/{loanId}") {
        fun createRoute(loanId: Long) = "prepayment/$loanId"
    }
    object StrategyComparison : Screen("strategy_comparison")
    object Export : Screen("export/{loanId}") {
        fun createRoute(loanId: Long) = "export/$loanId"
    }
    object Paywall : Screen("paywall")
    object Settings : Screen("settings")
}
