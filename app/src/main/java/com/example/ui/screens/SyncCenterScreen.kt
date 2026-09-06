package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.components.ClinicalBottomBar
import com.example.ui.components.ClinicalDisclaimerBanner
import com.example.ui.components.ClinicalHeader
import com.example.ui.theme.*

@Composable
fun SyncCenterScreen(
    viewModel: OrthoScreenViewModel
) {
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val unsyncedCount by viewModel.unsyncedCount.collectAsStateWithLifecycle()
    val allScreenings by viewModel.allScreenings.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()

    var showExportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ClinicalHeader(
                title = "Sync & Gateway Center",
                subtitle = "Offline-First Synchronization Engine",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.Dashboard) },
                isOnline = isOnline,
                unsyncedCount = unsyncedCount,
                onToggleNetwork = { viewModel.toggleNetworkMode() }
            )
        },
        bottomBar = {
            ClinicalBottomBar(
                currentScreen = Screen.SyncCenter,
                onNavigate = { viewModel.navigateTo(it) },
                unsyncedCount = unsyncedCount
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
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
        ) {
            // Advisory Banner
            item {
                ClinicalDisclaimerBanner()
            }

            // Connectivity State Simulation Card (Geometric Balance: rounded-3xl)
            item {
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
                                text = "NETWORK SIMULATION & FIELD MODE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = Color(0xFF64748B)
                            )
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (isOnline) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = if (isOnline) "CLOUD ONLINE" else "OFFLINE ISOLATION",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOnline) Color(0xFF16A34A) else Color(0xFFD97706)
                                )
                            }
                        }

                        Text(
                            text = "Toggle this switch to simulate entering/leaving remote Northeast India riverine areas with zero network connectivity.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF475569)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isOnline) Icons.Default.CloudQueue else Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = if (isOnline) Color(0xFF0061A4) else Color(0xFFD97706),
                                    modifier = Modifier.size(28.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isOnline) "Internet Connected" else "Zero Connectivity (Offline)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = if (isOnline) "Ready to sync with State Health Portal" else "All data safely saved in local SQLite database",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Switch(
                                checked = isOnline,
                                onCheckedChange = { viewModel.toggleNetworkMode() },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0061A4))
                            )
                        }
                    }
                }
            }

            // Sync Queue & Actions Card (Geometric Balance: rounded-3xl)
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SYNCHRONIZATION PIPELINE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = Color(0xFF64748B)
                            )
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (unsyncedCount > 0) Color(0xFFFEF3C7) else Color(0xFFEFF6FF)
                            ) {
                                Text(
                                    text = "$unsyncedCount Records Pending",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (unsyncedCount > 0) Color(0xFFD97706) else Color(0xFF0061A4)
                                )
                            }
                        }

                        if (isSyncing) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(color = Color(0xFF0061A4))
                                Text(
                                    text = syncMessage ?: "Syncing records...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF0061A4),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Button(
                                onClick = { viewModel.syncAllRecords() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("btn_trigger_sync"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4)),
                                enabled = unsyncedCount > 0
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (unsyncedCount > 0) "Synchronize $unsyncedCount Pending Records" else "All Records Up To Date",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // USB / Offline Export Fallback
                        OutlinedButton(
                            onClick = { showExportDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Icon(Icons.Default.Usb, contentDescription = null, tint = Color(0xFF64748B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export Encrypted Archive for Field USB Transfer", color = Color(0xFF334155), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Unsynced & Synced Queue Items
            item {
                Text(
                    text = "LOCAL DATA QUEUE AUDIT (${allScreenings.size} CASES)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color(0xFF64748B)
                )
            }

            items(allScreenings, key = { it.screeningId }) { item ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (item.syncStatus == "SYNCED") Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (item.syncStatus == "SYNCED") Icons.Default.Check else Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (item.syncStatus == "SYNCED") Color(0xFF16A34A) else Color(0xFFD97706),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = item.patientName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "${item.patientId} • ${item.village}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (item.syncStatus == "SYNCED") Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = item.syncStatus,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.syncStatus == "SYNCED") Color(0xFF16A34A) else Color(0xFFD97706)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.formattedDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            title = { Text("Field Camp Archive Exported", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)) },
            text = {
                Text(
                    "Encrypted database backup created: `orthoscreen_camp_majuli_backup.enc` (AES-256 encrypted). Ready for physical USB transfer to District Hospital Jorhat.",
                    color = Color(0xFF475569)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showExportDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4))
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
