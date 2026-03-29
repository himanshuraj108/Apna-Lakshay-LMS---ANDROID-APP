package com.example.lms_android.ui.fee

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lms_android.data.models.FeeRecord
import com.example.lms_android.data.models.UserProfile
import java.text.SimpleDateFormat
import java.util.*

// ── Palette (reuse from HomeScreen) ──────────────────────────────────────────
private val FeeBg        = Color(0xFF0D0F16)
private val FeeCard      = Color(0xFF13151D)
private val FeeBorder    = Color.White.copy(alpha = 0.08f)
private val FeeSecondary = Color(0xFF9CA3AF)
private val FeeOrange    = Color(0xFFE87A5D)
private val FeeGreen     = Color(0xFF10B981)
private val FeeYellow    = Color(0xFFFBBF24)
private val FeePurple    = Color(0xFF7C3AED)
private val FeeBlue      = Color(0xFF6366F1)

// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun FeeScreen(
    onNavigateBack: () -> Unit,
    viewModel: FeeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedFee by remember { mutableStateOf<FeeRecord?>(null) }
    var selectedProfile by remember { mutableStateOf<UserProfile?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(FeeBg)) {
        when (state) {
            is FeeState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = FeeOrange
                )
            }

            is FeeState.Error -> {
                val msg = (state as FeeState.Error).message
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = FeeOrange, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(msg, color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.fetchFees() },
                        colors = ButtonDefaults.buttonColors(containerColor = FeeOrange, contentColor = Color.White)
                    ) { Text("Retry", fontWeight = FontWeight.Bold) }
                }
            }

            is FeeState.Success -> {
                val s = state as FeeState.Success
                FeeContent(
                    fees    = s.fees,
                    profile = s.profile,
                    onBack  = onNavigateBack,
                    onViewReceipt = { fee ->
                        selectedFee     = fee
                        selectedProfile = s.profile
                    }
                )
            }
        }

        // Receipt overlay
        if (selectedFee != null && selectedProfile != null) {
            ReceiptDialog(
                fee     = selectedFee!!,
                profile = selectedProfile!!,
                onDismiss = { selectedFee = null }
            )
        }
    }
}

