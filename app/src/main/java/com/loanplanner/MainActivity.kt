package com.loanplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.loanplanner.ui.navigation.Screen
import com.loanplanner.ui.screens.export.ExportScreen
import com.loanplanner.ui.screens.home.HomeScreen
import com.loanplanner.ui.screens.home.HomeViewModel
import com.loanplanner.ui.screens.loan.AddLoanScreen
import com.loanplanner.ui.screens.loan.LoanDetailScreen
import com.loanplanner.ui.screens.loan.LoanViewModel
import com.loanplanner.ui.screens.paywall.PaywallScreen
import com.loanplanner.ui.screens.prepayment.PrepaymentScreen
import com.loanplanner.ui.screens.settings.SettingsScreen
import com.loanplanner.ui.screens.splash.SplashScreen
import com.loanplanner.ui.screens.splash.SplashViewModel
import com.loanplanner.ui.screens.strategy.StrategyComparisonScreen
import com.loanplanner.ui.theme.LoanForeclosurePlannerTheme

class MainActivity : ComponentActivity() {
    
    private lateinit var app: LoanPlannerApplication
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        app = application as LoanPlannerApplication
        
        setContent {
            LoanForeclosurePlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoanPlannerApp(app)
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        app.billingManager.dispose()
    }
}

@Composable
fun LoanPlannerApp(app: LoanPlannerApplication) {
    val navController = rememberNavController()
    
    val homeViewModel = HomeViewModel(app.loanRepository, app.billingManager)
    val loanViewModel = LoanViewModel(app.loanRepository)
    val splashViewModel = SplashViewModel(app.billingManager)
    
    val isPremium by app.billingManager.isPremiumUser.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                viewModel = splashViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToAddLoan = { navController.navigate(Screen.AddLoan.route) },
                onNavigateToLoanDetail = { loanId ->
                    navController.navigate(Screen.LoanDetail.createRoute(loanId))
                },
                onNavigateToPaywall = { navController.navigate(Screen.Paywall.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToStrategy = { navController.navigate(Screen.StrategyComparison.route) }
            )
        }
        
        composable(Screen.AddLoan.route) {
            AddLoanScreen(
                viewModel = loanViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.LoanDetail.route,
            arguments = listOf(navArgument("loanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong("loanId") ?: 0L
            LoanDetailScreen(
                loanId = loanId,
                viewModel = loanViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPrepayment = { id ->
                    navController.navigate(Screen.Prepayment.createRoute(id))
                },
                onNavigateToExport = { id ->
                    if (isPremium) {
                        navController.navigate(Screen.Export.createRoute(id))
                    }
                },
                isPremium = isPremium
            )
        }
        
        composable(
            route = Screen.Prepayment.route,
            arguments = listOf(navArgument("loanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong("loanId") ?: 0L
            PrepaymentScreen(
                loanId = loanId,
                viewModel = loanViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.StrategyComparison.route) {
            StrategyComparisonScreen(
                viewModel = homeViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.Export.route,
            arguments = listOf(navArgument("loanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong("loanId") ?: 0L
            if (isPremium) {
                ExportScreen(
                    loanId = loanId,
                    viewModel = loanViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        
        composable(Screen.Paywall.route) {
            PaywallScreen(
                billingManager = app.billingManager,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                billingManager = app.billingManager,
                loanRepository = app.loanRepository,
                purchaseRepository = app.purchaseRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
