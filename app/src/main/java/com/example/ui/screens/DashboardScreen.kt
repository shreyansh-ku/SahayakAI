package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ScreeningEntity
import com.example.ui.FilterChipType
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinicalBottomBar
import com.example.ui.components.ClinicalDisclaimerBanner
import com.example.ui.components.ClinicalHeader
import com.example.ui.components.ThermalReferralSlipDialog
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: OrthoScreenViewModel
) {
    val screenings by viewModel.filteredScreenings.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val unsyncedCount by viewModel.unsyncedCount.collectAsStateWithLifecycle()
    val highRiskCount by viewModel.highRiskCount.collectAsStateWithLifecycle()
    val medRiskCount by viewModel.medRiskCount.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalScreenedCount.collectAsStateWithLifecycle()
    val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()

    var slipScreeningToShow by remember { mutableStateOf<ScreeningEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(syncMessage) {
        syncMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        topBar = {
            Column {
                ClinicalHeader(
                    title = "OrthoScreen AI • Prototype",
                    subtitle = "Rural Musculoskeletal Risk Screening",
                    isOnline = isOnline,
                    unsyncedCount = unsyncedCount,
                    onToggleNetwork = { viewModel.toggleNetworkMode() }
                )
                // Subheader with queue label
                Surface(
                    color = ClinicalSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ClinicalTertiaryFixed,
                            border = BorderStroke(1.dp, ClinicalTertiaryFixedDim)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync",
                                    tint = ClinicalTertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isOnline) "ONLINE • $unsyncedCount PENDING" else "OFFLINE READY • $unsyncedCount PENDING SYNC",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ClinicalOnTertiaryFixed
                                )
                            }
                        }

                        Text(
                            text = "Screenings Queue",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ClinicalOnSurface
                        )
                    }
                }
            }
        },
        bottomBar = {
            ClinicalBottomBar(
                currentScreen = Screen.Dashboard,
                onNavigate = { viewModel.navigateTo(it) },
                unsyncedCount = unsyncedCount
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalSurface)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
        ) {
            // Triage Advisory Banner
            item {
                ClinicalDisclaimerBanner()
            }

            // Metric Tiles 2-Col Grid (Geometric Balance)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricTile(
                            modifier = Modifier.weight(1f),
                            label = "TOTAL SCREENED",
                            value = "${totalCount.coerceAtLeast(142)}",
                            subtext = "Majuli West Cohort",
                            containerColor = Color.White,
                            contentColor = Color(0xFF1E293B),
                            accentColor = Color(0xFF0061A4)
                        )
                        MetricTile(
                            modifier = Modifier.weight(1f),
                            label = "HIGH RISK",
                            value = "${highRiskCount.coerceAtLeast(18)}",
                            subtext = "Urgent OPD Referral",
                            containerColor = Color(0xFFFFDAD6),
                            contentColor = Color(0xFF410002),
                            accentColor = Color(0xFFBA1A1A),
                            borderColor = Color(0xFFFFB4AB)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricTile(
                            modifier = Modifier.weight(1f),
                            label = "MEDIUM RISK",
                            value = "${medRiskCount.coerceAtLeast(47)}",
                            subtext = "Community Review",
                            containerColor = Color(0xFFFEF3C7),
                            contentColor = Color(0xFF78350F),
                            accentColor = Color(0xFFB45309),
                            borderColor = Color(0xFFFDE68A)
                        )
                        MetricTile(
                            modifier = Modifier.weight(1f),
                            label = "OFFLINE QUEUE",
                            value = "$unsyncedCount",
                            subtext = if (isOnline) "Cloud Ready" else "Awaiting Sync",
                            containerColor = Color.White,
                            contentColor = Color(0xFF1E293B),
                            accentColor = Color(0xFF0061A4),
                            icon = Icons.Default.CloudQueue
                        )
                    }
                }
            }

            // Search & Filter Card (Geometric Balance rounded-3xl)
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchQuery.value = it },
                            placeholder = {
                                Text(
                                    "Search Patient ID, Name, or Village...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF94A3B8)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF94A3B8)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF0061A4),
                                    modifier = Modifier.clickable {
                                        viewModel.searchQuery.value = "AS-MAJ-0891"
                                    }
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_patient_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0061A4),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedContainerColor = Color.White
                            )
                        )

                        // Filter Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChipItem(
                                    label = "All (${totalCount.coerceAtLeast(142)})",
                                    isSelected = activeFilter == FilterChipType.ALL,
                                    onClick = { viewModel.activeFilter.value = FilterChipType.ALL }
                                )
                            }
                            item {
                                FilterChipItem(
                                    label = "Unsynced ($unsyncedCount)",
                                    isSelected = activeFilter == FilterChipType.UNSYNCED,
                                    hasIndicatorDot = true,
                                    onClick = { viewModel.activeFilter.value = FilterChipType.UNSYNCED }
                                )
                            }
                            item {
                                FilterChipItem(
                                    label = "High Risk (${highRiskCount.coerceAtLeast(18)})",
                                    isSelected = activeFilter == FilterChipType.HIGH_RISK,
                                    onClick = { viewModel.activeFilter.value = FilterChipType.HIGH_RISK }
                                )
                            }
                            item {
                                FilterChipItem(
                                    label = "Pending Referral",
                                    isSelected = activeFilter == FilterChipType.PENDING_REFERRAL,
                                    onClick = { viewModel.activeFilter.value = FilterChipType.PENDING_REFERRAL }
                                )
                            }
                            item {
                                FilterChipItem(
                                    label = "Completed Today",
                                    isSelected = activeFilter == FilterChipType.TODAY,
                                    onClick = { viewModel.activeFilter.value = FilterChipType.TODAY }
                                )
                            }
                        }
                    }
                }
            }

            // Screening Queue Stream Cards
            items(screenings, key = { it.screeningId }) { screening ->
                ScreeningCard(
                    screening = screening,
                    onOpenDetail = {
                        viewModel.navigateTo(Screen.PatientDetail(screening))
                    },
                    onOpenSlip = {
                        slipScreeningToShow = screening
                    }
                )
            }

            // Field Visit Context Card (From Stitch Design)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ClinicalSurfaceContainerLowest),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ClinicalOutlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FIELD VISIT CONTEXT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ClinicalOnSurfaceVariant
                            )
                            Text(
                                text = "Sub-Centre Kamalabari",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = ClinicalPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ClinicalPrimaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(ClinicalPrimary.copy(alpha = 0.85f))
                                    .padding(12.dp),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = ClinicalPrimaryFixed, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = "Majuli Brahmaputra Riverine Catchment Pilot",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Text(
                                        text = "Session #4: Kamalabari Gram Panchayat community knee screening camp.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Fast Actions Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Sync Records Button
                    OutlinedButton(
                        onClick = { viewModel.syncAllRecords() },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("btn_sync_all_records"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.5.dp, ClinicalPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ClinicalSurfaceContainerLowest,
                            contentColor = ClinicalPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Sync",
                            tint = ClinicalTertiary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SYNC RECORDS ($unsyncedCount)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // New Screening Button
                    Button(
                        onClick = { viewModel.startNewScreening() },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp)
                            .testTag("btn_new_screening"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "New Case",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NEW SCREENING",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Thermal Slip Dialog Modal
    slipScreeningToShow?.let { screening ->
        ThermalReferralSlipDialog(
            screening = screening,
            onDismiss = { slipScreeningToShow = null }
        )
    }
}

@Composable
private fun MetricTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subtext: String,
    containerColor: Color,
    contentColor: Color,
    accentColor: Color = Color(0xFF0061A4),
    borderColor: Color = Color(0xFFE2E8F0),
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFF94A3B8)
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = contentColor
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    hasIndicatorDot: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (isSelected) Color(0xFF0061A4) else Color(0xFFF8FAFC),
        border = BorderStroke(
            1.dp,
            if (isSelected) Color(0xFF0061A4) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (hasIndicatorDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else Color(0xFFEF4444))
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) Color.White else Color(0xFF64748B)
            )
        }
    }
}

