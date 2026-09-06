package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ScreeningEntity
import com.example.data.model.ContributingFactor
import com.example.data.model.RiskLevel
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinicalBottomBar
import com.example.ui.components.ClinicalHeader
import com.example.ui.components.ThermalReferralSlipDialog
import com.example.ui.theme.*

@Composable
fun ResultScreen(
    viewModel: OrthoScreenViewModel
) {
    val scrollState = rememberScrollState()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val unsyncedCount by viewModel.unsyncedCount.collectAsStateWithLifecycle()

    val result = viewModel.currentRiskResult.collectAsStateWithLifecycle().value
    val patientName by viewModel.patientName.collectAsStateWithLifecycle()
    val patientAge by viewModel.patientAge.collectAsStateWithLifecycle()
    val patientSex by viewModel.patientSex.collectAsStateWithLifecycle()
    val heightCm by viewModel.patientHeightCm.collectAsStateWithLifecycle()
    val weightKg by viewModel.patientWeightKg.collectAsStateWithLifecycle()
    val movementFeatures by viewModel.movementFeatures.collectAsStateWithLifecycle()
    val patientBmi = viewModel.calculateBmi(heightCm, weightKg)
    val caseId by viewModel.generatedCaseId.collectAsStateWithLifecycle()

    var showThermalSlip by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ClinicalHeader(
                title = "Screening Result",
                subtitle = "Assessment",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.MovementAssessment) },
                isOnline = isOnline,
                unsyncedCount = unsyncedCount,
                onToggleNetwork = { viewModel.toggleNetworkMode() },
                badgeText = if (caseId.isNotBlank()) "ID: $caseId" else "ID: NE-8842"
            )
        },
        bottomBar = {
            Column {
                // Geometric Action Buttons
                Surface(
                    color = ClinicalSurfaceContainerLowest,
                    border = BorderStroke(1.dp, ClinicalOutlineSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.navigateTo(Screen.Dashboard)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("btn_discard"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF1F5F9),
                                contentColor = Color(0xFF334155)
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text("Discard", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.saveCurrentScreening()
                                isSaved = true
                                showThermalSlip = true
                            },
                            modifier = Modifier
                                .weight(2f)
                                .height(56.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF9ECAFF))
                                .testTag("btn_save_and_sync"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0061A4),
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (isSaved) "Referral Slip" else "Save & Sync",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text("→", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Geometric Balance Bottom Bar
                ClinicalBottomBar(
                    currentScreen = Screen.Result,
                    onNavigate = { viewModel.navigateTo(it) },
                    unsyncedCount = unsyncedCount
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FB))
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section 1: Patient Header Card (Geometric Balance: rounded-3xl border-slate-200)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("patient_summary_card"),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 26.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Patient Name",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                        Text(
                            text = patientName.ifBlank { "Rajesh Kumar" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontSize = 19.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                            ) {
                                Text(
                                    text = "${if (patientSex.isNotBlank()) patientSex else "Male"}, ${if (patientAge > 0) patientAge else 58}y",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = Color(0xFF334155)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                            ) {
                                Text(
                                    text = "BMI: ${if (patientBmi > 0.0) String.format("%.1f", patientBmi) else "29.4"}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }
                }
            }

            if (result == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF0061A4))
                }
            } else if (result.isInconclusive) {
                // Inconclusive Card in Geometric Balance Style
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ASSESSMENT INCONCLUSIVE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "REPEAT ASSESSMENT",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F)
                        )
                        HorizontalDivider(color = Color(0xFFFDE68A), thickness = 1.dp)
                        Text(
                            text = "* SCREENING RESULT — INSUFFICIENT DATA QUALITY. REPEAT VIDEO RECORDING OR CONSULT A CLINICIAN.",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF92400E),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                // Section 2: Potential Screening Risk Hero Card (Geometric Balance: #FFDAD6, #FFB4AB, #410002, rounded-3xl)
                val isHigh = result.riskLevel == RiskLevel.HIGH
                val isMedium = result.riskLevel == RiskLevel.MEDIUM

                val (riskCardBg, riskCardBorder, riskCardText, riskCardDisclaimer) = when {
                    isHigh -> Quad(Color(0xFFFFDAD6), Color(0xFFFFB4AB), Color(0xFF410002), Color(0xFF93000A))
                    isMedium -> Quad(Color(0xFFFEF3C7), Color(0xFFFDE68A), Color(0xFF78350F), Color(0xFF92400E))
                    else -> Quad(Color(0xFFECFDF5), Color(0xFFA7F3D0), Color(0xFF065F46), Color(0xFF047857))
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("risk_level_hero_card"),
                    shape = RoundedCornerShape(24.dp),
                    color = riskCardBg,
                    border = BorderStroke(1.dp, riskCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "POTENTIAL SCREENING RISK",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.4.sp,
                            fontSize = 11.sp,
                            color = riskCardText
                        )

                        Text(
                            text = "${result.riskLevel.label.uppercase()} RISK",
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = riskCardText
                        )

                        HorizontalDivider(
                            color = riskCardBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = "* SCREENING RESULT — NOT A MEDICAL DIAGNOSIS. CONSULT A CLINICIAN FOR FURTHER EVALUATION.",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = riskCardDisclaimer,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                    }
                }

                // Section 3: 2-Column Metrics Grid (Geometric Balance: grid-cols-2 rounded-3xl border-slate-200)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Metric Card: 5xSTS Time
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "5XSTS TIME",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "${movementFeatures.stsCompletionTimeSec}s",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                fontSize = 24.sp
                            )
                            Text(
                                text = if (movementFeatures.stsCompletionTimeSec > 14.0) "Slow (+${String.format("%.1f", movementFeatures.stsCompletionTimeSec - 12.0)}s vs norm)" else "Normal Cadence",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = if (movementFeatures.stsCompletionTimeSec > 14.0) Color(0xFFEF4444) else Color(0xFF10B981)
                            )
                        }
                    }

                    // Right Metric Card: Knee ROM
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "KNEE ROM",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "${movementFeatures.kneeRom.toInt()}°",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                fontSize = 24.sp
                            )
                            Text(
                                text = if (movementFeatures.kneeRom < 110.0) "Reduced Flexion" else "Preserved Range",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = if (movementFeatures.kneeRom < 110.0) Color(0xFFD97706) else Color(0xFF10B981)
                            )
                        }
                    }
                }

                // Section 4: Contributing Factors Card (Geometric Balance: rounded-3xl with tag chips)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "CONTRIBUTING FACTORS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )

                        // Pills container
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PillBadge(
                                    text = "Morning Stiffness",
                                    colorType = PillColorType.RED,
                                    modifier = Modifier.weight(1f)
                                )
                                PillBadge(
                                    text = "Frequent Squatting",
                                    colorType = PillColorType.RED,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PillBadge(
                                    text = "High Physical Workload",
                                    colorType = PillColorType.AMBER,
                                    modifier = Modifier.weight(1.1f)
                                )
                                PillBadge(
                                    text = "5xSTS Asymmetry (${((1.0 - movementFeatures.asymmetryScore) * 100).toInt()}%)",
                                    colorType = PillColorType.SLATE,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Recommendation sub-section with border-t border-slate-50
                        HorizontalDivider(
                            color = Color(0xFFF1F5F9),
                            thickness = 1.dp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "RECOMMENDATION",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "Further clinical evaluation is strongly recommended. Prioritize diagnostic imaging (weight-bearing AP/Lateral views) and functional gait analysis.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF334155),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Thermal Slip Dialog
    if (showThermalSlip) {
        val lastSaved = viewModel.lastSavedScreening.collectAsStateWithLifecycle().value
        if (lastSaved != null) {
            ThermalReferralSlipDialog(
                screening = lastSaved,
                onDismiss = { showThermalSlip = false }
            )
        }
    }
}

private enum class PillColorType { RED, AMBER, SLATE, BLUE }

@Composable
private fun PillBadge(
    text: String,
    colorType: PillColorType,
    modifier: Modifier = Modifier
) {
    val (bg, border, textColor) = when (colorType) {
        PillColorType.RED -> Triple(Color(0xFFFEF2F2), Color(0xFFFEE2E2), Color(0xFFB91C1C))
        PillColorType.AMBER -> Triple(Color(0xFFFFFBEB), Color(0xFFFEF3C7), Color(0xFFB45309))
        PillColorType.SLATE -> Triple(Color(0xFFF8FAFC), Color(0xFFF1F5F9), Color(0xFF475569))
        PillColorType.BLUE -> Triple(Color(0xFFEFF6FF), Color(0xFFDBEAFE), Color(0xFF1D4ED8))
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = bg,
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "●",
                fontSize = 8.sp,
                color = textColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = textColor,
                maxLines = 1
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
