package com.example.lms_android.ui.attendance

import android.Manifest
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.MarkAttendanceRequest
import com.google.accompanist.permissions.*
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// ─────────────────────────────────────────────────────────────────────────────
//  ViewModel
// ─────────────────────────────────────────────────────────────────────────────
sealed class QrScanState {
    object Idle : QrScanState()
    object Scanning : QrScanState()
    object Processing : QrScanState()
    data class Success(val message: String, val type: String) : QrScanState()
    data class Error(val message: String) : QrScanState()
}

class QrScanViewModel : ViewModel() {
    private val _state = MutableStateFlow<QrScanState>(QrScanState.Idle)
    val state: StateFlow<QrScanState> = _state.asStateFlow()

    private var lastScannedToken: String? = null
    private var processingLock = false

    fun startScanning() {
        _state.value = QrScanState.Scanning
        lastScannedToken = null
        processingLock = false
    }

    fun onQrCodeDetected(rawValue: String) {
        if (processingLock) return
        if (rawValue == lastScannedToken) return
        lastScannedToken = rawValue
        processingLock = true

        viewModelScope.launch {
            _state.value = QrScanState.Processing
            try {
                val response = ApiClient.apiService.markAttendanceByQr(
                    MarkAttendanceRequest(qrToken = rawValue)
                )
                if (response.success) {
                    _state.value = QrScanState.Success(
                        message = response.message ?: "Attendance marked!",
                        type = response.type ?: "entry"
                    )
                } else {
                    _state.value = QrScanState.Error(response.message ?: "Failed to mark attendance")
                }
            } catch (e: retrofit2.HttpException) {
                val body = e.response()?.errorBody()?.string()
                val msg = body?.substringAfter("\"message\":\"")?.substringBefore("\"")
                    ?: "Server error: ${e.code()}"
                _state.value = QrScanState.Error(msg)
            } catch (e: Exception) {
                _state.value = QrScanState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun retry() {
        processingLock = false
        lastScannedToken = null
        _state.value = QrScanState.Scanning
    }

    fun dismiss() {
        _state.value = QrScanState.Idle
        processingLock = false
        lastScannedToken = null
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  QR Scanner Bottom Sheet / Overlay Screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrScannerScreen(
    onDismiss: () -> Unit,
    viewModel: QrScanViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.collectAsState()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    // Kick off scanning when we open
    LaunchedEffect(Unit) {
        viewModel.startScanning()
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    // Full-screen dark overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070A12))
    ) {
        when (val s = state) {
            is QrScanState.Scanning, is QrScanState.Processing -> {
                if (cameraPermission.status.isGranted) {
                    CameraPreviewWithScannerOverlay(
                        isProcessing = state is QrScanState.Processing,
                        onQrDetected = { viewModel.onQrCodeDetected(it) }
                    )
                } else {
                    PermissionDeniedContent(
                        onRequest = { cameraPermission.launchPermissionRequest() }
                    )
                }
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close",
                            tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Scan QR Code", color = Color.White,
                            fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("Point camera at attendance QR",
                            color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                }
            }
            is QrScanState.Success -> {
                ScanResultContent(
                    isSuccess = true,
                    message = s.message,
                    type = s.type,
                    onDismiss = onDismiss,
                    onRetry = null
                )
            }
            is QrScanState.Error -> {
                ScanResultContent(
                    isSuccess = false,
                    message = s.message,
                    type = "error",
                    onDismiss = onDismiss,
                    onRetry = { viewModel.retry() }
                )
            }
            else -> {}
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Camera Preview with Scan Overlay
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CameraPreviewWithScannerOverlay(
    isProcessing: Boolean,
    onQrDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    val previewView = remember { PreviewView(context) }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { imgAnalysis ->
                    imgAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(
                                mediaImage, imageProxy.imageInfo.rotationDegrees
                            )
                            barcodeScanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    barcodes.firstOrNull { it.valueType == Barcode.TYPE_TEXT || it.rawValue != null }
                                        ?.rawValue?.let { onQrDetected(it) }
                                }
                                .addOnCompleteListener { imageProxy.close() }
                        } else {
                            imageProxy.close()
                        }
                    }
                }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analyzer
                )
            } catch (e: Exception) {
                Log.e("QrScanner", "Camera bind failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraExecutor.shutdown()
            barcodeScanner.close()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Dark overlay with cut-out illusion
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        // Scanner frame
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ScannerFrame(isProcessing = isProcessing)
                Spacer(Modifier.height(24.dp))
                Text(
                    if (isProcessing) "Verifying..." else "Align QR code inside the frame",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ScannerFrame(size: Dp = 260.dp, cornerLen: Dp = 28.dp, isProcessing: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "scanLineY"
    )

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Corners using Canvas
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val cornerColor = if (isProcessing)
                androidx.compose.ui.graphics.Color(0xFF7C3AED)
            else
                androidx.compose.ui.graphics.Color.White
            val strokeW = 4.dp.toPx()
            val cLen = cornerLen.toPx()

            // Top-left
            drawLine(cornerColor, androidx.compose.ui.geometry.Offset(0f, cLen),
                androidx.compose.ui.geometry.Offset(0f, 0f), strokeW)
            drawLine(cornerColor, androidx.compose.ui.geometry.Offset(0f, 0f),
                androidx.compose.ui.geometry.Offset(cLen, 0f), strokeW)
            // Top-right
            drawLine(cornerColor, androidx.compose.ui.geometry.Offset(this.size.width - cLen, 0f),
                androidx.compose.ui.geometry.Offset(this.size.width, 0f), strokeW)
            drawLine(cornerColor, androidx.compose.ui.geometry.Offset(this.size.width, 0f),
                androidx.compose.ui.geometry.Offset(this.size.width, cLen), strokeW)
            // Bottom-left
            drawLine(cornerColor, androidx.compose.ui.geometry.Offset(0f, this.size.height - cLen),
                androidx.compose.ui.geometry.Offset(0f, this.size.height), strokeW)
            drawLine(cornerColor, androidx.compose.ui.geometry.Offset(0f, this.size.height),
                androidx.compose.ui.geometry.Offset(cLen, this.size.height), strokeW)
            // Bottom-right
            drawLine(cornerColor,
                androidx.compose.ui.geometry.Offset(this.size.width, this.size.height - cLen),
                androidx.compose.ui.geometry.Offset(this.size.width, this.size.height), strokeW)
            drawLine(cornerColor,
                androidx.compose.ui.geometry.Offset(this.size.width - cLen, this.size.height),
                androidx.compose.ui.geometry.Offset(this.size.width, this.size.height), strokeW)

            // Animated scan line
            if (!isProcessing) {
                val y = this.size.height * scanLineY
                val scanAlpha = 0.8f
                drawLine(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(
                            androidx.compose.ui.graphics.Color.Transparent,
                            androidx.compose.ui.graphics.Color(0xFF7C3AED).copy(alpha = scanAlpha),
                            androidx.compose.ui.graphics.Color.Transparent
                        )
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, y),
                    end = androidx.compose.ui.geometry.Offset(this.size.width, y),
                    strokeWidth = 3.dp.toPx()
                )
            }
        }

        // Processing spinner
        if (isProcessing) {
            CircularProgressIndicator(
                color = Color(0xFF7C3AED),
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Result Screen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ScanResultContent(
    isSuccess: Boolean,
    message: String,
    type: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)?
) {
    val isEntry = type == "entry"
    val isAlreadyMarked = type == "already_marked"

    val iconColor = when {
        !isSuccess      -> Color(0xFFF87171)
        isAlreadyMarked -> Color(0xFFFBBF24)
        isEntry         -> Color(0xFF10B981)
        else            -> Color(0xFF60A5FA)
    }
    val iconBg = when {
        !isSuccess      -> Color(0xFF7F1D1D).copy(alpha = 0.3f)
        isAlreadyMarked -> Color(0xFF78350F).copy(alpha = 0.3f)
        isEntry         -> Color(0xFF064E3B).copy(alpha = 0.3f)
        else            -> Color(0xFF1E3A8A).copy(alpha = 0.3f)
    }
    val icon = when {
        !isSuccess      -> Icons.Default.ErrorOutline
        isAlreadyMarked -> Icons.Default.Info
        isEntry         -> Icons.Default.Login
        else            -> Icons.Default.Logout
    }
    val title = when {
        !isSuccess      -> "Scan Failed"
        isAlreadyMarked -> "Already Marked"
        isEntry         -> "Check-in Successful"
        else            -> "Check-out Successful"
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF13151D))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(iconBg, CircleShape)
                    .border(1.dp, iconColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor,
                    modifier = Modifier.size(40.dp))
            }

            Spacer(Modifier.height(24.dp))

            Text(title, color = Color.White, fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)

            Spacer(Modifier.height(12.dp))

            Text(message, color = Color(0xFF9CA3AF), fontSize = 14.sp,
                textAlign = TextAlign.Center, lineHeight = 22.sp)

            Spacer(Modifier.height(32.dp))

            // Action buttons
            if (onRetry != null) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Scan Again", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(Modifier.height(12.dp))
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Text("Close", color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun PermissionDeniedContent(onRequest: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null,
                tint = Color(0xFF9CA3AF), modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("Camera Permission Required", color = Color.White,
                fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("Please allow camera access to scan the attendance QR code.",
                color = Color(0xFF9CA3AF), fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Grant Permission", fontWeight = FontWeight.Bold)
            }
        }
    }
}
