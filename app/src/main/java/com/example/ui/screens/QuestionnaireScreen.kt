package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinicalDisclaimerBanner
import com.example.ui.components.ClinicalHeader
import com.example.ui.theme.*

@Composable
fun QuestionnaireScreen(
    viewModel: OrthoScreenViewModel
) {
    val scrollState = rememberScrollState()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val unsyncedCount by viewModel.unsyncedCount.collectAsStateWithLifecycle()

    val painScore by viewModel.qPainScore.collectAsStateWithLifecycle()
    val stiffness by viewModel.qStiffness.collectAsStateWithLifecycle()
    val walking by viewModel.qWalkingLimit.collectAsStateWithLifecycle()
    val stairs by viewModel.qStairsDifficulty.collectAsStateWithLifecycle()
    val stand by viewModel.qStandDifficulty.collectAsStateWithLifecycle()
    val crepitus by viewModel.qCrepitus.collectAsStateWithLifecycle()
    val nightPain by viewModel.qNightPain.collectAsStateWithLifecycle()

    val totalScore = viewModel.computeQuestionnaireTotal()
    val severityLabel = when {
        totalScore >= 20 -> "Severe Symptomatic Burden"
        totalScore >= 12 -> "Moderate Symptomatic Burden"
        totalScore >= 6 -> "Mild Symptomatic Burden"
        else -> "Minimal Symptom Presence"
    }

    Scaffold(
        topBar = {
            ClinicalHeader(
                title = "New Screening",
                subtitle = "Step 2 of 4 • Standardized Symptoms Survey",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.Registration) },
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
                            text = "STEP 2 OF 4: STANDARDIZED SYMPTOMS SURVEY",
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
                                text = "50% Completed",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { 0.50f },
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
                        Text("1. Intake (Done)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = Color(0xFF0061A4))
                        Text("2. Symptoms", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0061A4), fontWeight = FontWeight.Bold)
                        Text("3. Motion & AI", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        Text("4. Referral", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                    }
                }
            }

            // Clinical Advisory Banner
            ClinicalDisclaimerBanner()

            // Protocol Chip (Geometric Balance: rounded-2xl)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(20.dp))
                    Text(
                        text = "CLINICAL PROTOCOL: WOMAC & ACR ADAPTED FOR RURAL ASHA SCREENING",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp,
                        color = Color(0xFF1E40AF)
                    )
                }
            }

            // Section 1: Knee Pain Severity (Geometric Balance: rounded-3xl)
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
                            text = "1. KNEE PAIN SEVERITY (LAST 30 DAYS)",
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
                                text = "Score: $painScore / 4",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0061A4)
                            )
                        }
                    }
                    Text(
                        text = "Rate overall pain during weight-bearing activities (walking, carrying firewood/water):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF334155)
                    )

                    // 5 Pain Levels
                    val painLevels = listOf(
                        "0 - None" to "No knee joint pain",
                        "1 - Mild" to "Noticeable but doesn't restrict work",
                        "2 - Moderate" to "Limits prolonged field/domestic work",
                        "3 - Severe" to "Stops daily chores frequently",
                        "4 - Disabling" to "Constant agonizing weight-bearing pain"
                    )

                    painLevels.forEachIndexed { index, (title, desc) ->
                        val isSelected = painScore == index
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF0061A4) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.qPainScore.value = index }
                                .testTag("pain_level_$index")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.qPainScore.value = index },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0061A4))
                                )
                                Column {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF0061A4) else Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Morning Joint Stiffness Duration (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "2. MORNING JOINT STIFFNESS DURATION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "How long does knee joint stiffness last upon waking from bed?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF334155)
                    )

                    val stiffnessOptions = listOf(
                        "< 10 minutes (Normal/Transient)",
                        "10 - 30 minutes (Classic Osteoarthritis indicator)",
                        "> 30 minutes (Possible Inflammatory/RA flag)",
                        "Severe / Persistent throughout morning"
                    )

                    stiffnessOptions.forEachIndexed { index, option ->
                        val isSelected = stiffness == index
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF0061A4) else Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.qStiffness.value = index }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.qStiffness.value = index },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0061A4))
                                )
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) Color(0xFF0061A4) else Color(0xFF1E293B),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Section 3: Functional Mobility & Weight-Bearing Limitations (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "3. FUNCTIONAL MOBILITY & DAILY LIMITATIONS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF64748B)
                    )

                    // A: Walking
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "A. Walking on flat ground / unpaved village road:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                        val walkOpts = listOf("No difficulty", "Walks < 500m before stopping", "Walks < 100m with pain", "Unable without stick / support")
                        walkOpts.forEachIndexed { index, opt ->
                            SelectOptionRow(text = opt, isSelected = walking == index, onSelect = { viewModel.qWalkingLimit.value = index })
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // B: Stairs / Sakho
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "B. Ascending / Descending stairs or bamboo bridge (Sakho):",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                        val stairsOpts = listOf("No difficulty", "Mild pain on descent", "Moderate / one step at a time", "Severe / Needs rail or person")
                        stairsOpts.forEachIndexed { index, opt ->
                            SelectOptionRow(text = opt, isSelected = stairs == index, onSelect = { viewModel.qStairsDifficulty.value = index })
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // C: Rising from floor / Murha
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "C. Rising from floor / low stool (Murha) / squatting:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                        val standOpts = listOf("Easy without hands", "Needs hands on thighs", "Needs wall/furniture support", "Severe pain / Cannot stand alone")
                        standOpts.forEachIndexed { index, opt ->
                            SelectOptionRow(text = opt, isSelected = stand == index, onSelect = { viewModel.qStandDifficulty.value = index })
                        }
                    }
                }
            }

            // Section 4: Physical Signs (Crepitus & Nocturnal Pain) (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "4. PHYSICAL & PALPATORY KNEE SIGNS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF64748B)
                    )

                    // Crepitus
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Audible or Palpable Joint Crepitus (Grating / crackling sensation):",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                        val crepitusOpts = listOf("None / Smooth articulation (0)", "Occasional / Fine crackle (1)", "Coarse grating on every flexion (2)")
                        crepitusOpts.forEachIndexed { index, opt ->
                            SelectOptionRow(text = opt, isSelected = crepitus == index, onSelect = { viewModel.qCrepitus.value = index })
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Night Pain
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Nocturnal Rest Pain (Awakened at night by knee throbbing):",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                        val nightOpts = listOf("Never (0)", "Rarely / After heavy work (1)", "2-3 nights per week (2)", "Most nights / Disrupts sleep (3)")
                        nightOpts.forEachIndexed { index, opt ->
                            SelectOptionRow(text = opt, isSelected = nightPain == index, onSelect = { viewModel.qNightPain.value = index })
                        }
                    }
                }
            }

            // Score Summary Card (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF0061A4),
                border = BorderStroke(1.dp, Color(0xFF004D84))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL SYMPTOM BURDEN SCORE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFFBAE6FD)
                        )
                        Text(
                            text = "$totalScore / 28 points",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = severityLabel,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Bottom Navigation Buttons
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
                        .clickable { viewModel.navigateTo(Screen.Registration) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Intake", color = Color(0xFF334155), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Button(
                    onClick = { viewModel.navigateTo(Screen.MovementAssessment) },
                    modifier = Modifier
                        .weight(1.6f)
                        .height(54.dp)
                        .testTag("btn_proceed_to_movement"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4))
                ) {
                    Text("Continue to 5xSTS (Step 3)", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SelectOptionRow(
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF0061A4) else Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0061A4))
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) Color(0xFF0061A4) else Color(0xFF1E293B),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
