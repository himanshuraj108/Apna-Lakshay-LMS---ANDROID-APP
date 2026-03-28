package com.example.lms_android.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import com.example.lms_android.data.api.ApiClient
import kotlinx.coroutines.launch
import androidx.compose.ui.window.Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToSeats: () -> Unit,
    onNavigateContactAdmin: () -> Unit,
    onNavigateForgotPassword: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var showAttendanceForm by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onNavigateToHome()
        }
    }

    // Colors matching screenshot
    val colorBg = Color(0xFF0D0F16)         // Pure Dark Background
    val colorCardBg = Color(0xFF171A21)     // Slightly lighter dark for card
    val colorTextFieldBg = Color(0xFF111319) // Darker text field
    val colorOrange = Color(0xFFE87A5D)     // The accent orange
    val colorTextSecondary = Color(0xFF9CA3AF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Logo Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Apna ",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Lakshay",
                    color = colorOrange,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "LIBRARY SYSTEM",
                color = colorTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Top Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // View Seats Button
                Button(
                    onClick = onNavigateToSeats,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colorOrange.copy(alpha = 0.05f),
                        contentColor = colorOrange
                    ),
                    border = BorderStroke(1.dp, colorOrange.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.List, contentDescription = "View Seats", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Seats", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                // Location Button
                Button(
                    onClick = onNavigateContactAdmin,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colorTextFieldBg,
                        contentColor = colorTextSecondary
                    ),
                    border = BorderStroke(1.dp, colorTextSecondary.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Location", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login Card
            Card(
                colors = CardDefaults.cardColors(containerColor = colorCardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text("Welcome back", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Sign in to access your dashboard", color = colorTextSecondary, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Email Field
                    Text("EMAIL ADDRESS", color = colorTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = colorTextSecondary, modifier = Modifier.size(20.dp)) },
                        placeholder = { Text("you@example.com", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colorTextFieldBg,
                            unfocusedContainerColor = colorTextFieldBg,
                            focusedBorderColor = colorOrange,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Password Field
                    Text("PASSWORD", color = colorTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = colorTextSecondary, modifier = Modifier.size(20.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colorTextFieldBg,
                            unfocusedContainerColor = colorTextFieldBg,
                            focusedBorderColor = colorOrange,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Forgot password?",
                        color = colorOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onNavigateForgotPassword() }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Error message logic
                    if (authState is AuthState.Error) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Sign In Button
                    val buttonGradient = Brush.horizontalGradient(listOf(Color(0xFFEA8D60), Color(0xFFDF5B55)))
                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = authState !is AuthState.Loading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(buttonGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            if (authState is AuthState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Sign In", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = "Sign In", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "OR",
                        color = colorTextSecondary.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Don't have an account? ", color = colorTextSecondary, fontSize = 13.sp)
                        Text(
                            text = "Create account",
                            color = colorOrange,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateRegister() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Secure", tint = colorTextSecondary.copy(alpha=0.5f), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Protected by secure encryption",
                    color = colorTextSecondary.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(140.dp)) // Extra padding so it doesn't hide behind FAB
        }

        // Attendance FAB (Bottom Right with clear boundaries)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 24.dp)
        ) {
            val greenGradient = Brush.verticalGradient(listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)))
            Button(
                onClick = { showAttendanceForm = true },
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0xFF4CAF50), ambientColor = Color(0xFF4CAF50))
            ) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(60.dp)
                        .background(greenGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Attendance", tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Attendance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("MARK NOW", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    }
                }
            }
        }

        if (showAttendanceForm) {
            AttendanceDialog(onDismiss = { showAttendanceForm = false })
        }
    }
}

@Composable
fun AttendanceDialog(onDismiss: () -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var scanResult by remember { mutableStateOf<com.example.lms_android.data.models.KioskAttendanceResponse?>(null) }
    var manualToken by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    // An outer wrapper to mock the top green highlight inside the Card
    Dialog(onDismissRequest = onDismiss) {
        Box {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13151D)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Phone Icon Box
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF4CAF50).copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (step == 1) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                } else if (step == 2) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                val title = when (step) {
                                    1 -> "Mark Attendance"
                                    2 -> "Scan Kiosk QR"
                                    else -> "Attendance Marked!"
                                }
                                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Apna Lakshay Library", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                            }
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF9CA3AF))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (errorMsg != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEF4444).copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(errorMsg!!, color = Color(0xFFF87171), fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (step == 1) {
                        Text(
                            text = "Enter your registered phone number to scan the attendance QR",
                            color = Color(0xFF9CA3AF),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("PHONE NUMBER", color = Color(0xFF9CA3AF), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1A1D27),
                                unfocusedContainerColor = Color(0xFF1A1D27),
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = {
                                Text("+91", color = Color(0xFF9CA3AF), fontSize = 14.sp, modifier = Modifier.padding(start = 16.dp, end = 8.dp))
                            },
                            placeholder = { Text("98765 43210", color = Color(0xFF9CA3AF).copy(alpha=0.5f), fontSize = 14.sp) },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (phoneNumber.length < 10) {
                                    errorMsg = "Enter a valid 10-digit phone number"
                                    return@Button
                                }
                                errorMsg = null
                                isLoading = true
                                scope.launch {
                                    try {
                                        val response = ApiClient.apiService.checkPhone(com.example.lms_android.data.models.CheckPhoneRequest(phoneNumber))
                                        if (response.success) {
                                            step = 2
                                        } else {
                                            errorMsg = response.message ?: "No student found with this phone number"
                                        }
                                    } catch (e: Exception) {
                                        errorMsg = "No student found with this phone number"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Open Scanner", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    } else if (step == 2) {
                        // Kiosk Scan View
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Text("Point your camera at the ", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                            Text("Kiosk QR code", color = Color(0xFF4CAF50), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(" on the wall", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .background(Color.Black, RoundedCornerShape(12.dp))
                                .border(2.dp, Color(0xFF4CAF50).copy(alpha=0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                                val strokeW = 12f
                                val length = 60f
                                val col = Color.White
                                
                                // Top Left
                                drawLine(col, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Offset(length, 0f), strokeW, androidx.compose.ui.graphics.StrokeCap.Square)
                                drawLine(col, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Offset(0f, length), strokeW, androidx.compose.ui.graphics.StrokeCap.Square)
                                
                                // Top Right
                                drawLine(col, androidx.compose.ui.geometry.Offset(size.width, 0f), androidx.compose.ui.geometry.Offset(size.width - length, 0f), strokeW, androidx.compose.ui.graphics.StrokeCap.Square)
                                drawLine(col, androidx.compose.ui.geometry.Offset(size.width, 0f), androidx.compose.ui.geometry.Offset(size.width, length), strokeW, androidx.compose.ui.graphics.StrokeCap.Square)
                                
                                // Bottom Left
                                drawLine(col, androidx.compose.ui.geometry.Offset(0f, size.height), androidx.compose.ui.geometry.Offset(length, size.height), strokeW, androidx.compose.ui.graphics.StrokeCap.Square)
                                drawLine(col, androidx.compose.ui.geometry.Offset(0f, size.height), androidx.compose.ui.geometry.Offset(0f, size.height - length), strokeW, androidx.compose.ui.graphics.StrokeCap.Square)
                                
                                // Bottom Right
                                drawLine(col, androidx.compose.ui.geometry.Offset(size.width, size.height), androidx.compose.ui.geometry.Offset(size.width - length, size.height), strokeW, androidx.compose.ui.graphics.StrokeCap.Square)
                                drawLine(col, androidx.compose.ui.geometry.Offset(size.width, size.height), androidx.compose.ui.geometry.Offset(size.width, size.height - length), strokeW, androidx.compose.ui.graphics.StrokeCap.Square)
                            }
                            
                            // Four small white dots in the corners
                            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp).size(4.dp).background(Color.White, RoundedCornerShape(2.dp)))
                                Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(4.dp).background(Color.White, RoundedCornerShape(2.dp)))
                                Box(modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).size(4.dp).background(Color.White, RoundedCornerShape(2.dp)))
                                Box(modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).size(4.dp).background(Color.White, RoundedCornerShape(2.dp)))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Manual Token Input for Simulation 
                        OutlinedTextField(
                            value = manualToken,
                            onValueChange = { manualToken = it },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1A1D27),
                                unfocusedContainerColor = Color(0xFF1A1D27),
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("Paste 'attendance_qr_token' here", color = Color(0xFF9CA3AF).copy(alpha=0.5f), fontSize = 14.sp) },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (manualToken.isEmpty()) {
                                    errorMsg = "Enter a mock kiosk token"
                                    return@Button
                                }
                                errorMsg = null
                                isLoading = true
                                scope.launch {
                                    try {
                                        val req = com.example.lms_android.data.models.KioskAttendanceRequest(phoneNumber, manualToken)
                                        val res = ApiClient.apiService.markKioskAttendance(req)
                                        if (res.success) {
                                            scanResult = res
                                            step = 3
                                        } else {
                                            errorMsg = res.message
                                        }
                                    } catch (e: Exception) {
                                        // A naive parse because Retrofit doesn't give clean JSON on 4xx/5xx by default
                                        errorMsg = "Failed to mark attendance: Invalid Token or Expiration"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Simulate QR Scan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    } else if (step == 3) {
                        scanResult?.let { res ->
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                val isCheckIn = res.type == "check-in"
                                val bgColors = if (isCheckIn) listOf(Color(0xFF16A34A), Color(0xFF15803D)) else listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(Brush.linearGradient(bgColors), RoundedCornerShape(32.dp))
                                        .shadow(16.dp, RoundedCornerShape(32.dp), ambientColor = bgColors[0], spotColor = bgColors[0]),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(if (isCheckIn) "✅" else "👋", fontSize = 28.sp)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(res.student?.name ?: "Unknown Info", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(res.message, color = Color(0xFF9CA3AF), fontSize = 14.sp)
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${res.time ?: ""} · Seat ${res.student?.seat ?: "No Seat"}", color = Color(0xFF6B7280), fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
                                        .border(1.dp, Color.White.copy(alpha=0.07f), RoundedCornerShape(16.dp))
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text("Do you want to log into your dashboard?", color=Color(0xFF9CA3AF), fontSize=12.sp)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Button(
                                                onClick = { onDismiss() /* later: switch to login flow */ },
                                                modifier = Modifier.weight(1f).height(40.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Login", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }
                                            Button(
                                                onClick = onDismiss,
                                                modifier = Modifier.weight(1f).height(40.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                                border = BorderStroke(1.dp, Color.White.copy(alpha=0.1f)),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Later", color = Color(0xFF9CA3AF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Pager indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (step == 1) {
                            Box(modifier = Modifier.width(24.dp).height(4.dp).background(Color(0xFF4CAF50), RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(4.dp).background(Color(0xFF4A4E5C), RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(4.dp).background(Color(0xFF4A4E5C), RoundedCornerShape(2.dp)))
                        } else if (step == 2) {
                            Box(modifier = Modifier.size(4.dp).background(Color(0xFF4A4E5C), RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.width(24.dp).height(4.dp).background(Color(0xFF4CAF50), RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(4.dp).background(Color(0xFF4A4E5C), RoundedCornerShape(2.dp)))
                        } else {
                            Box(modifier = Modifier.size(4.dp).background(Color(0xFF4A4E5C), RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(4.dp).background(Color(0xFF4A4E5C), RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.width(24.dp).height(4.dp).background(Color(0xFF4CAF50), RoundedCornerShape(2.dp)))
                        }
                    } // closes Row
                } // closes Column
            } // closes Card
            
            // Top Green Glow Header line
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(180.dp)
                    .height(4.dp)
                    .background(Color(0xFF81C784), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            )
        }
    }
}
