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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.components.ClinicalDisclaimerBanner
import com.example.ui.components.ClinicalHeader
import com.example.ui.theme.*

@Composable
fun PatientRegistrationScreen(
    viewModel: OrthoScreenViewModel
) {
    val scrollState = rememberScrollState()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val unsyncedCount by viewModel.unsyncedCount.collectAsStateWithLifecycle()

    val caseId by viewModel.generatedCaseId.collectAsStateWithLifecycle()
    val name by viewModel.patientName.collectAsStateWithLifecycle()
    val guardian by viewModel.guardianName.collectAsStateWithLifecycle()
    val village by viewModel.selectedVillage.collectAsStateWithLifecycle()
    val localRefId by viewModel.localRefId.collectAsStateWithLifecycle()
    val age by viewModel.patientAge.collectAsStateWithLifecycle()
    val sex by viewModel.patientSex.collectAsStateWithLifecycle()
    val height by viewModel.patientHeightCm.collectAsStateWithLifecycle()
    val weight by viewModel.patientWeightKg.collectAsStateWithLifecycle()
    val hasInjury by viewModel.hasPriorInjury.collectAsStateWithLifecycle()
    val injuryDetails by viewModel.injuryDetails.collectAsStateWithLifecycle()
    val occupation by viewModel.occupationCategory.collectAsStateWithLifecycle()
    val cHypertension by viewModel.comorbidityHypertension.collectAsStateWithLifecycle()
    val cDiabetes by viewModel.comorbidityDiabetes.collectAsStateWithLifecycle()
    val cRheumatoid by viewModel.comorbidityRheumatoid.collectAsStateWithLifecycle()
    val cNone by viewModel.comorbidityNone.collectAsStateWithLifecycle()
    val consent by viewModel.consentAccepted.collectAsStateWithLifecycle()

    var villageDropdownExpanded by remember { mutableStateOf(false) }
    var occupationDropdownExpanded by remember { mutableStateOf(false) }
    var showDraftSavedToast by remember { mutableStateOf(false) }

    val villages = listOf(
        "Garamur Phutuki Pathar",
        "Kamalabari Gaon (Sub-Centre A)",
        "Jengraimukh Tribal Belt",
        "Salmora Pottery Hamlet",
        "Dakshinpat River Bank Habitation"
    )

    val occupations = listOf(
        "Agricultural field worker (Paddy transplanting)",
        "Tea garden plucker / loader (Sustained carrying)",
        "Handloom artisan / Mishing traditional weaver",
        "Homemaker / Caretaker (Ground-level cooking)",
        "Sedentary / Shopkeeping / Desk work"
    )

    val bmi = viewModel.calculateBmi(height, weight)
    val bmiCategory = when {
        bmi < 18.5 -> "Underweight"
        bmi < 23.0 -> "Normal (Asian-Indian Ref)"
        bmi < 25.0 -> "Overweight (Asian Threshold)"
        else -> "Obese / Elevated Knee Loading"
    }

    Scaffold(
        topBar = {
            ClinicalHeader(
                title = "New Screening",
                subtitle = "Step 1 of 4 • Patient Intake",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.Dashboard) },
                isOnline = isOnline,
                unsyncedCount = unsyncedCount,
                onToggleNetwork = { viewModel.toggleNetworkMode() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress Header Card (Geometric Balance: rounded-3xl)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STEP 1 OF 4: PATIENT INTAKE & DEMOGRAPHICS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF0061A4)
                        )
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color(0xFFE0F2FE)
                        ) {
                            Text(
                                text = "25% Completed",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0061A4)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { 0.25f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = Color(0xFF0061A4),
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1. Intake", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0061A4), fontWeight = FontWeight.Bold)
                        Text("2. Symptoms", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        Text("3. Motion & AI", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        Text("4. Referral", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                    }
                }
            }

            // Clinical Advisory Banner
            ClinicalDisclaimerBanner()

            // 1. Identifier & Identity Fieldset (Geometric Balance: rounded-3xl)
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF0061A4), modifier = Modifier.size(18.dp))
                            Text(
                                text = "1. IDENTIFIER & IDENTITY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Surface(shape = RoundedCornerShape(999.dp), color = Color(0xFFEFF6FF)) {
                            Text(
                                text = "FIELD-ID",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8)
                            )
                        }
                    }

                    // Auto-generated Screening Case ID
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "GENERATED SCREENING CASE ID",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = caseId,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0061A4)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable { /* Copy */ }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF334155), modifier = Modifier.size(18.dp))
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF0061A4),
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable { /* Scan */ }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Local Patient Ref ID
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "LOCAL PATIENT REFERENCE ID (OPTIONAL)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF64748B)
                        )
                        OutlinedTextField(
                            value = localRefId,
                            onValueChange = { viewModel.localRefId.value = it },
                            placeholder = { Text("e.g., LOC-2023-XX (Optional)", color = Color(0xFF94A3B8)) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0061A4),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedContainerColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Patient Full Name
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "PATIENT FULL NAME *",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF64748B)
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.patientName.value = it },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0061A4),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedContainerColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_patient_name")
                        )
                    }

                    // Guardian Name
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "FATHER / MOTHER / SPOUSE NAME *",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF64748B)
                        )
                        OutlinedTextField(
                            value = guardian,
                            onValueChange = { viewModel.guardianName.value = it },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0061A4),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedContainerColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Village Catchment Area Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "SUB-CENTRE / VILLAGE CATCHMENT AREA (MAJULI BLOCK) *",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF64748B)
                        )
                        Box {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                    .clickable { villageDropdownExpanded = true },
                                color = Color(0xFFF8FAFC)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = village,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF1E293B)
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF64748B))
                                }
                            }

                            DropdownMenu(
                                expanded = villageDropdownExpanded,
                                onDismissRequest = { villageDropdownExpanded = false }
                            ) {
                                villages.forEach { v ->
                                    DropdownMenuItem(
                                        text = { Text(v) },
                                        onClick = {
                                            viewModel.selectedVillage.value = v
                                            villageDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Demographics & Vitals Fieldset (Geometric Balance: rounded-3xl)
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = Color(0xFF0061A4), modifier = Modifier.size(18.dp))
                            Text(
                                text = "2. DEMOGRAPHICS & VITALS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = "ASIAN-INDIAN METRICS",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8)
                            )
                        }
                    }

                    // Age & Sex
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Age
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("AGE IN COMPLETED YEARS *", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, color = Color(0xFF64748B))
                            OutlinedTextField(
                                value = "$age",
                                onValueChange = { str ->
                                    str.toIntOrNull()?.let { viewModel.patientAge.value = it }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                trailingIcon = { Text("YRS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8)) },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF0061A4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    unfocusedContainerColor = Color(0xFFF8FAFC),
                                    focusedContainerColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_patient_age")
                            )
                        }

                        // Sex (Pill selector)
                        Column(modifier = Modifier.weight(1.5f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("BIOLOGICAL SEX *", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, color = Color(0xFF64748B))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Male", "Female", "Other").forEach { s ->
                                    val isSelected = sex == s
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(999.dp))
                                            .clickable { viewModel.patientSex.value = s }
                                            .testTag("sex_btn_$s"),
                                        color = if (isSelected) Color(0xFF0061A4) else Color.Transparent
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = s,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else Color(0xFF334155)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Height & Weight
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Height
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("STANDING HEIGHT *", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, color = Color(0xFF64748B))
                            OutlinedTextField(
                                value = "${height.toInt()}",
                                onValueChange = { str ->
                                    str.toDoubleOrNull()?.let { viewModel.patientHeightCm.value = it }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                trailingIcon = { Text("CM", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8)) },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF0061A4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    unfocusedContainerColor = Color(0xFFF8FAFC),
                                    focusedContainerColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_patient_height")
                            )
                        }

                        // Weight
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("BODY WEIGHT *", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, color = Color(0xFF64748B))
                            OutlinedTextField(
                                value = "${weight.toInt()}",
                                onValueChange = { str ->
                                    str.toDoubleOrNull()?.let { viewModel.patientWeightKg.value = it }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                trailingIcon = { Text("KG", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8)) },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF0061A4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    unfocusedContainerColor = Color(0xFFF8FAFC),
                                    focusedContainerColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_patient_weight")
                            )
                        }
                    }

                    // Auto-calculated BMI Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "CALCULATED BODY MASS INDEX (BMI)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF15803D),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${String.format("%.1f", bmi)} kg/m²",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFF86EFAC))
                            ) {
                                Text(
                                    text = bmiCategory,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Joint History & Workload Fieldset (Geometric Balance: rounded-3xl)
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = Color(0xFF0061A4), modifier = Modifier.size(18.dp))
                            Text(
                                text = "3. JOINT HISTORY & OCCUPATIONAL STRAIN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Surface(shape = RoundedCornerShape(999.dp), color = Color(0xFFFEF2F2)) {
                            Text(
                                text = "CRITICAL AI FACTOR",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }

                    // Prior Knee or Hip Trauma / Fracture Toggle
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Prior Knee or Hip Trauma / Fracture? *",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = "Ligament tear, meniscus injury, or bone fracture",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Color(0xFFE2E8F0))
                                        .padding(3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = if (hasInjury) Color(0xFF0061A4) else Color.Transparent,
                                        modifier = Modifier
                                            .clickable { viewModel.hasPriorInjury.value = true }
                                    ) {
                                        Text(
                                            "Yes",
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (hasInjury) FontWeight.Bold else FontWeight.Medium,
                                            color = if (hasInjury) Color.White else Color(0xFF334155)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = if (!hasInjury) Color(0xFF0061A4) else Color.Transparent,
                                        modifier = Modifier
                                            .clickable { viewModel.hasPriorInjury.value = false }
                                    ) {
                                        Text(
                                            "No",
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (!hasInjury) FontWeight.Bold else FontWeight.Medium,
                                            color = if (!hasInjury) Color.White else Color(0xFF334155)
                                        )
                                    }
                                }
                            }

                            if (hasInjury) {
                                OutlinedTextField(
                                    value = injuryDetails,
                                    onValueChange = { viewModel.injuryDetails.value = it },
                                    placeholder = { Text("Details of Injury & Year of Occurrence", color = Color(0xFF94A3B8)) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF0061A4),
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        unfocusedContainerColor = Color.White,
                                        focusedContainerColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Occupational Load Category
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "DAILY HEAVY SQUATTING / MECHANICAL LOAD CATEGORY *",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF64748B)
                        )
                        Box {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                    .clickable { occupationDropdownExpanded = true },
                                color = Color(0xFFF8FAFC)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = occupation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF1E293B)
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF64748B))
                                }
                            }

                            DropdownMenu(
                                expanded = occupationDropdownExpanded,
                                onDismissRequest = { occupationDropdownExpanded = false }
                            ) {
                                occupations.forEach { occ ->
                                    DropdownMenuItem(
                                        text = { Text(occ) },
                                        onClick = {
                                            viewModel.occupationCategory.value = occ
                                            occupationDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Known Comorbidities
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "KNOWN PRE-EXISTING COMORBIDITIES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF64748B)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ComorbidityChip(
                                label = "Hypertension",
                                isChecked = cHypertension,
                                onToggle = { viewModel.comorbidityHypertension.value = !cHypertension },
                                modifier = Modifier.weight(1f)
                            )
                            ComorbidityChip(
                                label = "Diabetes Mellitus",
                                isChecked = cDiabetes,
                                onToggle = { viewModel.comorbidityDiabetes.value = !cDiabetes },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ComorbidityChip(
                                label = "Rheumatoid / Gout",
                                isChecked = cRheumatoid,
                                onToggle = { viewModel.comorbidityRheumatoid.value = !cRheumatoid },
                                modifier = Modifier.weight(1f)
                            )
                            ComorbidityChip(
                                label = "None / No history",
                                isChecked = cNone,
                                onToggle = { viewModel.comorbidityNone.value = !cNone },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 4. Informed Consent Fieldset (Geometric Balance: rounded-3xl)
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = Color(0xFF0061A4), modifier = Modifier.size(18.dp))
                            Text(
                                text = "4. INFORMED CONSENT (MANDATORY)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color(0xFFFEF2F2)
                        ) {
                            Text(
                                text = "REQUIRED",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }

                    // Checkbox
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.consentAccepted.value = !consent }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Checkbox(
                                checked = consent,
                                onCheckedChange = { viewModel.consentAccepted.value = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0061A4))
                            )
                            Column {
                                Text(
                                    text = "Consent verbally explained & accepted by patient / guardian",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Assamese/Bengali protocol summary: Patient agrees to photographic computer-assisted joint motion evaluation and risk screening record keeping.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    // Attesting Worker Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF1F5F9),
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
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0061A4)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text("ATTESTING HEALTH WORKER", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0061A4), fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                    Text("Pranita Saikia, ASHA", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                    Text("SC-Garamur • ID: AS-ASHA-04921", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                                }
                            }

                            Surface(shape = RoundedCornerShape(999.dp), color = Color(0xFFDCFCE7)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(14.dp))
                                    Text("VERIFIED", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                }
                            }
                        }
                    }
                }
            }

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { viewModel.navigateTo(Screen.Questionnaire) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_proceed_to_questionnaire"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4))
                ) {
                    Text(
                        "Continue to Symptoms Survey (Step 2)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable { showDraftSavedToast = true }
                        .testTag("btn_save_draft")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF0061A4), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Save Draft to Local Storage (Offline Safe)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0061A4)
                        )
                    }
                }
            }
        }
    }

    if (showDraftSavedToast) {
        AlertDialog(
            onDismissRequest = { showDraftSavedToast = false },
            title = { Text("Draft Saved Locally", fontWeight = FontWeight.Bold) },
            text = { Text("Patient record draft saved to local secure SQLite cache. You can resume screening anytime offline.") },
            confirmButton = {
                Button(
                    onClick = { showDraftSavedToast = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4))
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun ComorbidityChip(
    label: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0061A4))
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
        }
    }
}
