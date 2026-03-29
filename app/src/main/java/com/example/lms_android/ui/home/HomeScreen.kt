package com.example.lms_android.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lms_android.data.TokenManager
import com.example.lms_android.data.models.DashboardData

val bgDark = Color(0xFF0D0F16)
val borderDark = Color.White.copy(alpha = 0.08f)
val colorOrange = Color(0xFFE87A5D)
val colorTextSecondary = Color(0xFF9CA3AF)

@Composable
fun HomeScreen(
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToMySeat: () -> Unit = {},
    onNavigateToFee: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val homeState by viewModel.homeState.collectAsState()

    Scaffold(
        containerColor = bgDark,
        floatingActionButton = { MarkAttendanceFab() },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (homeState) {
                is HomeState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = colorOrange)
                }
                is HomeState.Error -> {
                    val error = (homeState as HomeState.Error).message
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = error, color = Color.White, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchDashboard() },
                            colors = ButtonDefaults.buttonColors(containerColor = colorOrange, contentColor = Color.White)
                        ) {
                            Text("Retry", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is HomeState.Success -> {
                    val data = (homeState as HomeState.Success).dashboard
                    DashboardContent(data, onNavigateToAttendance, onNavigateToMySeat, onNavigateToFee, onNavigateToNotifications, onNavigateToPlanner)
                }
            }
        }
    }
}