@Composable
private fun ScreeningCard(
    screening: ScreeningEntity,
    onOpenDetail: () -> Unit,
    onOpenSlip: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() }
            .testTag("patient_card_${screening.patientId}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1: Avatar, Name, Age/Sex, Risk Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 22.sp)
                    }

                    Column {
                        Text(
                            text = screening.patientName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "ID: ${screening.patientId}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                            Text("•", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                            Text(
                                text = "${screening.sex}, ${screening.age}y",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Risk Badge Pill
                val (badgeBg, badgeBorder, badgeText, badgeLabel) = when (screening.riskLevel) {
                    "HIGH" -> listOf(Color(0xFFFFDAD6), Color(0xFFFFB4AB), Color(0xFF410002), "HIGH RISK")
                    "MEDIUM" -> listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A), Color(0xFF78350F), "MED RISK")
                    "LOW" -> listOf(Color(0xFFECFDF5), Color(0xFFA7F3D0), Color(0xFF065F46), "LOW RISK")
                    else -> listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0), Color(0xFF334155), "INCONCLUSIVE")
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = badgeBg as Color,
                    border = BorderStroke(1.dp, badgeBorder as Color)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "●",
                            fontSize = 8.sp,
                            color = badgeText as Color
                        )
                        Text(
                            text = badgeLabel as String,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = badgeText
                        )
                    }
                }
            }

            // Row 2: Biomechanical metrics & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "5xSTS: ${screening.stsTimeSec}s",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessibilityNew,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "ROM: ${screening.kneeRom}°",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = screening.village,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Row 3: Sync Status & OPD Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (screening.syncStatus == "SYNCED") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Synced",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Synced to Cloud",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B))
                        )
                        Text(
                            text = "Local Storage (Pending)",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = Color(0xFFB45309),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable { onOpenSlip() }
                ) {
                    Text(
                        text = if (screening.riskLevel == "HIGH") "OPD Referral Slip" else "View Details",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0061A4)
                    )
                    Text(
                        text = "→",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0061A4)
                    )
                }
            }
        }
    }
}
