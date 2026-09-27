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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.clinix.model.AtypicalCase
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinixBottomBar
import com.example.ui.components.ClinixHeader

@Composable
fun ClinixOutliersScreen(viewModel: OrthoScreenViewModel) {
    val activeModule by viewModel.activeModule.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val atypicalCases by viewModel.atypicalCasesList.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ClinixHeader(
                title = "Atypical Cases",
                subtitle = "Outlier & Variance Detection",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.ClinixOverview) },
                activeModule = activeModule,
                onSwitchModule = { viewModel.switchModule(it) },
                isOnline = isOnline
            )
        },
        bottomBar = {
            ClinixBottomBar(
                currentScreen = Screen.ClinixOutliers,
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
            // Mandatory Interpretation Notice
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFAF5FF),
                    border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Atypical Feature Interpretation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF581C87)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "This case may require closer clinical review because its feature pattern differs from surrounding cases. An outlier does NOT indicate rare pathology.",
                                fontSize = 11.sp,
                                color = Color(0xFF6B21A8),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FLAGGED ATYPICAL CASES (${atypicalCases.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Variance Threshold > 0.35",
                        fontSize = 11.sp,
                        color = Color(0xFF7C3AED),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Atypical Case Cards
            items(atypicalCases) { atypicalCase ->
                AtypicalCaseCard(
                    atypicalCase = atypicalCase,
                    onReviewClick = {
                        viewModel.navigateTo(Screen.ClinixCaseDetail(atypicalCase.caseId))
                    }
                )
            }
        }
    }
}

@Composable
fun AtypicalCaseCard(
    atypicalCase: AtypicalCase,
    onReviewClick: () -> Unit
) {
    val screening = atypicalCase.screening

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onReviewClick)
            .testTag("outlier_card_${atypicalCase.caseId}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Case ID and Outlier Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3E8FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flare,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Case #${screening.patientId.takeLast(4)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = screening.patientName,
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF5F3FF)
                ) {
                    Text(
                        text = "Atypical Pattern",
                        color = Color(0xFF7C3AED),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Sub-demographics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Age: ${screening.age} • ${screening.sex} • BMI ${String.format("%.1f", screening.bmi)}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Risk: ${screening.riskLevel}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (screening.riskLevel) {
                        "HIGH" -> Color(0xFFDC2626)
                        "MEDIUM" -> Color(0xFFD97706)
                        "INCONCLUSIVE" -> Color(0xFF7C3AED)
                        else -> Color(0xFF16A34A)
                    }
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Explanation Section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Identified pattern divergence:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF581C87)
                )
                Text(
                    text = atypicalCase.explanation,
                    fontSize = 12.sp,
                    color = Color(0xFF334155),
                    lineHeight = 16.sp
                )
            }

            // Potential Contributing Reasons
            if (atypicalCase.unusualFeatures.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    atypicalCase.unusualFeatures.forEach { f ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("•", color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)
                            Text(text = f, fontSize = 11.sp, color = Color(0xFF475569))
                        }
                    }
                }
            }

            // Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onReviewClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Review Case", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.size(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
