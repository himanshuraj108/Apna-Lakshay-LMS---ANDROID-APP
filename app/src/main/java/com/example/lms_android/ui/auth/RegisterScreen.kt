package com.example.lms_android.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.RegisterRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onNavigateBackToLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val bgDark = Color(0xFF0D0F16)
    val cardDark = Color(0xFF171A21)
    val textFieldBg = Color(0xFF111319)
    val borderDark = Color.White.copy(alpha = 0.08f)
    val colorOrange = Color(0xFFE87A5D)
    val colorTextSecondary = Color(0xFF9CA3AF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Apna ", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Lakshay", color = colorOrange, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "LIBRARY SYSTEM",
                        color = colorTextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }

                // Back to Login
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigateBackToLogin() }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colorTextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back to Login", color = colorTextSecondary, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main Card
            Card(
                colors = CardDefaults.cardColors(containerColor = cardDark),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, borderDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text("Create account", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Join Apna Lakshay to get started", color = colorTextSecondary, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(32.dp))

                    if (errorMessage != null) {
                        Text(errorMessage!!, color = Color(0xFFFF5252), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 16.dp))
                    }
                    if (successMessage != null) {
                        Text(successMessage!!, color = Color(0xFF4CAF50), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 16.dp))
                    }

                    // ====== FULL NAME ======
                    Text("FULL NAME", color = colorTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = colorTextSecondary.copy(alpha=0.5f), modifier = Modifier.size(20.dp)) },
                        placeholder = { Text("Your full name", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = textFieldBg, unfocusedContainerColor = textFieldBg,
                            focusedBorderColor = colorOrange, unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        singleLine = true, shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ====== EMAIL ADDRESS ======
                    Text("EMAIL ADDRESS", color = colorTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = colorTextSecondary.copy(alpha=0.5f), modifier = Modifier.size(20.dp)) },
                        placeholder = { Text("you@example.com", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = textFieldBg, unfocusedContainerColor = textFieldBg,
                            focusedBorderColor = colorOrange, unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        singleLine = true, shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ====== MOBILE NUMBER ======
                    Text("MOBILE NUMBER", color = colorTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = colorTextSecondary.copy(alpha=0.5f), modifier = Modifier.size(20.dp)) },
                        placeholder = { Text("10-digit number", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = textFieldBg, unfocusedContainerColor = textFieldBg,
                            focusedBorderColor = colorOrange, unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        singleLine = true, shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ====== ADDRESS ======
                    Text("ADDRESS", color = colorTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = colorTextSecondary.copy(alpha=0.5f), modifier = Modifier.size(20.dp)) },
                        placeholder = { Text("Your complete address", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = textFieldBg, unfocusedContainerColor = textFieldBg,
                            focusedBorderColor = colorOrange, unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        singleLine = true, shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ====== PASSWORD ROW ======
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PASSWORD", color = colorTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = colorTextSecondary.copy(alpha=0.5f), modifier = Modifier.size(18.dp)) },
                                trailingIcon = {
                                    /* Using default icons for visibility since custom eye icons might not be available */
                                    val icon = if(passwordVisible) Icons.Default.Check else Icons.Default.Info
                                    Icon(icon, contentDescription = null, tint = colorTextSecondary.copy(alpha=0.5f), modifier = Modifier.size(18.dp).clickable{ passwordVisible = !passwordVisible })
                                },
                                placeholder = { Text("••••••••", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 14.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = textFieldBg, unfocusedContainerColor = textFieldBg, focusedBorderColor = colorOrange, unfocusedBorderColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                singleLine = true, shape = RoundedCornerShape(12.dp),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                            )
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CONFIRM", color = colorTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = colorTextSecondary.copy(alpha=0.5f), modifier = Modifier.size(18.dp)) },
                                trailingIcon = {
                                    val icon = if(confirmPasswordVisible) Icons.Default.Check else Icons.Default.Info
                                    Icon(icon, contentDescription = null, tint = colorTextSecondary.copy(alpha=0.5f), modifier = Modifier.size(18.dp).clickable{ confirmPasswordVisible = !confirmPasswordVisible })
                                },
                                placeholder = { Text("••••••••", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 14.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = textFieldBg, unfocusedContainerColor = textFieldBg, focusedBorderColor = colorOrange, unfocusedBorderColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                singleLine = true, shape = RoundedCornerShape(12.dp),
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    val buttonGradient = Brush.horizontalGradient(listOf(Color(0xFFEA8D60), Color(0xFFDF5B55)))
                    Button(
                        onClick = {
                            if (isLoading) return@Button
                            errorMessage = null
                            successMessage = null
                            
                            if (name.isBlank() || email.isBlank() || mobile.isBlank() || address.isBlank() || password.isBlank()) {
                                errorMessage = "All fields are required"
                                return@Button
                            }
                            if (password != confirmPassword) {
                                errorMessage = "Passwords do not match"
                                return@Button
                            }
                            
                            scope.launch {
                                isLoading = true
                                try {
                                    val req = RegisterRequest(name, email, mobile, address, password)
                                    val res = ApiClient.apiService.register(req)
                                    if (res.success) {
                                        successMessage = res.message ?: "Registration successful! Login credentials have been sent to your email."
                                        name = ""; email = ""; mobile = ""; address = ""; password = ""; confirmPassword = ""
                                    } else {
                                        errorMessage = res.message ?: "Registration failed."
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Network error. Please try again."
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(52.dp).background(buttonGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Create Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Warning Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, colorOrange.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .background(Color(0xFF2C1914), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(colorOrange, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Check Your Email After Registration", color = Color(0xFFFFD28C), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                val bulletText1 = buildAnnotatedString {
                                    append("Your ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                                        append("login credentials ")
                                    }
                                    append("will be sent to your email.")
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    Text("• ", color = colorOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(bulletText1, color = colorTextSecondary, fontSize = 12.sp)
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Top) {
                                    Text("• ", color = colorOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Seat allocation will be assigned by the library admin.", color = colorTextSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // OR divider
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("OR", color = borderDark.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Sign in link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Already have an account? ", color = colorTextSecondary, fontSize = 14.sp)
                        Text(
                            "Sign in", 
                            color = colorOrange, 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateBackToLogin() }
                        )
                    }
                }
            }
        }
    }
}
