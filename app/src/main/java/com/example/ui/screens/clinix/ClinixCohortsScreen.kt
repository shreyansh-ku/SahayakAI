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
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
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
import com.example.clinix.model.AnalyticalCohortInfo
import com.example.clinix.model.AnalyticalCohortType
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinixBottomBar
import com.example.ui.components.ClinixHeader

@Composable
fun ClinixCohortsScreen(viewModel: OrthoScreenViewModel) {
    val activeModule by viewModel.activeModule.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val cohorts by viewModel.analyticalCohortInfos.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ClinixHeader(
                title = "Clinical Cohorts",
                subtitle = "Analytical Clustering",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.ClinixOverview) },
                activeModule = activeModule,
                onSwitchModule = { viewModel.switchModule(it) },
                isOnline = isOnline
            )
        },
        bottomBar = {
            ClinixBottomBar(
                currentScreen = Screen.ClinixCohorts,
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
            // Informational Banner: Non-diagnostic Cohort Explanation
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
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
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Analytical Cohorts (Not Disease Categories)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF166534)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Cohorts group cases by shared functional profiles (pain-dominance vs kinematic offloading) to help identify workload patterns and care pathways.",
                                fontSize = 11.sp,
                                color = Color(0xFF14532D),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Text(
                    text = "4 ACTIVE ANALYTICAL COHORTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
            }

            // Cohort Cards
            items(cohorts) { cohortInfo ->
                CohortSummaryCard(
                    cohortInfo = cohortInfo,
                    onClick = {
                        viewModel.openCohortDetail(cohortInfo.cohortType)
                    }
                )
            }
        }
    }
}

@Composable
fun CohortSummaryCard(
    cohortInfo: AnalyticalCohortInfo,
    onClick: () -> Unit
) {
    val type = cohortInfo.cohortType

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("cohort_card_${type.id}"),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Title & Case Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCCFBF1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BubbleChart,
                            contentDescription = null,
                            tint = Color(0xFF0F766E),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = type.title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = type.subtitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0F766E)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = "${cohortInfo.caseCount} cases",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = type.description,
                fontSize = 12.sp,
                color = Color(0xFF475569),
                lineHeight = 16.sp
            )

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Common Patterns
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Common feature patterns:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                cohortInfo.commonPatterns.forEach { p ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("•", color = Color(0xFF0F766E), fontWeight = FontWeight.Bold)
                        Text(text = p, fontSize = 11.sp, color = Color(0xFF334155))
                    }
                }
            }

            // Footer: Risk Distribution Pill & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val highCount = cohortInfo.riskDistribution["HIGH"] ?: 0
                val medCount = cohortInfo.riskDistribution["MEDIUM"] ?: 0
                val lowCount = cohortInfo.riskDistribution["LOW"] ?: 0

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (highCount > 0) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEE2E2)) {
                            Text(
                                text = "High: $highCount",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (medCount > 0) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEF3C7)) {
                            Text(
                                text = "Med: $medCount",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (lowCount > 0) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDCFCE7)) {
                            Text(
                                text = "Low: $lowCount",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Explore Cohort",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF0F766E),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
