package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ScreeningEntity
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinicalDisclaimerBanner
import com.example.ui.components.ClinicalHeader
import com.example.ui.components.ThermalReferralSlipDialog
import com.example.ui.theme.*

@Composable
fun PatientDetailScreen(
    viewModel: OrthoScreenViewModel,
    screening: ScreeningEntity
) {
    val scrollState = rememberScrollState()
    var showSlip by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ClinicalHeader(
                title = "Patient Record",
                subtitle = "${screening.patientName} • ${screening.patientId}",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.Dashboard) }
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
            ClinicalDisclaimerBanner()

            // Header Card (Geometric Balance: rounded-3xl)
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
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(screening.patientName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text("ID: ${screening.patientId}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = Color(0xFF64748B))
                            Text("Village: ${screening.village}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569))
                        }

                        val (riskBg, riskText) = when (screening.riskLevel) {
                            "HIGH" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
                            "MEDIUM" -> Color(0xFFFEF3C7) to Color(0xFFD97706)
                            else -> Color(0xFFDCFCE7) to Color(0xFF16A34A)
                        }

                        Surface(shape = RoundedCornerShape(999.dp), color = riskBg) {
                            Text(
                                text = "${screening.riskLevel} (${String.format("%.2f", screening.riskScore)})",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = riskText
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "Age / Sex: ${screening.age} Yrs / ${screening.sex}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF334155)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "BMI: ${String.format("%.1f", screening.bmi)} kg/m²",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF334155)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = if (screening.syncStatus == "SYNCED") Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = screening.syncStatus,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (screening.syncStatus == "SYNCED") Color(0xFF16A34A) else Color(0xFFD97706)
                            )
                        }
                    }
                }
            }

            // Biomechanical & Symptom Features (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ASSESSMENT SUMMARY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF64748B)
                    )

                    val summaryItems = listOf(
                        "5xSTS Cadence Time" to "${screening.stsTimeSec}s (Reps: ${screening.repCount})",
                        "Knee Range of Motion" to "${screening.kneeRom}° Arc",
                        "Bilateral Asymmetry" to String.format("%.2f", screening.asymmetry),
                        "WOMAC Severity Index" to "${screening.questionnaireTotal} / 28",
                        "Pain Severity Rating" to "${screening.painScore} / 4"
                    )

                    summaryItems.forEach { (label, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF64748B))
                            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                        }
                    }

                    if (screening.testSkipped) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEE2E2),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Note: 5xSTS test was bypassed (${screening.skipReason ?: "Mobility caution"})",
                                modifier = Modifier.padding(10.dp),
                                color = Color(0xFFDC2626),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Referral Destination (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFDBEAFE))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ASSIGNED CLINICAL PATHWAY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF1D4ED8)
                    )
                    Text(
                        screening.counselingSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        "Destination: ${screening.referralDestination}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )
                }
            }

            // Action Button (Geometric Balance: rounded-2xl)
            Button(
                onClick = { showSlip = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4))
            ) {
                Icon(Icons.Default.Print, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Thermal Referral Slip", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    if (showSlip) {
        ThermalReferralSlipDialog(
            screening = screening,
            onDismiss = { showSlip = false }
        )
    }
}
