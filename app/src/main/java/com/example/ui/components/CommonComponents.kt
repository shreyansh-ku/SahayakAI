package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.ScreeningEntity
import com.example.ui.Screen
import com.example.ui.theme.*

/**
 * Geometric Balance clinical disclaimer strip.
 */
@Composable
fun ClinicalDisclaimerBanner(
    modifier: Modifier = Modifier,
    title: String = "Triage Advisory:"
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("disclaimer_banner"),
        color = ClinicalSurfaceContainerLowest,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, ClinicalOutlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BluePillBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.VerifiedUser,
                    contentDescription = "Clinical Advisory",
                    tint = ClinicalPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$title Screening result — not a diagnosis.",
                    style = MaterialTheme.typography.titleSmall,
                    color = ClinicalOnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Clinical validation is required before deployment. Field decision support only.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ClinicalOutline,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * Geometric Balance Navigation Bar
 * Matching: Clean white bar, w-10 h-10 rounded-full back container, uppercase tracking subtitle, bold title, pill badge.
 */
@Composable
fun ClinicalHeader(
    title: String = "Screening Result",
    subtitle: String = "Assessment",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    isOnline: Boolean = false,
    unsyncedCount: Int = 0,
    onToggleNetwork: () -> Unit = {},
    badgeText: String? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("clinical_header"),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (showBackButton) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, ClinicalOutlineSubtle, CircleShape)
                                .clickable { onBackClick() }
                                .testTag("btn_back"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = ClinicalOnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ClinicalPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Biotech,
                                contentDescription = "OrthoScreen Emblem",
                                tint = ClinicalOnPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = subtitle.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = ClinicalOutline,
                            letterSpacing = 1.2.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ClinicalOnSurface,
                            fontSize = 17.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ID Pill or Network Badge
                    if (badgeText != null) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = BluePillBg,
                            border = BorderStroke(1.dp, BluePillBorder)
                        ) {
                            Text(
                                text = badgeText,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BluePillText
                            )
                        }
                    } else {
                        // Network Offline/Online Toggle Button for SIH Field Demo
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { onToggleNetwork() }
                                .testTag("network_toggle_badge"),
                            color = if (isOnline) BluePillBg else AmberPillBg,
                            border = BorderStroke(
                                1.dp,
                                if (isOnline) BluePillBorder else AmberPillBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isOnline) ClinicalSecondary else ClinicalTertiary)
                                )
                                Text(
                                    text = if (isOnline) "ONLINE" else "OFFLINE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOnline) BluePillText else AmberPillText
                                )
                            }
                        }
                    }

                    // Worker Avatar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .border(1.dp, ClinicalOutlineVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Worker Profile",
                            tint = ClinicalOutline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Clean 1px bottom border
            HorizontalDivider(
                thickness = 1.dp,
                color = ClinicalOutlineSubtle
            )
        }
    }
}

/**
 * Geometric Balance Bottom Navigation Bar
 * Matching: h-16 flex items-center justify-around bg-white border-t border-slate-100
 */
