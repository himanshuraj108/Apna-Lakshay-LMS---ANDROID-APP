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
import com.example.lms_android.ui.myseat.MySeatScreen
import com.example.lms_android.ui.attendance.AttendanceScreen
import com.example.lms_android.ui.fee.FeeScreen
import com.example.lms_android.ui.notifications.NotificationsScreen
import com.example.lms_android.ui.public.ContactAdminScreen
import com.example.lms_android.ui.public.PublicSeatsScreen
import com.example.lms_android.ui.planner.PlannerScreen
import com.example.lms_android.ui.chat.ChatScreen

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
            HomeScreen(
                onNavigateToAttendance = { navController.navigate("attendance") },
                onNavigateToMySeat = { navController.navigate("my_seat") },
                onNavigateToFee = { navController.navigate("fee_status") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToPlanner = { navController.navigate("planner") },
                onNavigateToChat = { navController.navigate("chat") }
            )
        }
        composable("attendance") {
            AttendanceScreen(onNavigateBack = { navController.popBackStack() })
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
        
        composable("my_seat") {
            MySeatScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable("fee_status") {
            FeeScreen(onNavigateBack = { navController.popBackStack() })
        }
        
        composable("notifications") {
            NotificationsScreen(onNavigateBack = { navController.popBackStack() })
        }
        
        composable("planner") {
            PlannerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("chat") {
            ChatScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