// ─── Main content ─────────────────────────────────────────────────────────────
@Composable
fun FeeContent(
    fees: List<FeeRecord>,
    profile: UserProfile,
    onBack: () -> Unit,
    onViewReceipt: (FeeRecord) -> Unit
) {
    val totalPaid    = fees.filter { it.status.lowercase() == "paid" }.sumOf { it.amount }
    val totalPending = fees.filter { it.status.lowercase() != "paid" }.sumOf { it.amount }
    val paidMonths   = fees.count { it.status.lowercase() == "paid" }
    val pendingMonths= fees.count { it.status.lowercase() != "paid" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(52.dp))

        // ── Top bar ──────────────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, FeeBorder, RoundedCornerShape(12.dp))
                    .background(FeeCard, RoundedCornerShape(12.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Fee Status", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text("Payment history & dues", color = FeeSecondary, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Summary cards row ─────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            SummaryCard(
                modifier   = Modifier.weight(1f),
                label      = "TOTAL PAID",
                value      = "₹$totalPaid",
                sub        = "$paidMonths months",
                icon       = Icons.Default.CheckCircleOutline,
                iconTint   = FeeGreen,
                iconBg     = Color(0xFF064E3B).copy(alpha = 0.35f),
                topLine    = FeeGreen
            )
            SummaryCard(
                modifier   = Modifier.weight(1f),
                label      = "PENDING",
                value      = "₹$totalPending",
                sub        = "$pendingMonths months",
                icon       = Icons.Default.Schedule,
                iconTint   = FeeYellow,
                iconBg     = Color(0xFF78350F).copy(alpha = 0.35f),
                topLine    = FeeYellow
            )
            SummaryCard(
                modifier   = Modifier.weight(1f),
                label      = "TOTAL RECORDS",
                value      = "${fees.size}",
                sub        = "all months",
                icon       = Icons.Default.Receipt,
                iconTint   = FeeBlue,
                iconBg     = Color(0xFF1E3A8A).copy(alpha = 0.35f),
                topLine    = FeeBlue
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Payment History ───────────────────────────────────────────────────
        SectionCard(title = "Payment History", icon = Icons.Default.Receipt) {
            if (fees.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No payment records found.", color = FeeSecondary, fontSize = 14.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    fees.forEach { fee ->
                        PaymentRow(fee = fee, onViewReceipt = { onViewReceipt(fee) })
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Payment Instructions ──────────────────────────────────────────────
        SectionCard(title = "Payment Instructions", icon = Icons.Default.Info) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InstructionItem(1, "Visit the library office during working hours (9:00 AM – 6:00 PM)")
                InstructionItem(2, "Make cash payment to the admin")
                InstructionItem(3, "Admin will mark your payment in the system")
                InstructionItem(4, "You'll receive a confirmation notification & email with your receipt")

                Spacer(Modifier.height(4.dp))

                // Important notice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, FeeYellow.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .background(FeeYellow.copy(alpha = 0.07f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = FeeYellow, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Important: Pay before the due date to avoid late fees or membership suspension.",
                            color = FeeYellow,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

// ─── Summary card ─────────────────────────────────────────────────────────────
@Composable
private fun SummaryCard(
    modifier:  Modifier,
    label:     String,
    value:     String,
    sub:       String,
    icon:      androidx.compose.ui.graphics.vector.ImageVector,
    iconTint:  Color,
    iconBg:    Color,
    topLine:   Color
) {
    Box(
        modifier = modifier
            .border(1.dp, FeeBorder, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(FeeCard)
            .drawBehind {
                drawLine(
                    color       = topLine,
                    start       = Offset(24f, 0f),
                    end         = Offset(size.width - 24f, 0f),
                    strokeWidth = 3f
                )
            }
            .padding(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(iconBg, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(label, color = FeeSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, lineHeight = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(sub,   color = FeeSecondary, fontSize = 10.sp, lineHeight = 14.sp)
        }
    }
}

// ─── Section wrapper ──────────────────────────────────────────────────────────
@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FeeBorder, RoundedCornerShape(20.dp))
            .background(FeeCard, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(FeePurple.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(20.dp))
            content()
        }
    }
}

// ─── Payment row ──────────────────────────────────────────────────────────────
@Composable
private fun PaymentRow(fee: FeeRecord, onViewReceipt: () -> Unit) {
    val isPaid    = fee.status.lowercase() == "paid"
    val monthName = monthAbbr(fee.month)
    val dayOfPaid = fee.paidDate?.let { parseDay(it) } ?: "--"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FeeBorder, RoundedCornerShape(14.dp))
            .background(Color(0xFF191C26), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date badge
        Box(
            modifier = Modifier
                .size(width = 48.dp, height = 52.dp)
                .background(Color(0xFF1F222D), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(monthName, color = FeeSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(dayOfPaid, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 20.sp)
            }
        }

        Spacer(Modifier.width(12.dp))

        // Amount
        Text("₹${fee.amount}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)

        Spacer(Modifier.width(14.dp))

        // Due / Paid info
        Column(modifier = Modifier.weight(1f)) {
            fee.dueDate?.let { due ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = FeeSecondary, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Due ", color = FeeSecondary, fontSize = 11.sp)
                    Text(formatDate(due), color = FeeSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            if (isPaid) {
                fee.paidDate?.let { pd ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(7.dp).background(FeeGreen, androidx.compose.foundation.shape.CircleShape))
                        Spacer(Modifier.width(5.dp))
                        Text("Paid ", color = FeeGreen, fontSize = 11.sp)
                        Text(formatDate(pd), color = FeeGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).background(Color(0xFFF87171), androidx.compose.foundation.shape.CircleShape))
                    Spacer(Modifier.width(5.dp))
                    Text(fee.status.uppercase(), color = Color(0xFFF87171), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Receipt button (only for paid)
        if (isPaid) {
            Box(
                modifier = Modifier
                    .border(1.dp, FeeYellow.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF78350F).copy(alpha = 0.5f), FeeYellow.copy(alpha = 0.12f))),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onViewReceipt() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Receipt, contentDescription = "Receipt", tint = FeeYellow, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Receipt", color = FeeYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── Instruction item ─────────────────────────────────────────────────────────
@Composable
private fun InstructionItem(num: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FeeBorder, RoundedCornerShape(12.dp))
            .background(Color(0xFF191C26), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(FeePurple, androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$num", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Text(text, color = Color.White, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.weight(1f))
    }
}

// ─── Receipt Dialog ───────────────────────────────────────────────────────────
@Composable
fun ReceiptDialog(
    fee: FeeRecord,
    profile: UserProfile,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable(indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.93f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1A1C26))
                    .clickable(indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }) {} // Consume click so dialog doesn't close
                    .padding(bottom = 16.dp)
            ) {
                Column {
                    // Dialog header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color(0xFF7F1D1D).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFF87171).copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Payment Receipt", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Apna Lakshya Library", color = FeeSecondary, fontSize = 12.sp)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = FeeSecondary)
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Download PDF
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFFF87171).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .background(Color(0xFF7F1D1D).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable { /* TODO PDF download */ }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Download PDF", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        // Download Image
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, FeeBlue.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .background(FeeBlue.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                .clickable { /* TODO Image download */ }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Download Image", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Print button
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .border(1.dp, FeeBorder, RoundedCornerShape(10.dp))
                            .background(Color(0xFF272A35), RoundedCornerShape(10.dp))
                            .clickable { /* TODO Print */ }
                            .padding(horizontal = 28.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Print, contentDescription = null, tint = FeeSecondary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Print", color = FeeSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // ── Actual receipt ─────────────────────────────────────────
                    ReceiptBody(fee = fee, profile = profile)
                }
            }
        }
    }
}

// ─── Receipt body (physical-style) ────────────────────────────────────────────
@Composable
private fun ReceiptBody(fee: FeeRecord, profile: UserProfile) {
    val receiptBg       = Color(0xFFFFFDF8)
    val receiptRed      = Color(0xFFCC0000)
    val receiptDivider  = receiptRed.copy(alpha = 0.35f)
    val receiptText     = Color(0xFF1A1A1A)
    val receiptSubText  = Color(0xFF555555)
    val paidDate        = fee.paidDate?.let { formatDateShort(it) } ?: "--"
    val slno            = 1 // Placeholder; API could provide index

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .border(2.dp, receiptRed.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(receiptBg)
    ) {
        Column {
            // Header block
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Director.: Mahesh Ray", color = receiptSubText, fontSize = 8.sp)
                    Text("Managing by : D.K.  Mob.:9798908881, 8205772574", color = receiptSubText, fontSize = 8.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "APNA LAKSHYA LIBRARY",
                    color = receiptRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Self Study Point",
                    color = receiptRed,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }

            HorizontalDivider(color = receiptRed.copy(alpha = 0.5f), thickness = 1.dp)

            // Body
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Sl No / PAID stamp / Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sl. No. ", color = receiptText, fontSize = 11.sp)
                        Text("$slno", color = receiptText, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    // PAID stamp
                    Box(
                        modifier = Modifier
                            .border(2.dp, FeeGreen, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("PAID", color = FeeGreen, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
                    }

                    Text("Date $paidDate", color = receiptText, fontSize = 11.sp)
                }

                Spacer(Modifier.height(6.dp))
                DottedDivider(receiptDivider)
                Spacer(Modifier.height(4.dp))

                ReceiptField("Name", profile.name?.uppercase() ?: "--", receiptText, receiptSubText, isBold = true)
                ReceiptField("Father's Name", profile.fatherName ?: "", receiptText, receiptSubText)
                Row(modifier = Modifier.fillMaxWidth()) {
                    ReceiptFieldInline(modifier = Modifier.weight(1f), label = "DOB", value = profile.dob ?: "--", text = receiptText, sub = receiptSubText)
                    ReceiptFieldInline(modifier = Modifier.weight(1f), label = "Seat No.", value = profile.seatNumber ?: "OFFICE", text = receiptText, sub = receiptSubText)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    ReceiptFieldInline(modifier = Modifier.weight(1f), label = "Mobile No.", value = profile.mobile ?: "--", text = receiptText, sub = receiptSubText)
                    ReceiptFieldInline(modifier = Modifier.weight(1f), label = "Shift", value = (profile.shift ?: "FULL SHIFT").uppercase(), text = receiptText, sub = receiptSubText, valueBold = true)
                }
                ReceiptField("Adhar No.", profile.aadharNo ?: "", receiptText, receiptSubText)
                ReceiptField("Address", profile.address ?: "--", receiptText, receiptSubText)

                Spacer(Modifier.height(8.dp))

                // Registration / Locker row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, receiptDivider, RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Registration Fee", color = receiptSubText, fontSize = 9.sp)
                            Spacer(Modifier.height(14.dp))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, receiptDivider, RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Locker No.", color = receiptSubText, fontSize = 9.sp)
                            Spacer(Modifier.height(14.dp))
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Fee totals row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FeeCell(modifier = Modifier.weight(1f), label = "MONTHLY FEE", value = "₹${fee.amount}", bg = receiptBg, border = receiptDivider, textColor = receiptText)
                    FeeCell(modifier = Modifier.weight(0.6f), label = "DUE", value = if ((fee.due ?: 0) > 0) "₹${fee.due}" else "—", bg = receiptBg, border = receiptDivider, textColor = receiptText)
                    FeeCell(modifier = Modifier.weight(0.8f), label = "TOTAL", value = "₹${fee.amount}", bg = Color(0xFFFFE4E4), border = receiptRed.copy(alpha = 0.4f), textColor = receiptRed)
                }

                Spacer(Modifier.height(12.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("Director:", color = receiptSubText, fontSize = 9.sp)
                        Text("Mahesh Ray", color = receiptText, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    // Stamp box
                    Box(
                        modifier = Modifier
                            .background(receiptRed, RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Managing by : D.K.", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("Mob. :9798908881", color = Color.White, fontSize = 9.sp)
                            Text("0205772574", color = Color.White, fontSize = 9.sp)
                            Text("Near Nahar, Sitamarhi", color = Color.White, fontSize = 8.sp)
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                // Bottom red bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Brush.horizontalGradient(listOf(receiptRed, Color(0xFFFF6B6B), receiptRed)))
                )
            }
        }
    }
}

// ─── Receipt field helpers ────────────────────────────────────────────────────
@Composable
private fun ReceiptField(label: String, value: String, textColor: Color, subColor: Color, isBold: Boolean = false) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Text("$label  ", color = subColor, fontSize = 9.sp)
            Box(modifier = Modifier.weight(1f).padding(bottom = 1.dp)) {
                Text(value, color = textColor, fontSize = if (isBold) 12.sp else 11.sp, fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Normal, textDecoration = TextDecoration.Underline)
            }
        }
    }
}

@Composable
private fun ReceiptFieldInline(modifier: Modifier, label: String, value: String, text: Color, sub: Color, valueBold: Boolean = false) {
    Row(modifier = modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Bottom) {
        Text("$label  ", color = sub, fontSize = 9.sp)
        Text(value, color = text, fontSize = if (valueBold) 12.sp else 11.sp, fontWeight = if (valueBold) FontWeight.ExtraBold else FontWeight.Normal, textDecoration = TextDecoration.Underline)
    }
}

@Composable
private fun FeeCell(modifier: Modifier, label: String, value: String, bg: Color, border: Color, textColor: Color) {
    Box(
        modifier = modifier
            .border(1.dp, border, RoundedCornerShape(4.dp))
            .background(bg, RoundedCornerShape(4.dp))
            .padding(6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(label, color = textColor.copy(alpha = 0.6f), fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(value, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun DottedDivider(color: Color) {
    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        val dotWidth  = 4.dp.toPx()
        val dotGap    = 4.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawRect(color = color, topLeft = Offset(x, 0f), size = androidx.compose.ui.geometry.Size(dotWidth, size.height))
            x += dotWidth + dotGap
        }
    }
}

// ─── Date helpers ─────────────────────────────────────────────────────────────
private fun monthAbbr(month: Int): String {
    return try {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, month - 1)
        SimpleDateFormat("MMM", Locale.ENGLISH).format(cal.time).uppercase()
    } catch (_: Exception) { "---" }
}

private fun parseDay(iso: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(iso) ?: return "--"
        SimpleDateFormat("dd", Locale.ENGLISH).format(date)
    } catch (_: Exception) {
        try {
            val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date = sdf2.parse(iso) ?: return "--"
            SimpleDateFormat("dd", Locale.ENGLISH).format(date)
        } catch (_: Exception) { "--" }
    }
}

private fun formatDate(iso: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(iso) ?: return iso
        SimpleDateFormat("d/M/yyyy", Locale.ENGLISH).format(date)
    } catch (_: Exception) {
        try {
            val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date = sdf2.parse(iso) ?: return iso
            SimpleDateFormat("d/M/yyyy", Locale.ENGLISH).format(date)
        } catch (_: Exception) { iso }
    }
}

private fun formatDateShort(iso: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(iso) ?: return iso
        SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).format(date)
    } catch (_: Exception) {
        try {
            val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date = sdf2.parse(iso) ?: return iso
            SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).format(date)
        } catch (_: Exception) { iso }
    }
}