@Composable
fun ClinicalBottomBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    unsyncedCount: Int = 0
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bottom_nav_bar"),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(1.dp, ClinicalOutlineSubtle)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier
                .height(64.dp)
                .navigationBarsPadding()
        ) {
            NavigationBarItem(
                selected = currentScreen is Screen.Dashboard,
                onClick = { onNavigate(Screen.Dashboard) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home"
                    )
                },
                label = { Text("HOME", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ClinicalPrimary,
                    selectedTextColor = ClinicalPrimary,
                    unselectedIconColor = ClinicalOutline,
                    unselectedTextColor = ClinicalOutline,
                    indicatorColor = BluePillBg
                )
            )

            NavigationBarItem(
                selected = false,
                onClick = { onNavigate(Screen.Dashboard) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Patients"
                    )
                },
                label = { Text("PATIENTS", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ClinicalPrimary,
                    selectedTextColor = ClinicalPrimary,
                    unselectedIconColor = ClinicalOutline,
                    unselectedTextColor = ClinicalOutline,
                    indicatorColor = BluePillBg
                )
            )

            NavigationBarItem(
                selected = currentScreen is Screen.Registration || currentScreen is Screen.Questionnaire || currentScreen is Screen.MovementAssessment,
                onClick = { onNavigate(Screen.Registration) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ClinicalPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Case",
                            tint = ClinicalOnPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                label = { Text("NEW", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ClinicalPrimary,
                    selectedTextColor = ClinicalPrimary,
                    unselectedIconColor = ClinicalPrimary,
                    unselectedTextColor = ClinicalPrimary,
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = currentScreen is Screen.SyncCenter,
                onClick = { onNavigate(Screen.SyncCenter) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (unsyncedCount > 0) {
                                Badge(containerColor = ClinicalTertiary) {
                                    Text("$unsyncedCount", color = Color.White)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History / Sync"
                        )
                    }
                },
                label = { Text("HISTORY", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ClinicalPrimary,
                    selectedTextColor = ClinicalPrimary,
                    unselectedIconColor = ClinicalOutline,
                    unselectedTextColor = ClinicalOutline,
                    indicatorColor = BluePillBg
                )
            )
        }
    }
}

/**
 * Printable Thermal Patient Referral Slip Dialog
 */
@Composable
fun ThermalReferralSlipDialog(
    screening: ScreeningEntity,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFFFFFFF),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("thermal_slip_dialog"),
            border = BorderStroke(1.dp, ClinicalOutlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Slip Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PRIMARY HEALTH CENTRE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ClinicalOnSurface
                        )
                        Text(
                            text = "OrthoScreen Clinical Referral Slip",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ClinicalPrimary
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = BluePillBg,
                        border = BorderStroke(1.dp, BluePillBorder)
                    ) {
                        Text(
                            text = screening.patientId,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BluePillText
                        )
                    }
                }

                HorizontalDivider(color = ClinicalOutlineSubtle)

                // Patient Information
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("PATIENT NAME", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = ClinicalOutline)
                        Text(screening.patientName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("Age / Sex: ${screening.age} Y / ${screening.sex}", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("VILLAGE", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = ClinicalOutline)
                        Text(screening.village, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("Date: ${screening.formattedDate}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Risk Badge in Geometric Container
                val (riskBg, riskText) = when (screening.riskLevel) {
                    "HIGH" -> ClinicalErrorContainer to ClinicalOnErrorContainer
                    "MEDIUM" -> AmberPillBg to AmberPillText
                    else -> GreenPillBg to GreenPillText
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = riskBg,
                    border = BorderStroke(1.dp, if (screening.riskLevel == "HIGH") ClinicalErrorBorder else Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "POTENTIAL SCREENING RISK",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = riskText
                        )
                        Text(
                            text = screening.riskLevel + " RISK",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = riskText
                        )
                        Text(
                            text = "* SCREENING RESULT — NOT A MEDICAL DIAGNOSIS",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = riskText.copy(alpha = 0.8f)
                        )
                    }
                }

                // Biomechanical & Symptom Findings
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ASSESSMENT SUMMARY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ClinicalOutline)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("5xSTS Duration:", style = MaterialTheme.typography.bodySmall)
                        Text("${screening.stsTimeSec}s", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Knee ROM:", style = MaterialTheme.typography.bodySmall)
                        Text("${screening.kneeRom}° Flexion", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Symptom Severity (WOMAC):", style = MaterialTheme.typography.bodySmall)
                        Text("${screening.questionnaireTotal} / 28", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }

                // Referral Action
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BluePillBg,
                    border = BorderStroke(1.dp, BluePillBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("RECOMMENDED PATHWAY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = BluePillText)
                        Text(screening.counselingSummary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        Text("Referral To: ${screening.referralDestination}", style = MaterialTheme.typography.bodySmall, color = ClinicalOutline)
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ClinicalPrimary)
                ) {
                    Text("Close Slip", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
