package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MovementFeatures
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinicalDisclaimerBanner
import com.example.ui.components.ClinicalHeader
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun MovementAssessmentScreen(
    viewModel: OrthoScreenViewModel
) {
    val scrollState = rememberScrollState()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val unsyncedCount by viewModel.unsyncedCount.collectAsStateWithLifecycle()

    var isRecording by remember { mutableStateOf(true) }
    var recordingTimer by remember { mutableStateOf(16.4f) }
    var completedReps by remember { mutableStateOf(5) }
    var currentAngle by remember { mutableStateOf(86.5f) }
    var showBypassDialog by remember { mutableStateOf(false) }
    var isSimulatingInconclusive by remember { mutableStateOf(false) }

    // Skeletal bounce animation for realistic AI pose demo
    val infiniteTransition = rememberInfiniteTransition(label = "PoseTrack")
    val jointOscillation by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "JointOscillation"
    )

    Scaffold(
        topBar = {
            ClinicalHeader(
                title = "New Screening",
                subtitle = "Step 3 of 4 • 5xSTS Movement Assessment",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.Questionnaire) },
                isOnline = isOnline,
                unsyncedCount = unsyncedCount,
                onToggleNetwork = { viewModel.toggleNetworkMode() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Step Progress Card (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STEP 3 OF 4: FUNCTIONAL MOVEMENT ASSESSMENT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF0061A4)
                        )
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = "75% Completed",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { 0.75f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = Color(0xFF0061A4),
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1. Intake", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = Color(0xFF0061A4))
                        Text("2. Symptoms", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = Color(0xFF0061A4))
                        Text("3. Motion & AI", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0061A4), fontWeight = FontWeight.Bold)
                        Text("4. Referral", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                    }
                }
            }

            // Clinical Advisory Banner
            ClinicalDisclaimerBanner(title = "Biomechanical Advisory:")

            // Camera Viewfinder Card with Simulated AI Skeletal Mesh (Geometric Balance: rounded-3xl)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, Color(0xFF0061A4).copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Simulated skeletal pose tracking canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val cx = w * 0.5f
                        val cy = h * 0.48f + jointOscillation

                        // Draw Grid Crosshairs
                        drawLine(
                            color = Color(0xFF38BDF8).copy(alpha = 0.35f),
                            start = Offset(cx, 20f),
                            end = Offset(cx, h - 20f),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                        )

                        // Anatomical skeletal keypoints (Shoulder -> Hip -> Knee -> Ankle)
                        val head = Offset(cx, cy - 100f)
                        val shoulderL = Offset(cx - 36f, cy - 65f)
                        val shoulderR = Offset(cx + 36f, cy - 65f)
                        val hipL = Offset(cx - 28f, cy)
                        val hipR = Offset(cx + 28f, cy)
                        val kneeL = Offset(cx - 34f, cy + 60f)
                        val kneeR = Offset(cx + 34f, cy + 60f)
                        val ankleL = Offset(cx - 30f, cy + 120f)
                        val ankleR = Offset(cx + 30f, cy + 120f)

                        // Draw Limbs
                        val limbColor = Color(0xFF7DD3FC)
                        val limbWidth = 4f

                        // Torso
                        drawLine(limbColor, shoulderL, shoulderR, strokeWidth = limbWidth)
                        drawLine(limbColor, shoulderL, hipL, strokeWidth = limbWidth)
                        drawLine(limbColor, shoulderR, hipR, strokeWidth = limbWidth)
                        drawLine(limbColor, hipL, hipR, strokeWidth = limbWidth)

                        // Left Leg (Affected)
                        drawLine(Color(0xFFF87171), hipL, kneeL, strokeWidth = 5f)
                        drawLine(Color(0xFFF87171), kneeL, ankleL, strokeWidth = 5f)

                        // Right Leg
                        drawLine(limbColor, hipR, kneeR, strokeWidth = limbWidth)
                        drawLine(limbColor, kneeR, ankleR, strokeWidth = limbWidth)

                        // Keypoint Circles
                        val jointRadius = 7f
                        drawCircle(Color.White, radius = 14f, center = head)
                        drawCircle(Color(0xFF0061A4), radius = 10f, center = head)

                        listOf(shoulderL, shoulderR, hipL, hipR, kneeR, ankleL, ankleR).forEach { pt ->
                            drawCircle(Color(0xFF38BDF8), radius = jointRadius, center = pt)
                            drawCircle(Color(0xFF0061A4), radius = jointRadius - 3f, center = pt)
                        }

                        // Highlight Left Knee Joint with Angle Ring
                        drawCircle(Color(0xFFEF4444), radius = 12f, center = kneeL, style = Stroke(width = 3f))
                        drawCircle(Color.White, radius = 6f, center = kneeL)
                    }

                    // Top Viewfinder Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.Black.copy(alpha = 0.65f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Text(
                                    text = "POSE TRACKED (Confidence: 94%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.Black.copy(alpha = 0.65f)
                        ) {
                            Text(
                                text = "5xSTS PROTOCOL",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7DD3FC)
                            )
                        }
                    }

                    // Floating Metrics HUD
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Reps badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Reps: $completedReps / 5",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Knee Angle Tag
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            border = BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.RotateRight, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Left Knee Angle: ${String.format("%.1f", currentAngle)}° Flexion",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Timer overlay on bottom right
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(14.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text(
                                text = "${String.format("%.1f", recordingTimer)}s",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Environmental Quality Telemetry Bar (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EnvCheckItem(label = "Distance", value = "2.4m", isOk = true)
                    EnvCheckItem(label = "Lighting", value = "240 Lux", isOk = true)
                    EnvCheckItem(label = "Tilt Angle", value = "0.8°", isOk = true)
                    EnvCheckItem(label = "Full Body", value = "100%", isOk = true)
                }
            }

            // Real-time Cadence & Kinematics Telemetry Card (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXTRACTED BIOMECHANICAL FEATURES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF64748B)
                        )
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = "ON-DEVICE AI",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8)
                            )
                        }
                    }

                    // Feature 1: 5xSTS Completion Cadence
                    FeatureRow(
                        title = "5xSTS Completion Time",
                        metric = "16.4 seconds",
                        reference = "Prolonged (Clinical Reference: < 12.0s)",
                        isFlagged = true
                    )

                    // Feature 2: Limb Asymmetry
                    FeatureRow(
                        title = "Left/Right Symmetry Ratio",
                        metric = "0.71 (Significant Asymmetry)",
                        reference = "Protective offloading to right limb detected",
                        isFlagged = true
                    )

                    // Feature 3: Range of Motion (ROM)
                    FeatureRow(
                        title = "Dynamic Knee Flexion/ROM",
                        metric = "108.0° Arc (Max 174° - Min 86.5°)",
                        reference = "Mild extension deceleration & flexion deficit",
                        isFlagged = false
                    )
                }
            }

            // Patient Safety Bypass & Test Skip Option (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFFFF1F2),
                border = BorderStroke(1.dp, Color(0xFFFECDD3))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Patient unable to stand safely unaided?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9F1239)
                        )
                        Text(
                            text = "Bypass 5xSTS to protect patient from acute joint pain or fall risk.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFBE123C)
                        )
                    }

                    OutlinedButton(
                        onClick = { showBypassDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE11D48)),
                        border = BorderStroke(1.dp, Color(0xFFE11D48)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("btn_bypass_5xsts")
                    ) {
                        Text("Bypass Test", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .clickable { viewModel.navigateTo(Screen.Questionnaire) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Symptoms", color = Color(0xFF334155), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Button(
                    onClick = {
                        viewModel.movementSkipped.value = false
                        viewModel.movementFeatures.value = MovementFeatures(
                            stsCompletionTimeSec = recordingTimer.toDouble(),
                            repetitionCount = completedReps,
                            kneeAngle = currentAngle.toDouble(),
                            kneeRom = 108.0,
                            asymmetryScore = 0.71,
                            videoQualityScore = if (isSimulatingInconclusive) 0.30 else 0.96
                        )
                        viewModel.evaluateScreening(forceInconclusive = isSimulatingInconclusive)
                    },
                    modifier = Modifier
                        .weight(1.6f)
                        .height(54.dp)
                        .testTag("btn_process_screening_risk"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4))
                ) {
                    Text(
                        "Process Risk Screening (Step 4)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }
        }
    }

    // Bypass 5xSTS Dialog
    if (showBypassDialog) {
        val bypassReasons = listOf(
            "Acute severe knee joint arthralgia on weight-bearing",
            "High fall risk / Unable to rise without hand support",
            "Severe joint instability or severe valgus/varus deformity",
            "General dizziness or balance deficit"
        )
        var selectedReason by remember { mutableStateOf(bypassReasons[0]) }

        AlertDialog(
            onDismissRequest = { showBypassDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Bypass 5xSTS Movement Test",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "The system will record this functional limitation as a severe mobility deficit and evaluate risk based on symptoms and demographics.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )
                    Text("Select primary clinical reason:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = Color(0xFF1E293B))

                    bypassReasons.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedReason = reason }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0061A4))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = reason, style = MaterialTheme.typography.bodySmall, color = Color(0xFF1E293B))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.movementSkipped.value = true
                        viewModel.movementSkipReason.value = selectedReason
                        viewModel.movementFeatures.value = MovementFeatures(
                            testSkipped = true,
                            skipReason = selectedReason
                        )
                        showBypassDialog = false
                        viewModel.evaluateScreening()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Confirm Bypass & Screen", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBypassDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }
}

@Composable
private fun EnvCheckItem(label: String, value: String, isOk: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isOk) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (isOk) Color(0xFF16A34A) else Color(0xFFDC2626),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun FeatureRow(
    title: String,
    metric: String,
    reference: String,
    isFlagged: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isFlagged) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
        border = BorderStroke(
            1.dp,
            if (isFlagged) Color(0xFFFECACA) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
                if (isFlagged) {
                    Surface(shape = RoundedCornerShape(999.dp), color = Color(0xFFFEE2E2)) {
                        Text(
                            text = "FLAGGED",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
            Text(
                text = metric,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isFlagged) Color(0xFFDC2626) else Color(0xFF0061A4)
            )
            Text(
                text = reference,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
        }
    }
}
