package com.example.ui.screens.clinix

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.clinix.model.ActiveModule
import com.example.clinix.model.ClinixCase
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinixBottomBar
import com.example.ui.components.ClinixHeader
import com.example.ui.components.ClinixSafetyDisclaimer
import com.example.ui.components.ModuleSwitcher
import com.example.ui.components.ReviewStatusBadge

@Composable
fun ClinixOverviewScreen(viewModel: OrthoScreenViewModel) {
    val activeModule by viewModel.activeModule.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val metrics by viewModel.clinixOverviewMetrics.collectAsStateWithLifecycle()
    val priorityCases by viewModel.clinixPriorityCases.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ClinixHeader(
                title = "ClinixAI",
                subtitle = "Clinical Review",
                activeModule = activeModule,
                onSwitchModule = { viewModel.switchModule(it) },
                isOnline = isOnline,
                badgeText = "Majuli Civil Hospital • Clinical Decision Support"
            )
        },
        bottomBar = {
            ClinixBottomBar(
                currentScreen = Screen.ClinixOverview,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
        ) {
            // Module Switcher Banner
            item {
                ModuleSwitcher(
                    activeModule = activeModule,
                    onSwitchModule = { viewModel.switchModule(it) }
                )
            }

            // Standard Clinical Safety Disclaimer
            item {
                ClinixSafetyDisclaimer()
            }

            // Compact Priority Metrics Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WORKLOAD METRICS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Real-time Queue",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0F766E)
                    )
                }
            }

            // Compact Metric Grid (Row 1: New Cases, High Risk, Awaiting Review)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClinixMetricTile(
                        modifier = Modifier.weight(1f),
                        label = "New Cases",
                        value = "${metrics.newCasesCount}",
                        accentColor = Color(0xFF0284C7),
                        bgColor = Color(0xFFF0F9FF),
                        tag = "metric_new_cases"
                    )
                    ClinixMetricTile(
                        modifier = Modifier.weight(1f),
                        label = "High-Risk",
                        value = "${metrics.highRiskCount}",
                        accentColor = Color(0xFFDC2626),
                        bgColor = Color(0xFFFEF2F2),
                        tag = "metric_high_risk"
                    )
                    ClinixMetricTile(
                        modifier = Modifier.weight(1f),
                        label = "Awaiting",
                        value = "${metrics.awaitingReviewCount}",
                        accentColor = Color(0xFFD97706),
                        bgColor = Color(0xFFFFFBEB),
                        tag = "metric_awaiting_review"
                    )
                }
            }

            // Compact Metric Grid (Row 2: Inconclusive, Atypical Cases)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClinixMetricTile(
                        modifier = Modifier.weight(1f),
                        label = "Inconclusive",
                        value = "${metrics.inconclusiveCount}",
                        accentColor = Color(0xFF4F46E5),
                        bgColor = Color(0xFFEEF2FF),
                        tag = "metric_inconclusive"
                    )
                    ClinixMetricTile(
                        modifier = Modifier.weight(1f),
                        label = "Atypical Cases",
                        value = "${metrics.atypicalCount}",
                        accentColor = Color(0xFF7C3AED),
                        bgColor = Color(0xFFF5F3FF),
                        tag = "metric_atypical"
                    )
                }
            }

            // Priority Cases Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Priority Cases",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Stratified by clinical urgency and triage criteria",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }

                    Text(
                        text = "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E),
                        modifier = Modifier
                            .clickable { viewModel.navigateTo(Screen.ClinixCases) }
                            .padding(4.dp)
                            .testTag("btn_view_all_cases")
                    )
                }
            }

            // Priority Case Cards
            if (priorityCases.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "All priority cases reviewed",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "No urgent screenings pending in the clinical queue.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            } else {
                items(priorityCases) { clinixCase ->
                    ClinixPriorityCaseCard(
                        clinixCase = clinixCase,
                        onReviewClick = {
                            viewModel.openClinixCase(clinixCase)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ClinixMetricTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accentColor: Color,
    bgColor: Color,
    tag: String
) {
    Surface(
        modifier = modifier.testTag(tag),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569),
                maxLines = 1
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }
    }
}

@Composable
fun ClinixPriorityCaseCard(
    clinixCase: ClinixCase,
    onReviewClick: () -> Unit
) {
    val screening = clinixCase.screening
    val isHighRisk = screening.riskLevel == "HIGH"
    val isInconclusive = screening.riskLevel == "INCONCLUSIVE"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onReviewClick)
            .testTag("priority_case_${clinixCase.caseId}"),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(
            1.dp,
            if (isHighRisk) Color(0xFFFECACA) else Color(0xFFE2E8F0)
        ),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Case ID & Risk Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Case #${screening.patientId.takeLast(4)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    if (clinixCase.isDemo) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = "Demo / Synthetic",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Risk Badge
                    val (riskBg, riskText) = when (screening.riskLevel) {
                        "HIGH" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
                        "MEDIUM" -> Color(0xFFFEF3C7) to Color(0xFFD97706)
                        "INCONCLUSIVE" -> Color(0xFFEDE9FE) to Color(0xFF7C3AED)
                        else -> Color(0xFFDCFCE7) to Color(0xFF16A34A)
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = riskBg
                    ) {
                        Text(
                            text = screening.riskLevel,
                            color = riskText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    // Review Status Badge
                    ReviewStatusBadge(status = clinixCase.reviewStatus)
                }
            }

            // Patient Demographic & Key Feature Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = screening.patientName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Age: ${screening.age} • ${screening.sex} • BMI ${String.format("%.1f", screening.bmi)}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Pain: ${screening.painScore}/10",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (screening.painScore >= 7) Color(0xFFDC2626) else Color(0xFF334155)
                    )
                    Text(
                        text = if (screening.testSkipped) "5xSTS: Bypassed" else "5xSTS: ${String.format("%.1f", screening.stsTimeSec)}s",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Main Clinical Observation / Atypical indicator
            if (clinixCase.isAtypical) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFAF5FF),
                    border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flare,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Atypical feature pattern: ${clinixCase.atypicalReason ?: "Distinct profile"}",
                            fontSize = 11.sp,
                            color = Color(0xFF6B21A8),
                            maxLines = 1
                        )
                    }
                }
            }

            // Footer Row: Date & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = screening.formattedDate,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Button(
                    onClick = onReviewClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F766E),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Review Case",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
