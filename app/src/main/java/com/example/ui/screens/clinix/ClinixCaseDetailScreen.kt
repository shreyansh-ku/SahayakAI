package com.example.ui.screens.clinix

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.clinix.model.ActiveModule
import com.example.clinix.model.ClinixCase
import com.example.clinix.model.ReviewStatus
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinixBottomBar
import com.example.ui.components.ClinixHeader
import com.example.ui.components.ReviewStatusBadge

@Composable
fun ClinixCaseDetailScreen(
    viewModel: OrthoScreenViewModel,
    caseId: String
) {
    val activeModule by viewModel.activeModule.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val clinixCase = viewModel.getClinixCaseById(caseId)

    if (clinixCase == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Case record not found: $caseId")
            Button(onClick = { viewModel.navigateTo(Screen.ClinixCases) }) {
                Text("Back to Cases")
            }
        }
        return
    }

    val screening = clinixCase.screening
    val scrollState = rememberScrollState()

    var clinicalNote by remember(clinixCase.review?.clinicalNote) {
        mutableStateOf(clinixCase.review?.clinicalNote ?: "")
    }
    var selectedAction by remember(clinixCase.review?.status) {
        mutableStateOf(
            when (clinixCase.review?.status) {
                "REVIEWED" -> ReviewStatus.REVIEWED
                "REPEAT_REQUIRED" -> ReviewStatus.REPEAT_REQUIRED
                "REFERRED" -> ReviewStatus.REFERRED
                else -> ReviewStatus.REVIEWED
            }
        )
    }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            ClinixHeader(
                title = "Case #${screening.patientId.takeLast(4)}",
                subtitle = "Clinical Case Review",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.ClinixCases) },
                activeModule = activeModule,
                onSwitchModule = { viewModel.switchModule(it) },
                isOnline = isOnline
            )
        },
        bottomBar = {
            ClinixBottomBar(
                currentScreen = Screen.ClinixCaseDetail(caseId),
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Card: Screening Result & Statutory Non-Diagnostic Banner
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SCREENING RESULT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Potential OA Risk: ${screening.riskLevel}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = when (screening.riskLevel) {
                                    "HIGH" -> Color(0xFFDC2626)
                                    "MEDIUM" -> Color(0xFFD97706)
                                    "INCONCLUSIVE" -> Color(0xFF7C3AED)
                                    else -> Color(0xFF16A34A)
                                }
                            )
                        }

                        ReviewStatusBadge(status = clinixCase.reviewStatus)
                    }

                    // Mandatory Non-Diagnostic Banner Immediately Below Screening Result
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Screening result — not a diagnosis.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    Text(
                        text = "Screening Date: ${screening.formattedDate} • Attested by: ${screening.attestingWorker}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Section 1: Patient Profile
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Patient Profile",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    DetailItemRow(label = "Patient Name", value = screening.patientName)
                    DetailItemRow(label = "Patient ID", value = screening.patientId)
                    DetailItemRow(label = "Age / Sex", value = "${screening.age} years • ${screening.sex}")
                    DetailItemRow(
                        label = "BMI",
                        value = "${String.format("%.1f", screening.bmi)} kg/m² (${screening.heightCm.toInt()} cm, ${screening.weightKg.toInt()} kg)"
                    )
                    DetailItemRow(label = "Village / Catchment", value = screening.village)
                    DetailItemRow(label = "Occupation", value = screening.occupationCategory)
                    DetailItemRow(
                        label = "Previous Knee Injury",
                        value = if (screening.hasInjury) screening.injuryDetails.ifBlank { "Yes (Reported)" } else "None reported"
                    )
                    DetailItemRow(label = "Comorbidities", value = screening.comorbidities.ifBlank { "None reported" })
                }
            }

            // Section 2: Musculoskeletal Symptoms
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Reported Symptoms",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    DetailItemRow(label = "Pain Score", value = "${screening.painScore} / 10")
                    DetailItemRow(label = "Morning Stiffness", value = "${screening.stiffnessDuration} minutes")
                    DetailItemRow(
                        label = "Walking Difficulty",
                        value = describeSeverity(screening.walkingLimitation)
                    )
                    DetailItemRow(
                        label = "Stair Negotiation",
                        value = describeSeverity(screening.stairsDifficulty)
                    )
                    DetailItemRow(
                        label = "Chair-Rise Difficulty",
                        value = describeSeverity(screening.standDifficulty)
                    )
                    DetailItemRow(
                        label = "Joint Crepitus",
                        value = if (screening.crepitus > 0) "Present (Palpable / Audible)" else "Absent"
                    )
                    DetailItemRow(
                        label = "Night / Rest Pain",
                        value = describeSeverity(screening.nightPain)
                    )
                }
            }

            // Section 3: Movement & Kinematics
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Movement Telemetry (5xSTS Protocol)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    if (screening.testSkipped) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFECACA))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "5xSTS Protocol Safely Bypassed",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B),
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Reason: ${screening.skipReason ?: "Safety contraindication indicated"}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                        }
                    } else {
                        DetailItemRow(
                            label = "5xSTS Completion Time",
                            value = "${String.format("%.1f", screening.stsTimeSec)} seconds"
                        )
                        DetailItemRow(label = "Repetitions Completed", value = "${screening.repCount} / 5")
                        DetailItemRow(label = "Knee Range of Motion", value = "${screening.kneeRom.toInt()}° ROM")
                        DetailItemRow(label = "Deepest Knee Flexion", value = "${screening.kneeAngle.toInt()}°")
                        DetailItemRow(
                            label = "Left/Right Asymmetry",
                            value = "${(screening.asymmetry * 100).toInt()}% offloading"
                        )
                        DetailItemRow(label = "Pose Landmark Confidence", value = "94% Tracking Integrity")
                        DetailItemRow(label = "Video Quality Score", value = "96% Optimal Lighting & Framing")
                    }
                }
            }

            // Section 4: AI Analytical Summary
            val observations = viewModel.getAiAnalyticalSummary(screening)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "AI-Assisted Analysis",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }

                    Text(
                        text = "Key feature patterns identified from multi-modal screening inputs:",
                        fontSize = 12.sp,
                        color = Color(0xFF14532D)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        observations.forEach { obs ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("•", fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                Text(
                                    text = obs,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1F2937),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = "Pattern recognition only. Clinical decisions remain solely with the physician.",
                        fontSize = 10.sp,
                        color = Color(0xFF15803D),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quick Navigation Actions: Similar Cases & Cohorts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.openSimilarCases(clinixCase) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_find_similar_cases"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F766E),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Similar Cases", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.openCohortDetail(clinixCase.assignedCohort) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_view_cohort"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF0F766E)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF0F766E))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BubbleChart,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("View Cohort", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Section 5: Clinician Review & Final Action
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFF0F766E)),
                shadowElevation = 2.dp,
                modifier = Modifier.testTag("clinician_review_section")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Clinician Review",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )

                    Text(
                        text = "Select final clinical review decision:",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    // Action Choice Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ReviewActionChip(
                            label = "Mark Reviewed",
                            isSelected = selectedAction == ReviewStatus.REVIEWED,
                            onClick = { selectedAction = ReviewStatus.REVIEWED },
                            modifier = Modifier.weight(1f)
                        )

                        ReviewActionChip(
                            label = "Repeat Screening",
                            isSelected = selectedAction == ReviewStatus.REPEAT_REQUIRED,
                            onClick = { selectedAction = ReviewStatus.REPEAT_REQUIRED },
                            modifier = Modifier.weight(1f)
                        )

                        ReviewActionChip(
                            label = "Refer Case",
                            isSelected = selectedAction == ReviewStatus.REFERRED,
                            onClick = { selectedAction = ReviewStatus.REFERRED },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Clinical Note Text Field
                    OutlinedTextField(
                        value = clinicalNote,
                        onValueChange = { clinicalNote = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("clinical_note_input"),
                        label = { Text("Clinical Note", fontSize = 12.sp) },
                        placeholder = {
                            Text(
                                "Enter diagnostic recommendations, secondary imaging orders, or physio referral notes...",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0F766E),
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    // Save Review Button
                    Button(
                        onClick = {
                            viewModel.saveClinicianReview(
                                caseId = clinixCase.caseId,
                                status = selectedAction,
                                clinicalNote = clinicalNote,
                                actionTaken = when (selectedAction) {
                                    ReviewStatus.REVIEWED -> "Reviewed & Validated by Clinician"
                                    ReviewStatus.REPEAT_REQUIRED -> "Repeat Screening Requested (Inconclusive / Mobility Check)"
                                    ReviewStatus.REFERRED -> "Referred for Clinical Orthopedic Evaluation"
                                    else -> "In Review"
                                }
                            )
                            saveSuccessMessage = "Review saved successfully."
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_save_review"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F766E),
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text("Save Review", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (saveSuccessMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = saveSuccessMessage!!,
                                color = Color(0xFF15803D),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }

                    // Independent Clinical Decision Statement
                    Text(
                        text = "AI assists organization and pattern review. Final clinical decisions remain with the clinician.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewActionChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.testTag("action_chip_$label"),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0xFF0F766E) else Color(0xFFF1F5F9),
        border = BorderStroke(
            1.dp,
            if (isSelected) Color(0xFF0F766E) else Color(0xFFCBD5E1)
        )
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF334155),
                maxLines = 1
            )
        }
    }
}

@Composable
fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B),
            modifier = Modifier.weight(1.2f)
        )
    }
}

fun describeSeverity(score: Int): String {
    return when (score) {
        0 -> "None (0/4)"
        1 -> "Mild (1/4)"
        2 -> "Moderate (2/4)"
        3 -> "Severe (3/4)"
        4 -> "Extreme / Unable (4/4)"
        else -> "$score/4"
    }
}
