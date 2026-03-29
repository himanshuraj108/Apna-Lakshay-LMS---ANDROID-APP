package com.example.lms_android.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.UserProfile
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun IdCardDialog(
    onDismiss: () -> Unit
) {
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.apiService.getProfile()
            if (response.success && response.user != null) {
                profile = response.user
            } else {
                errorMessage = "Failed to load profile"
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Network Error"
        } finally {
            isLoading = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable { onDismiss() }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.clickable(enabled = false) {}) { // block clicks
                Column(horizontalAlignment = Alignment.End) {
                    // Close Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                    
                    Spacer(Modifier.height(12.dp))

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF6366F1))
                        }
                    } else if (errorMessage != null) {
                        Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                            Text(text = errorMessage!!, color = Color.White)
                        }
                    } else {
                        profile?.let { user ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                            // ID CARD Surface
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                            ) {
                                Column {
                                    // Top Header Section (Green)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF4C9A62))
                                            .padding(top = 24.dp, bottom = 48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                "APNA LAKSHAY",
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 1.sp
                                            )
                                            Text(
                                                "LIBRARY MANAGEMENT SYSTEM",
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                    }

                                    // White Body Section
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Spacer(modifier = Modifier.height(30.dp)) // Space for overlapping profile pic

                                        // Name
                                        val nameToDisplay = if (user.name.isNullOrEmpty()) "ADMINISTRATOR" else user.name.uppercase()
                                        Text(
                                            text = nameToDisplay,
                                            color = Color(0xFF1F2937),
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )

                                        Spacer(Modifier.height(8.dp))

                                        // Student Badge
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFDCFCE7), RoundedCornerShape(16.dp))
                                                .padding(horizontal = 12.dp, vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "STUDENT",
                                                color = Color(0xFF166534),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            )
                                        }

                                        Spacer(Modifier.height(32.dp))

                                        // Details Grid
                                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                DetailItem("ID NUMBER", user.lmsId ?: "HL-XXXXX", modifier = Modifier.weight(1f))
                                                DetailItem("ASSIGNED SEAT", user.seatNumber ?: "OFFICE", modifier = Modifier.weight(1f), rightAlign = true, valueColor = Color(0xFF8B5CF6))
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                val joinDate = formatDate(user.createdAt)
                                                val mShift = user.shift ?: "FULL"
                                                val formattedShift = if (mShift.contains("SHIFT", ignoreCase=true)) mShift.uppercase() else "$mShift SHIFT".uppercase()
                                                DetailItem("JOINED DATE", joinDate, modifier = Modifier.weight(1f))
                                                DetailItem("SHIFT", formattedShift, modifier = Modifier.weight(1f), rightAlign = true, valueColor = Color(0xFF8B5CF6))
                                            }
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                DetailItem("ADDRESS", user.address ?: "Not provided", modifier = Modifier.fillMaxWidth())
                                            }
                                        }

                                        Spacer(Modifier.height(24.dp))
                                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 2.dp, modifier = Modifier.padding(horizontal = 8.dp))
                                        Spacer(Modifier.height(24.dp))

                                        // Bottom Row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("VALID UNTIL", color = Color(0xFF9CA3AF), fontSize = 10.sp)
                                                Spacer(Modifier.height(4.dp))
                                                val isActive = user.status?.lowercase() == "active"
                                                val validText = if (isActive || user.status == null) "Active Membership" else "Inactive"
                                                val validColor = if (isActive || user.status == null) Color(0xFF10B981) else Color(0xFFEF4444)
                                                Text(validText, color = validColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            // Mock QR
                                            Icon(
                                                Icons.Default.QrCode2,
                                                contentDescription = "QR Code",
                                                tint = Color.Black,
                                                modifier = Modifier.size(60.dp)
                                            )
                                        }
                                        
                                        Spacer(Modifier.height(8.dp))
                                    }
                                }

                                // Profile Picture Overlay
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .offset(y = 56.dp)
                                        .size(100.dp)
                                        .border(6.dp, Color.White, CircleShape)
                                        .background(Color(0xFFE5E7EB), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
                                }
                                
                                // Bottom Green Strip
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .background(Color(0xFF4C9A62))
                                )
                            }

                            Spacer(Modifier.height(24.dp))

                            // Action Buttons
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = { Toast.makeText(context, "Download Coming Soon!", Toast.LENGTH_SHORT).show() },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF60A5FA)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Download PNG", fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)
                                }

                                Button(
                                    onClick = { Toast.makeText(context, "Print PDF Coming Soon!", Toast.LENGTH_SHORT).show() },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B5563)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Print PDF", fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)
                                }
                            }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    rightAlign: Boolean = false,
    valueColor: Color = Color(0xFF1F2937)
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (rightAlign) Alignment.End else Alignment.Start
    ) {
        Text(text = label, color = Color(0xFF9CA3AF), fontSize = 9.sp, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = if (rightAlign) TextAlign.End else TextAlign.Start
        )
    }
}

private fun formatDate(isoString: String?): String {
    if (isoString.isNullOrEmpty()) return "Unknown"
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = parser.parse(isoString)
        date?.let {
            val formatter = SimpleDateFormat("dd MMM yy", Locale.getDefault())
            formatter.format(it)
        } ?: isoString
    } catch (e: Exception) {
        isoString
    }
}