@Composable
fun DashboardContent(
    data: DashboardData,
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToMySeat: () -> Unit = {},
    onNavigateToFee: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {}
) {
    var showIdCard by remember { mutableStateOf(false) }

    if (showIdCard) {
        IdCardDialog(onDismiss = { showIdCard = false })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        DashboardTopBar(
            unreadCount = data.unreadNotifications ?: 0,
            onNavigateToNotifications = onNavigateToNotifications
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        ProfileHeaderCard(name = data.studentName ?: TokenManager.getUserName(), active = data.isActive ?: true)
        Spacer(modifier = Modifier.height(16.dp))
        
        MetricsGrid(
            data = data,
            onNavigateToAttendance = onNavigateToAttendance,
            onNavigateToMySeat = onNavigateToMySeat,
            onNavigateToFee = onNavigateToFee,
            onNavigateToNotifications = onNavigateToNotifications
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        QuickActionsSection(
            doubtCredits = data.doubtCredits ?: 0, 
            onShowIdCard = { showIdCard = true },
            onNavigateToPlanner = onNavigateToPlanner
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        ResourceCenterSection()
        Spacer(modifier = Modifier.height(40.dp))
        
        FooterSection()
        Spacer(modifier = Modifier.height(100.dp)) // Padding for FAB
    }
}

@Composable
fun DashboardTopBar(
    unreadCount: Int = 0,
    onNavigateToNotifications: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Apna ", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "Lakshay", color = colorOrange, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }
        
        // Actions
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.clickable { onNavigateToNotifications() }) {
                Icon(
                    Icons.Default.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = colorTextSecondary,
                    modifier = Modifier.size(24.dp)
                )
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(8.dp)
                            .border(2.dp, bgDark, CircleShape)
                            .background(Color(0xFFF87171), CircleShape)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, borderDark, RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PersonOutline, contentDescription = "Profile", tint = colorTextSecondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ProfileHeaderCard(name: String, active: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderDark, RoundedCornerShape(16.dp))
            .background(Color(0xFF13151D), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    // Status dot
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 2.dp)
                            .size(12.dp)
                            .border(2.dp, Color(0xFF13151D), CircleShape)
                            .background(Color(0xFF10B981), CircleShape)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Name & Wave
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name.uppercase(),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("👋", fontSize = 16.sp)
                }
            }
            
            // Active Pill
            Box(
                modifier = Modifier
                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .background(Color(0xFF064E3B).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Active", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MetricsGrid(
    data: DashboardData,
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToMySeat: () -> Unit = {},
    onNavigateToFee: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            // My Seat
            MetricCard(
                modifier = Modifier.weight(1f).clickable { onNavigateToMySeat() },
                title = "MY SEAT",
                icon = Icons.Default.Chair,
                iconTint = Color(0xFF60A5FA),
                iconBg = Color(0xFF1E3A8A).copy(alpha = 0.3f),
                mainValue = data.seat?.number ?: "OFFICE",
                subValue = "${data.seat?.shift ?: "FULL SHIFT"} Shift"
            )
            // Attendance
            MetricCard(
                modifier = Modifier.weight(1f).clickable { onNavigateToAttendance() },
                title = "ATTENDANCE",
                icon = Icons.Default.CalendarToday,
                iconTint = Color(0xFF34D399),
                iconBg = Color(0xFF064E3B).copy(alpha = 0.3f),
                mainValue = "${data.attendance?.percentage ?: 0}%",
                mainColor = Color(0xFFF87171),
                subValue = "${data.attendance?.present ?: 0} / ${data.attendance?.total ?: 0} days",
                tagText = "Rank #${data.attendance?.rank ?: "-"}",
                tagColor = Color(0xFFA78BFA),
                tagBg = Color(0xFF4C1D95).copy(alpha = 0.3f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            val isPaid = data.fee?.status?.lowercase() == "paid"
            // Fee Status
            MetricCard(
                modifier = Modifier.weight(1f).clickable { onNavigateToFee() },
                title = "FEE STATUS",
                icon = Icons.Default.Payments,
                iconTint = Color(0xFFFBBF24),
                iconBg = Color(0xFF78350F).copy(alpha = 0.3f),
                mainValue = "₹${data.fee?.amount ?: 0}",
                subValue = "", // Empty since status is tag
                tagText = data.fee?.status?.uppercase() ?: "UNPAID",
                tagColor = if (isPaid) Color(0xFF34D399) else Color(0xFFF87171),
                tagBg = if (isPaid) Color(0xFF064E3B).copy(alpha = 0.3f) else Color(0xFF7F1D1D).copy(alpha = 0.3f)
            )
            // Alerts
            MetricCard(
                modifier = Modifier.weight(1f).clickable { onNavigateToNotifications() },
                title = "ALERTS",
                icon = Icons.Default.NotificationsActive,
                iconTint = Color(0xFFF472B6),
                iconBg = Color(0xFF831843).copy(alpha = 0.3f),
                mainValue = "${data.unreadNotifications ?: 0}",
                subValue = "Unread notifications"
            )
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    mainValue: String,
    mainColor: Color = Color.White,
    subValue: String,
    tagText: String? = null,
    tagColor: Color = Color.Transparent,
    tagBg: Color = Color.Transparent
) {
    Box(
        modifier = modifier
            .border(1.dp, borderDark, RoundedCornerShape(16.dp))
            .background(Color(0xFF13151D), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).background(iconBg, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, color = colorTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(mainValue, color = mainColor, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text(subValue, color = colorTextSecondary, fontSize = 12.sp)
                if (tagText != null) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, tagColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .background(tagBg, RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(tagText, color = tagColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Quick Actions Section
@Composable
fun QuickActionsSection(
    doubtCredits: Int, 
    onShowIdCard: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderDark, RoundedCornerShape(16.dp))
            .background(Color(0xFF13151D), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).background(Color(0xFF4C1D95).copy(alpha=0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = "Quick", tint = Color(0xFFA78BFA), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Quick Actions", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            // Grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(modifier = Modifier.weight(1f), title = "ID Card", icon = Icons.Default.Badge, iconTint = Color(0xFF60A5FA), bgTint = Color(0xFF1E3A8A), onClick = onShowIdCard)
                    QuickActionCard(modifier = Modifier.weight(1f), title = "Planner", icon = Icons.Default.MenuBook, iconTint = Color(0xFFF472B6), bgTint = Color(0xFF831843), onClick = onNavigateToPlanner)
                    QuickActionCard(modifier = Modifier.weight(1f), title = "Discussion", icon = Icons.Default.ChatBubbleOutline, iconTint = Color(0xFFFBBF24), bgTint = Color(0xFF78350F))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(modifier = Modifier.weight(1f), title = "Newspaper", icon = Icons.Default.Article, iconTint = Color(0xFFA78BFA), bgTint = Color(0xFF4C1D95))
                    QuickActionCard(modifier = Modifier.weight(1f), title = "Current Affairs", icon = Icons.Default.GridView, iconTint = Color(0xFF38BDF8), bgTint = Color(0xFF0C4A6E), badgeText = "• LIVE", badgeColor = Color(0xFFF87171))
                    QuickActionCard(modifier = Modifier.weight(1f), title = "Exam Alerts", icon = Icons.Default.ErrorOutline, iconTint = Color(0xFFFACC15), bgTint = Color(0xFF713F12), badgeText = "• LIVE", badgeColor = Color(0xFFF87171))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(modifier = Modifier.weight(1f), title = "My Report", icon = Icons.Default.Description, iconTint = Color(0xFF34D399), bgTint = Color(0xFF064E3B), badgeText = "NEW", badgeColor = Color(0xFFFBBF24))
                    QuickActionCard(modifier = Modifier.weight(1f), title = "Ask AI", subtitle = "$doubtCredits credits left", icon = Icons.Default.AutoAwesome, iconTint = Color(0xFFFDE047), bgTint = Color(0xFF854D0E), badgeText = "NEW", badgeColor = Color(0xFFFBBF24))
                    QuickActionCard(modifier = Modifier.weight(1f), title = "Support", icon = Icons.Default.HelpOutline, iconTint = Color(0xFFD1D5DB), bgTint = Color(0xFF374151))
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color,
    bgTint: Color,
    badgeText: String? = null,
    badgeColor: Color = Color.Transparent,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(1.dp, borderDark, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF151821), bgTint.copy(alpha=0.15f))))
            .clickable { onClick() }
    ) {
        // Watermark icon
        Icon(
            icon, 
            contentDescription = null, 
            tint = bgTint.copy(alpha = 0.2f),
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 10.dp, y = 10.dp)
        )
        
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier.size(28.dp).background(bgTint.copy(alpha=0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(14.dp))
                }
                if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, badgeColor.copy(alpha=0.3f), RoundedCornerShape(12.dp))
                            .background(badgeColor.copy(alpha=0.15f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(badgeText, color = badgeColor, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) {
                Text(subtitle, color = Color(0xFFFDE047), fontSize = 9.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top=2.dp))
            }
        }
    }
}

// Recource Center Section
@Composable
fun ResourceCenterSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("Library Resource Center", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Quickly access features, understand library guidelines, and manage your daily study routines all from your dashboard.",
            color = colorTextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Tabs Custom UI
        Box(
            modifier = Modifier
                .border(1.dp, borderDark, RoundedCornerShape(12.dp))
                .background(Color(0xFF13151D), RoundedCornerShape(12.dp))
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF272A35), RoundedCornerShape(8.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Quick Actions", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Library Guidelines", color = colorTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // List Cards
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ResourceListCard(
                icon = Icons.Default.QrCodeScanner,
                title = "Smart Attendance",
                desc = "Simply scan the QR code at the entrance kiosk. No manual entry needed—your attendance is marked instantly."
            )
            ResourceListCard(
                icon = Icons.Default.CreditCard,
                title = "Fee & Payments",
                desc = "Track your payment history and download receipts. Get automated reminders 5 days before your due date."
            )
            ResourceListCard(
                icon = Icons.Default.Chair,
                title = "Seat Management",
                desc = "View your assigned seat on the digital map. Request seat changes or shift changes directly from your dashboard."
            )
            ResourceListCard(
                icon = Icons.Default.SupportAgent,
                title = "24/7 Support",
                desc = "Facing an issue? Submit a ticket for WiFi, AC, or cleaning. Track the status and get resolved quickly."
            )
            ResourceListCard(
                icon = Icons.Default.DateRange,
                title = "Study Planner",
                desc = "Organize your daily tasks, set priorities, and track your study hours. Stay productive with the Pomodoro timer."
            )
            ResourceListCard(
                icon = Icons.Default.Group,
                title = "Community Connect",
                desc = "Join the Discussion Room to collaborate with peers. Share notes, ask questions, and learn together."
            )
        }
    }
}

@Composable
fun ResourceListCard(icon: ImageVector, title: String, desc: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderDark, RoundedCornerShape(16.dp))
            .background(Color(0xFF13151D), RoundedCornerShape(16.dp))
            .clickable { /* TODO Action */ }
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(Color(0xFF1F222D), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = title, tint = Color(0xFFD1D5DB), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(desc, color = colorTextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
fun FooterSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(color = borderDark, thickness = 1.dp, modifier = Modifier.fillMaxWidth(0.9f))
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("© 2026 Apna Lakshay Library Management System. All rights", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 11.sp)
        Text("reserved.", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 11.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Made with ", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 11.sp)
            Text("♥", color = Color.Red.copy(alpha=0.8f), fontSize = 11.sp)
            Text(" for Students", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Privacy Policy", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 11.sp)
            Text("Terms of Service", color = colorTextSecondary.copy(alpha=0.5f), fontSize = 11.sp)
        }
    }
}

@Composable
fun MarkAttendanceFab() {
    Box(
        modifier = Modifier
            .background(Brush.horizontalGradient(listOf(Color(0xFF6EE7B7), Color(0xFF3B82F6).copy(alpha=0.8f))), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha=0.2f), RoundedCornerShape(16.dp))
            .clickable { /* TODO Mark Attendance */ }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Mark Attendance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
