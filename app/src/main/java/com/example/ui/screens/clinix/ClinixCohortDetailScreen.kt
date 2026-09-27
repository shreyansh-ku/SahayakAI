package com.example.ui.screens.clinix

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.clinix.model.ActiveModule
import com.example.clinix.model.AnalyticalCohortType
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinixBottomBar
import com.example.ui.components.ClinixHeader

@Composable
fun ClinixCohortDetailScreen(
    viewModel: OrthoScreenViewModel,
    cohortType: AnalyticalCohortType
) {
    val activeModule by viewModel.activeModule.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val cohortInfo = viewModel.getCohortInfo(cohortType)

    Scaffold(
        topBar = {
            ClinixHeader(
                title = cohortType.title,
                subtitle = cohortType.subtitle,
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.ClinixCohorts) },
                activeModule = activeModule,
                onSwitchModule = { viewModel.switchModule(it) },
                isOnline = isOnline
            )
        },
        bottomBar = {
            ClinixBottomBar(
                currentScreen = Screen.ClinixCohortDetail(cohortType),
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
            // Overview Card
            item {
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
                            Text(
                                text = "COHORT OVERVIEW",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 0.5.sp
                            )

                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = Color(0xFFCCFBF1)
                            ) {
                                Text(
                                    text = "${cohortInfo.caseCount} Cases Assigned",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F766E),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = cohortType.description,
                            fontSize = 13.sp,
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        // Common Characteristics
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Common Cluster Characteristics:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            cohortInfo.commonPatterns.forEach { pattern ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("•", color = Color(0xFF0F766E), fontWeight = FontWeight.Bold)
                                    Text(
                                        text = pattern,
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section Header: Cases in this Cohort
            item {
                Text(
                    text = "CASES IN THIS COHORT (${cohortInfo.recentCases.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
            }

            // List of cases
            items(cohortInfo.recentCases) { clinixCase ->
                ClinixCaseCard(
                    clinixCase = clinixCase,
                    onClick = { viewModel.openClinixCase(clinixCase) }
                )
            }
        }
    }
}
