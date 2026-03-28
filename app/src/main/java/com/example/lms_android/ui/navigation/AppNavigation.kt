package com.example.lms_android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lms_android.ui.auth.AuthViewModel
import com.example.lms_android.ui.auth.ForgotPasswordScreen
import com.example.lms_android.ui.auth.LoginScreen
import com.example.lms_android.ui.auth.RegisterScreen
import com.example.lms_android.ui.home.HomeScreen
import com.example.lms_android.ui.public.ContactAdminScreen
import com.example.lms_android.ui.public.PublicSeatsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val authViewModel: AuthViewModel = viewModel()
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSeats = {
                    navController.navigate("public_seats")
                },
                onNavigateContactAdmin = {
                    navController.navigate("contact_admin")
                },
                onNavigateForgotPassword = {
                    navController.navigate("forgot_password")
                },
                onNavigateRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateBackToLogin = { navController.popBackStack() }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onResetSuccess = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen()
        }
        composable("contact_admin") {
            ContactAdminScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("public_seats") {
            PublicSeatsScreen(
                onNavigateLogin = {
                    navController.navigate("login") {
                        popUpTo("public_seats") { inclusive = true }
                    }
                }
            )
        }
    }
}
