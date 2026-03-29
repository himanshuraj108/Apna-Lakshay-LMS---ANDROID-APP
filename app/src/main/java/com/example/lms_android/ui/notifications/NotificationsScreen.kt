package com.example.lms_android.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lms_android.data.models.NotificationModel
import java.text.SimpleDateFormat
import java.util.Locale

private val BgDark = Color(0xFF0D0F16)
private val CardDark = Color(0xFF13151D)
private val BorderDark = Color.White.copy(alpha = 0.08f)
private val TextSecondary = Color(0xFF9CA3AF)
private val TabActiveBg = Color(0xFF7C3AED)
private val TabInactiveBg = Color.Transparent

enum class NotificationTab {
    ALL, UNREAD, READ
}

@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(NotificationTab.ALL) }

    Scaffold(
        containerColor = BgDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            TopHeader(onNavigateBack)
            Spacer(modifier = Modifier.height(32.dp))

            when (state) {
                is NotificationsState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TabActiveBg)
                    }
                }
                is NotificationsState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Failed to load notifications",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.fetchNotifications() },
                                colors = ButtonDefaults.buttonColors(containerColor = TabActiveBg)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is NotificationsState.Success -> {
                    val notifications = (state as NotificationsState.Success).notifications
                    val unreadCount = notifications.count { !it.isRead }
                    val readCount = notifications.count { it.isRead }

                    TabsRow(
                        selectedTab = selectedTab,
                        allCount = notifications.size,
                        unreadCount = unreadCount,
                        readCount = readCount,
                        onTabSelected = { selectedTab = it }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    val filteredList = when (selectedTab) {
                        NotificationTab.ALL -> notifications
                        NotificationTab.UNREAD -> notifications.filter { !it.isRead }
                        NotificationTab.READ -> notifications.filter { it.isRead }
                    }

                    if (filteredList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No notifications", color = TextSecondary)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(filteredList) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onClick = {
                                        if (!notification.isRead) {
                                            viewModel.markAsRead(notification._id)
                                        }
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(32.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopHeader(onNavigateBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Back Button
        Box(
            modifier = Modifier
                .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.03f))
                .clickable { onNavigateBack() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Back", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
        
        Spacer(Modifier.width(20.dp))
        
        // Titles
        Column {
            Text(
                text = "Notifications",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Your library updates",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun TabsRow(
    selectedTab: NotificationTab,
    allCount: Int,
    unreadCount: Int,
    readCount: Int,
    onTabSelected: (NotificationTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TabItem("All ($allCount)", selectedTab == NotificationTab.ALL) { onTabSelected(NotificationTab.ALL) }
        TabItem("Unread ($unreadCount)", selectedTab == NotificationTab.UNREAD) { onTabSelected(NotificationTab.UNREAD) }
        TabItem("Read ($readCount)", selectedTab == NotificationTab.READ) { onTabSelected(NotificationTab.READ) }
    }
}

@Composable
private fun RowScope.TabItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) TabActiveBg else TabInactiveBg)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Bold title and transparent count if unselected
        val color = if (isSelected) Color.White else TextSecondary
        Text(
            text = label,
            color = color,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun NotificationCard(notification: NotificationModel, onClick: () -> Unit) {
    val type = notification.type.lowercase()
    
    val icon = when(type) {
        "seat", "shift" -> Icons.Default.Chair
        "fee", "payment" -> Icons.Default.Payments
        "announcement" -> Icons.Default.Campaign
        else -> Icons.Default.Notifications
    }
    
    val iconTint = when(type) {
        "seat", "shift" -> Color(0xFF60A5FA)
        "fee", "payment" -> Color(0xFF34D399)
        "announcement" -> Color(0xFFFBBF24)
        else -> Color(0xFFA78BFA)
    }
    
    val bgColor = when(type) {
        "seat", "shift" -> Color(0xFF1E3A8A).copy(alpha = 0.3f)
        "fee", "payment" -> Color(0xFF064E3B).copy(alpha = 0.3f)
        "announcement" -> Color(0xFF78350F).copy(alpha = 0.3f)
        else -> Color(0xFF4C1D95).copy(alpha = 0.3f)
    }
    
    val tagText = when(type) {
        "seat", "shift" -> "SEAT"
        "fee", "payment" -> "PAYMENT"
        "announcement" -> "ANNOUNCEMENT"
        else -> "ALERT"
    }

    // Format date string
    val formattedDate = remember(notification.createdAt) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = parser.parse(notification.createdAt)
            date?.let {
                val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                formatter.format(it)
            } ?: notification.createdAt
        } catch (e: Exception) {
            notification.createdAt
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (!notification.isRead) Color.White.copy(alpha = 0.15f) else BorderDark,
                shape = RoundedCornerShape(16.dp)
            )
            .background(if (!notification.isRead) Color(0xFF191B24) else CardDark)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // Red dot indicator for unread
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(8.dp)
                    .background(Color(0xFFF87171), CircleShape)
            )
        }

        Row(verticalAlignment = Alignment.Top) {
            // Context icon circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(bgColor, CircleShape)
                    .border(1.dp, iconTint.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Header with Tag and Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tag Pill
                    Box(
                        modifier = Modifier
                            .background(bgColor, RoundedCornerShape(10.dp))
                            .border(1.dp, iconTint.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tagText,
                            color = iconTint,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = formattedDate,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Title
                Text(
                    text = notification.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(Modifier.height(4.dp))

                // Message
                Text(
                    text = notification.message,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
