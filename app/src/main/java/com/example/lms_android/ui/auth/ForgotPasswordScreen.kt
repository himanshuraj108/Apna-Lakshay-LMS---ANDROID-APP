package com.example.lms_android.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.ForgotPasswordRequest
import com.example.lms_android.data.models.ResetPasswordRequest
import com.example.lms_android.data.models.VerifyOtpRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(onNavigateBack: () -> Unit, onResetSuccess: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()

    val bgDark = Color(0xFF0A0A0A)
    val cardDark = Color(0xFF141414)
    val borderDark = Color.White.copy(alpha = 0.08f)
    val accentOrange = Color(0xFFE87A5D)
    val textMuted = Color(0xFF9CA3AF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(cardDark, RoundedCornerShape(20.dp))
                .border(1.dp, borderDark, RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(0.05f))
                            .border(1.dp, borderDark, CircleShape)
                            .clickable {
                                if (step > 1 && !isLoading) {
                                    step--
                                    errorMessage = null
                                } else {
                                    onNavigateBack()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = when(step) {
                                1 -> "Forgot Password"
                                2 -> "Verify OTP"
                                else -> "Set New Password"
                            },
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Step $step of 3", color = textMuted, fontSize = 12.sp)
                    }
                }

                // Step Indicators
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(3) { i ->
                        Box(
                            modifier = Modifier
                                .width(if (i + 1 == step) 20.dp else 12.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (i + 1 == step) accentOrange else Color.White.copy(0.1f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage != null) {
                Text(errorMessage!!, color = Color(0xFFEF4444), fontSize = 13.sp, modifier = Modifier.padding(bottom = 16.dp))
            }

            // Step Content
            when (step) {
                1 -> {
                    Text("Enter your registered email to receive a verification code.", color = textMuted, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("EMAIL ADDRESS", color = textMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("hello@example.com", color = Color.White.copy(0.3f)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White.copy(0.3f), modifier = Modifier.size(20.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(0.02f),
                            unfocusedContainerColor = Color.White.copy(0.02f),
                            focusedBorderColor = accentOrange,
                            unfocusedBorderColor = borderDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = accentOrange
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                2 -> {
                    Text("Enter the 4-digit code sent to ", color = textMuted, fontSize = 14.sp)
                    Text(email, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("OTP CODE", color = textMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { if (it.length <= 4) otp = it },
                        placeholder = { Text("· · · ·", color = Color.White.copy(0.3f), fontSize = 24.sp, letterSpacing = 8.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(0.02f),
                            unfocusedContainerColor = Color.White.copy(0.02f),
                            focusedBorderColor = accentOrange,
                            unfocusedBorderColor = borderDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = accentOrange
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center, fontSize = 24.sp, letterSpacing = 8.sp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "← Change email",
                            color = accentOrange,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { step = 1; otp = ""; errorMessage = null }
                        )
                    }
                }
                3 -> {
                    Text("Create a strong new password for your account.", color = textMuted, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("NEW PASSWORD", color = textMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = { Text("New password", color = Color.White.copy(0.3f)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White.copy(0.3f), modifier = Modifier.size(20.dp)) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(0.02f),
                            unfocusedContainerColor = Color.White.copy(0.02f),
                            focusedBorderColor = accentOrange,
                            unfocusedBorderColor = borderDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = accentOrange
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("CONFIRM PASSWORD", color = textMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("Confirm password", color = Color.White.copy(0.3f)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White.copy(0.3f), modifier = Modifier.size(20.dp)) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(0.02f),
                            unfocusedContainerColor = Color.White.copy(0.02f),
                            focusedBorderColor = accentOrange,
                            unfocusedBorderColor = borderDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = accentOrange
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = {
                    if (isLoading) return@Button
                    errorMessage = null
                    scope.launch {
                        isLoading = true
                        try {
                            when (step) {
                                1 -> {
                                    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                        errorMessage = "Please enter a valid email."
                                    } else {
                                        val res = ApiClient.apiService.forgotPassword(ForgotPasswordRequest(email))
                                        if (res.success) {
                                            step = 2
                                        } else {
                                            errorMessage = res.message ?: "Failed to send OTP."
                                        }
                                    }
                                }
                                2 -> {
                                    if (otp.length != 4) {
                                        errorMessage = "OTP must be 4 digits."
                                    } else {
                                        val res = ApiClient.apiService.verifyOtp(VerifyOtpRequest(email, otp))
                                        if (res.success) {
                                            step = 3
                                        } else {
                                            errorMessage = res.message ?: "Invalid OTP."
                                        }
                                    }
                                }
                                3 -> {
                                    if (newPassword.length < 6) {
                                        errorMessage = "Password must be at least 6 characters."
                                    } else if (newPassword != confirmPassword) {
                                        errorMessage = "Passwords do not match."
                                    } else {
                                        val res = ApiClient.apiService.resetPassword(ResetPasswordRequest(email, otp, newPassword))
                                        if (res.success) {
                                            onResetSuccess()
                                        } else {
                                            errorMessage = res.message ?: "Failed to reset password."
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            errorMessage = "Network error occurred."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFE87A5D), Color(0xFFDA6042))),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        val btnText = when(step) {
                            1 -> "Send Verification Code"
                            2 -> "Verify Code"
                            else -> "Set New Password"
                        }
                        Text(btnText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
