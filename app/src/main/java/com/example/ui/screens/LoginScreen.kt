package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.OrthoScreenViewModel
import com.example.ui.components.ClinicalDisclaimerBanner
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: OrthoScreenViewModel
) {
    val scrollState = rememberScrollState()
    var workerId by remember { mutableStateOf(viewModel.screenerId) }
    var selectedRole by remember { mutableStateOf(viewModel.screenerRole) }
    var selectedSite by remember { mutableStateOf(viewModel.fieldSite) }
    var pinDigits by remember { mutableStateOf(listOf("1", "2", "3", "4")) }
    var langDropdownExpanded by remember { mutableStateOf(false) }
    var siteDropdownExpanded by remember { mutableStateOf(false) }
    var showScanSimModal by remember { mutableStateOf(false) }
    var showResetPinNotice by remember { mutableStateOf(false) }

    val languages = listOf("অসমীয়া / EN", "English", "বাংলা", "हिन्दी")
    val sites = listOf(
        "Majuli Field Evaluation Site • Catchment Alpha",
        "Garmur Riverine Pilot Station • Zone 1",
        "Bokakhat Community Screening Unit • Zone 2",
        "Titabar Field Test Station • Zone 3",
        "Karbi Anglong Mobile Pilot Unit"
    )

    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0061A4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Biotech,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "ORTHOSCREEN AI",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = Color(0xFF0061A4)
                            )
                            Text(
                                text = "Rural Musculoskeletal Screening",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Language Selector Pill
                    Box {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .clickable { langDropdownExpanded = true }
                                .testTag("btn_language_picker")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Language",
                                    tint = Color(0xFF0061A4),
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = viewModel.selectedLanguage,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = langDropdownExpanded,
                            onDismissRequest = { langDropdownExpanded = false }
                        ) {
                            languages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang) },
                                    onClick = {
                                        viewModel.setLanguage(lang)
                                        langDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.screenerId = workerId
                            viewModel.screenerRole = selectedRole
                            viewModel.fieldSite = selectedSite
                            viewModel.navigateTo(Screen.Dashboard)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("btn_login_submit"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Authenticate & Enter Portal",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Proceed",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card (Geometric Balance: rounded-3xl)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Biotech,
                                    contentDescription = "Emblem",
                                    tint = Color(0xFF0061A4),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "OrthoScreen AI",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Rural Musculoskeletal Risk Screening",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Text(
                                text = "v2.4.1 • NER",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    // Local Offline Auth Notice
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDBEAFE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = "Offline",
                                    tint = Color(0xFF1D4ED8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "LOCAL OFFLINE AUTH ENABLED",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp,
                                        color = Color(0xFF1D4ED8)
                                    )
                                    Text(
                                        text = "14 Cases In Local Queue",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF2563EB)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Device is fully operational without cellular connectivity. All screenings encrypt and persist to local Room DB.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }

                    // Disclaimer Strip
                    ClinicalDisclaimerBanner()
                }
            }

            // Authentication Form Card (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Worker ID + QR Scan
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "1. FIELD SCREENER ID / TEST ACCOUNT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = workerId,
                                onValueChange = { workerId = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_worker_id"),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = "Badge",
                                        tint = Color(0xFF0061A4)
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF0061A4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    unfocusedContainerColor = Color(0xFFF8FAFC),
                                    focusedContainerColor = Color.White
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .height(56.dp)
                                    .clickable { showScanSimModal = true }
                                    .testTag("btn_scan_qr")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "Scan QR",
                                        tint = Color(0xFF0061A4),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        "SCAN",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF1E293B),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Prefilled from local evaluation unit configuration.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // 2. Clinical Operational Role (Geometric Balance: Pills)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "2. CLINICAL OPERATIONAL ROLE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val roles = listOf(
                                "Field Screener" to Icons.Default.VolunteerActivism,
                                "Clinical Nurse" to Icons.Default.Assignment,
                                "Study Lead" to Icons.Default.MedicalServices
                            )
                            roles.forEach { (role, icon) ->
                                val isSelected = selectedRole == role
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(999.dp))
                                        .clickable { selectedRole = role }
                                        .testTag("role_btn_$role"),
                                    color = if (isSelected) Color(0xFF0061A4) else Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = role,
                                            tint = if (isSelected) Color.White else Color(0xFF64748B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = role,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else Color(0xFF334155),
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Assigned Field Evaluation Site
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "3. ASSIGNED FIELD EVALUATION SITE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Box {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                    .clickable { siteDropdownExpanded = true }
                                    .testTag("selector_field_site"),
                                color = Color(0xFFF8FAFC)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Domain,
                                            contentDescription = "Site",
                                            tint = Color(0xFF0061A4)
                                        )
                                        Text(
                                            text = selectedSite,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF1E293B),
                                            maxLines = 1
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        tint = Color(0xFF64748B)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = siteDropdownExpanded,
                                onDismissRequest = { siteDropdownExpanded = false }
                            ) {
                                sites.forEach { site ->
                                    DropdownMenuItem(
                                        text = { Text(site) },
                                        onClick = {
                                            selectedSite = site
                                            siteDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Location",
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Assigned to Majuli Pilot Field Cohort",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF059669)
                            )
                        }
                    }

                    // 4. Quick Access Security PIN
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "4. QUICK ACCESS SECURITY PIN",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Reset PIN",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF0061A4),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showResetPinNotice = true }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // 4 PIN Boxes (Rounded-2xl)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                for (i in 0..3) {
                                    Surface(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(16.dp)),
                                        color = Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "•",
                                                style = MaterialTheme.typography.headlineMedium,
                                                color = Color(0xFF0061A4),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // Device Passcode Button
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                modifier = Modifier
                                    .clickable {
                                        pinDigits = listOf("1", "2", "3", "4")
                                    }
                                    .testTag("btn_passcode_auth")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Password,
                                        contentDescription = "Passcode",
                                        tint = Color(0xFF1D4ED8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "PASSCODE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Enter your 4-digit field evaluation PIN or use device passcode.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // Field Screening Readiness Checklist (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "FIELD SCREENING READINESS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE0F2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sensors,
                                        contentDescription = "Camera",
                                        tint = Color(0xFF0061A4),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        "Camera Calibrated",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text("Sensors Ready", style = MaterialTheme.typography.labelSmall, color = Color(0xFF059669))
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDCFCE7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BatteryChargingFull,
                                        contentDescription = "Battery",
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        "Power 84%",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text("Optimal for Camp", style = MaterialTheme.typography.labelSmall, color = Color(0xFF15803D))
                                }
                            }
                        }
                    }
                }
            }

            // Footer Compliance
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Compliance",
                        tint = ClinicalOutline,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "PROTOTYPE FOR COMMUNITY RISK SCREENING • FIELD EVALUATION ONLY",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClinicalOutline,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = "Last Local Sync: Today, 07:15 AM (Field Server) • Unit ID: EVAL-772",
                    style = MaterialTheme.typography.bodySmall,
                    color = ClinicalOnSurfaceVariant
                )
            }
        }
    }

    // QR Scanner Simulation Modal
    if (showScanSimModal) {
        AlertDialog(
            onDismissRequest = { showScanSimModal = false },
            title = { Text("Scan Field Tester Card") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Hold the QR code on your field evaluation ID card steadily in view.")
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ClinicalSurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR Viewfinder",
                            tint = ClinicalPrimary,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        workerId = "ASHA-MJL-89104"
                        showScanSimModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinicalPrimary)
                ) {
                    Text("Simulate Scan (ASHA-MJL-89104)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showScanSimModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // PIN Reset guidance
    if (showResetPinNotice) {
        AlertDialog(
            onDismissRequest = { showResetPinNotice = false },
            title = { Text("Field PIN Override Protocol") },
            text = {
                Text("Contact your District Nodal Officer at Jorhat Health Complex for immediate biometric or PIN override. Emergency Offline Code is available from your Primary Center Supervisor.")
            },
            confirmButton = {
                Button(
                    onClick = { showResetPinNotice = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinicalPrimary)
                ) {
                    Text("Understood")
                }
            }
        )
    }
}
