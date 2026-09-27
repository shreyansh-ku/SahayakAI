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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.clinix.model.SimilarCase
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinixBottomBar
import com.example.ui.components.ClinixHeader

@Composable
fun ClinixSimilarCasesScreen(
    viewModel: OrthoScreenViewModel,
    caseId: String,
    initialTopK: Int = 5
) {
    val activeModule by viewModel.activeModule.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val baseCase = viewModel.getClinixCaseById(caseId)
    val similarCases by viewModel.similarCasesList.collectAsStateWithLifecycle()

    var selectedTopK by remember { mutableIntStateOf(initialTopK) }

    LaunchedEffect(caseId, selectedTopK) {
        viewModel.computeSimilarCases(caseId, selectedTopK)
    }

    Scaffold(
        topBar = {
            ClinixHeader(
                title = "Similar Cases",
                subtitle = "Case #${baseCase?.screening?.patientId?.takeLast(4) ?: caseId}",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.ClinixCaseDetail(caseId)) },
                activeModule = activeModule,
                onSwitchModule = { viewModel.switchModule(it) },
                isOnline = isOnline
            )
        },
        bottomBar = {
            ClinixBottomBar(
                currentScreen = Screen.ClinixSimilarCases(caseId, selectedTopK),
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
        ) {
            // Mandatory Disclaimer Banner
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
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
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Similarity ≠ Diagnosis",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Similarity scores reflect mathematical proximity of feature representations. It is not an estimate of disease probability or diagnosis.",
                                fontSize = 11.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Top-K Selector
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Retrieval Depth (Top-K):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(3, 5, 10).forEach { k ->
                                val isSelected = selectedTopK == k
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedTopK = k },
                                    label = {
                                        Text(
                                            text = "Top $k",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF0F766E),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFFF1F5F9),
                                        labelColor = Color(0xFF334155)
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFF0F766E) else Color(0xFFCBD5E1)
                                    ),
                                    modifier = Modifier.testTag("top_k_$k")
                                )
                            }
                        }
                    }
                }
            }

            // Results Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOP $selectedTopK SIMILAR HISTORICAL CASES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Cosine Distance Metric",
                        fontSize = 10.sp,
                        color = Color(0xFF0F766E),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Similar Case Cards
            if (similarCases.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Calculating vector similarity...",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            } else {
                items(similarCases) { simCase ->
                    SimilarCaseCard(
                        similarCase = simCase,
                        onOpenCase = {
                            viewModel.navigateTo(Screen.ClinixCaseDetail(simCase.caseId))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SimilarCaseCard(
    similarCase: SimilarCase,
    onOpenCase: () -> Unit
) {
    val simPercent = (similarCase.similarityScore * 100).toInt()

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("similar_case_${similarCase.caseId}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Case ID and Similarity Score
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
                        text = "Case #${similarCase.patientId.takeLast(4)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    if (similarCase.isDemo) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = "Synthetic",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Similarity Pill
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xFFCCFBF1)
                ) {
                    Text(
                        text = "$simPercent% Similarity",
                        color = Color(0xFF0F766E),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Sub-header: Demographics and Risk
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Age: ${similarCase.age} • ${similarCase.sex} • Pain: ${similarCase.painScore}/10",
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )

                Text(
                    text = "Risk: ${similarCase.screeningRisk}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (similarCase.screeningRisk) {
                        "HIGH" -> Color(0xFFDC2626)
                        "MEDIUM" -> Color(0xFFD97706)
                        else -> Color(0xFF16A34A)
                    }
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Shared Patterns
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Shared feature patterns:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F766E)
                )
                similarCase.sharedPatterns.forEach { p ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("•", color = Color(0xFF0F766E), fontWeight = FontWeight.Bold)
                        Text(text = p, fontSize = 11.sp, color = Color(0xFF334155))
                    }
                }
            }

            // Differences if any
            if (similarCase.differences.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Notable differences:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    similarCase.differences.forEach { d ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("–", color = Color(0xFF64748B))
                            Text(text = d, fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }

            // Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onOpenCase,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F766E),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("View Case Details", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
