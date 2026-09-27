package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinix.model.ActiveModule
import com.example.clinix.model.ReviewStatus
import com.example.ui.Screen

@Composable
fun ModuleSwitcher(
    activeModule: ActiveModule,
    onSwitchModule: (ActiveModule) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("module_switcher"),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF1F5F9),
        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // OrthoScreen AI Option
            val isOrthoSelected = activeModule == ActiveModule.ORTHOSCREEN
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSwitchModule(ActiveModule.ORTHOSCREEN) }
                    .testTag("btn_switch_orthoscreen"),
                color = if (isOrthoSelected) Color(0xFF0061A4) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = if (isOrthoSelected) Color.White else Color(0xFF475569),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "OrthoScreen",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOrthoSelected) Color.White else Color(0xFF1E293B)
                        )
                        Text(
                            text = "Field Screening",
                            fontSize = 10.sp,
                            color = if (isOrthoSelected) Color(0xFFD1E4FF) else Color(0xFF64748B)
                        )
                    }
                }
            }

            // ClinixAI Option
            val isClinixSelected = activeModule == ActiveModule.CLINIXAI
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSwitchModule(ActiveModule.CLINIXAI) }
                    .testTag("btn_switch_clinixai"),
                color = if (isClinixSelected) Color(0xFF0F766E) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = if (isClinixSelected) Color.White else Color(0xFF475569),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "ClinixAI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isClinixSelected) Color.White else Color(0xFF1E293B)
                        )
                        Text(
                            text = "Clinical Review",
                            fontSize = 10.sp,
                            color = if (isClinixSelected) Color(0xFFCCFBF1) else Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClinixHeader(
    title: String = "ClinixAI",
    subtitle: String = "Clinical Review",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    activeModule: ActiveModule = ActiveModule.CLINIXAI,
    onSwitchModule: (ActiveModule) -> Unit = {},
    isOnline: Boolean = true,
    badgeText: String? = null
) {
    Surface(
        color = Color(0xFF042F2E),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF134E4A))
                                .testTag("btn_clinix_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0D9488)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.3).sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF14B8A6)
                            ) {
                                Text(
                                    text = "CLINICAL",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = subtitle,
                            color = Color(0xFF99F6E4),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Connectivity Pill
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (isOnline) Color(0xFF0F766E) else Color(0xFF7F1D1D)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) Color(0xFF2DD4BF) else Color(0xFFF87171))
                            )
                            Text(
                                text = if (isOnline) "ONLINE" else "FIELD OFFLINE",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Module Quick Switcher Button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onSwitchModule(
                                    if (activeModule == ActiveModule.CLINIXAI) ActiveModule.ORTHOSCREEN
                                    else ActiveModule.CLINIXAI
                                )
                            }
                            .testTag("btn_quick_switch_module"),
                        color = Color(0xFF134E4A),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch Module",
                                tint = Color(0xFF5EEAD4),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Field",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (badgeText != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF134E4A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = badgeText,
                        color = Color(0xFFCCFBF1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClinixBottomBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClinixNavItem(
                title = "Overview",
                icon = Icons.Default.Assessment,
                isSelected = currentScreen is Screen.ClinixOverview,
                onClick = { onNavigate(Screen.ClinixOverview) },
                tag = "tab_clinix_overview"
            )

            ClinixNavItem(
                title = "Cases",
                icon = Icons.Default.Assignment,
                isSelected = currentScreen is Screen.ClinixCases || currentScreen is Screen.ClinixCaseDetail,
                onClick = { onNavigate(Screen.ClinixCases) },
                tag = "tab_clinix_cases"
            )

            ClinixNavItem(
                title = "Cohorts",
                icon = Icons.Default.BubbleChart,
                isSelected = currentScreen is Screen.ClinixCohorts || currentScreen is Screen.ClinixCohortDetail,
                onClick = { onNavigate(Screen.ClinixCohorts) },
                tag = "tab_clinix_cohorts"
            )

            ClinixNavItem(
                title = "Outliers",
                icon = Icons.Default.Flare,
                isSelected = currentScreen is Screen.ClinixOutliers,
                onClick = { onNavigate(Screen.ClinixOutliers) },
                tag = "tab_clinix_outliers"
            )

            ClinixNavItem(
                title = "Profile",
                icon = Icons.Default.Person,
                isSelected = currentScreen is Screen.ClinixProfile,
                onClick = { onNavigate(Screen.ClinixProfile) },
                tag = "tab_clinix_profile"
            )
        }
    }
}

@Composable
private fun ClinixNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (isSelected) Color(0xFFCCFBF1) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color(0xFF0F766E) else Color(0xFF64748B),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF0F766E) else Color(0xFF64748B)
        )
    }
}

@Composable
fun ReviewStatusBadge(
    status: ReviewStatus,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = Color(status.badgeBgHex)
    ) {
        Text(
            text = status.label,
            color = Color(status.badgeTextHex),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
fun ClinixSafetyDisclaimer(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("clinix_safety_disclaimer"),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF0FDF4),
        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF15803D),
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = "Screening result — not a diagnosis.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF166534)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Similarity ≠ Diagnosis. AI assists clinical review; final clinical decisions remain with the clinician.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF14532D)
                )
            }
        }
    }
}
