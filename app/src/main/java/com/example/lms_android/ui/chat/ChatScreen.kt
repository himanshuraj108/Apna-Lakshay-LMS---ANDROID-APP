package com.example.lms_android.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lms_android.ui.home.bgDark
import com.example.lms_android.ui.home.borderDark
import com.example.lms_android.ui.home.colorOrange
import com.example.lms_android.ui.home.colorTextSecondary

enum class ChatTab { PUBLIC, GROUPS, PRIVATE }

@Composable
fun ChatScreen(onNavigateBack: () -> Unit = {}) {
    var selectedTab by remember { mutableStateOf(ChatTab.PUBLIC) }
    var showPublicChat by remember { mutableStateOf(false) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }

    if (showCreateGroupDialog) {
        CreateGroupDialog(
            onDismiss = { showCreateGroupDialog = false },
            onCreate = { name, desc -> showCreateGroupDialog = false }
        )
    }

    Scaffold(
        containerColor = bgDark,
        topBar = {
            ChatTopBar(onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            ChatTabRow(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                    showPublicChat = false
                }
            )

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    ChatTab.PUBLIC -> {
                        if (showPublicChat) {
                            PublicChatWindow(onBack = { showPublicChat = false })
                        } else {
                            PublicChatRoomCard(onClick = { showPublicChat = true })
                        }
                    }
                    ChatTab.GROUPS -> {
                        GroupsTab(onCreateGroup = { showCreateGroupDialog = true })
                    }
                    ChatTab.PRIVATE -> {
                        PrivateTab()
                    }
                }
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Brush.linearGradient(listOf(colorOrange, Color(0xFFD64F35))),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ChatBubble, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Chat", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = bgDark)
    )
}

// ─── Tab Row ──────────────────────────────────────────────────────────────────

@Composable
fun ChatTabRow(selectedTab: ChatTab, onTabSelected: (ChatTab) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.dp, borderDark, RoundedCornerShape(50.dp))
            .background(Color(0xFF13151D), RoundedCornerShape(50.dp))
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            ChatTabItem(
                modifier = Modifier.weight(1f),
                label = "Public",
                icon = Icons.Default.People,
                selected = selectedTab == ChatTab.PUBLIC,
                onClick = { onTabSelected(ChatTab.PUBLIC) }
            )
            ChatTabItem(
                modifier = Modifier.weight(1f),
                label = "Groups",
                icon = Icons.Default.Group,
                selected = selectedTab == ChatTab.GROUPS,
                onClick = { onTabSelected(ChatTab.GROUPS) }
            )
            ChatTabItem(
                modifier = Modifier.weight(1f),
                label = "Private",
                icon = Icons.Default.Person,
                selected = selectedTab == ChatTab.PRIVATE,
                onClick = { onTabSelected(ChatTab.PRIVATE) }
            )
        }
    }
}

@Composable
fun ChatTabItem(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(
                if (selected) Brush.horizontalGradient(listOf(colorOrange, Color(0xFFD64F35)))
                else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (selected) Color.White else colorTextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                label,
                color = if (selected) Color.White else colorTextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
            )
        }
    }
}

// ─── Public Tab ───────────────────────────────────────────────────────────────

@Composable
fun PublicChatRoomCard(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderDark, RoundedCornerShape(16.dp))
                .background(Color(0xFF13151D), RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.linearGradient(listOf(colorOrange, Color(0xFFD64F35))),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.People, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Public Study Chat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("All students online", color = colorTextSecondary, fontSize = 12.sp)
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = colorTextSecondary)
            }
        }
    }
}

@Composable
fun PublicChatWindow(onBack: () -> Unit) {
    var messageText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, borderDark, RoundedCornerShape(16.dp))
                .background(Color(0xFF13151D), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            Brush.linearGradient(listOf(colorOrange, Color(0xFFD64F35))),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.People, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Public Study Chat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("All students online", color = colorTextSecondary, fontSize = 11.sp)
                }
            }
        }

        // Messages Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(1.dp, borderDark, RoundedCornerShape(16.dp))
                .background(Color(0xFF0F1117), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No messages yet. Start the conversation!",
                    color = colorTextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(1.dp, borderDark, RoundedCornerShape(28.dp))
                .background(Color(0xFF13151D), RoundedCornerShape(28.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Message...", color = colorTextSecondary, fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = colorOrange
                ),
                singleLine = true
            )
            Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = colorTextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        Brush.linearGradient(listOf(colorOrange, Color(0xFFD64F35))),
                        CircleShape
                    )
                    .clickable { /* TODO Send */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ─── Groups Tab ───────────────────────────────────────────────────────────────

@Composable
fun GroupsTab(onCreateGroup: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Create New Group button (top right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 8.dp)
                .background(
                    Brush.horizontalGradient(listOf(colorOrange, Color(0xFFD64F35))),
                    RoundedCornerShape(50.dp)
                )
                .clickable { onCreateGroup() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create New Group", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // Empty state
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 16.dp, end = 16.dp, top = 56.dp, bottom = 16.dp)
                .border(1.dp, borderDark, RoundedCornerShape(16.dp))
                .background(Color(0xFF13151D), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Group,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.4f),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "You haven't joined any groups yet",
                    color = colorTextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(listOf(colorOrange, Color(0xFFD64F35))),
                            RoundedCornerShape(50.dp)
                        )
                        .clickable { onCreateGroup() }
                        .padding(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Your First Group", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// ─── Private Tab ──────────────────────────────────────────────────────────────

@Composable
fun PrivateTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Start New Chat card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF7C3AED))
                    )
                )
                .clickable { /* TODO */ }
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("START NEW CHAT", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Direct Message a Student", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Empty state
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("No private chats yet", color = colorTextSecondary, fontSize = 14.sp)
            }
        }
    }
}

// ─── Create Group Dialog ──────────────────────────────────────────────────────

@Composable
fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, desc: String) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var groupDesc by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .border(1.dp, colorOrange.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .background(Color(0xFF13151D), RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column {
                // Title row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Brush.linearGradient(listOf(colorOrange, Color(0xFFD64F35))),
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ChatBubble, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Create Study Group", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text("Create a group for collaboration", color = colorTextSecondary, fontSize = 12.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, borderDark, RoundedCornerShape(8.dp))
                            .background(Color(0xFF1F222D), RoundedCornerShape(8.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = colorTextSecondary, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Group Name
                Text("GROUP NAME", color = colorTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    placeholder = { Text("e.g., Math Study Group", color = colorTextSecondary, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorOrange,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = colorOrange
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text("DESCRIPTION", color = colorTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                Text(" (optional)", color = colorTextSecondary.copy(alpha = 0.6f), fontSize = 11.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = groupDesc,
                    onValueChange = { groupDesc = it },
                    placeholder = { Text("What's this group about?", color = colorTextSecondary, fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorOrange,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = colorOrange
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Cancel
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, borderDark, RoundedCornerShape(12.dp))
                            .background(Color(0xFF1F222D), RoundedCornerShape(12.dp))
                            .clickable { onDismiss() }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cancel", color = colorTextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    // Create
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                Brush.horizontalGradient(listOf(colorOrange, Color(0xFFD64F35))),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { if (groupName.isNotBlank()) onCreate(groupName, groupDesc) }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Create Group", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
