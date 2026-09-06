package com.example.data.repository

import com.example.data.local.ScreeningDao
import com.example.data.local.ScreeningEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrthoScreenRepository(private val dao: ScreeningDao) {

    val allScreenings: Flow<List<ScreeningEntity>> = dao.getAllScreenings()
    val highRiskCount: Flow<Int> = dao.getHighRiskCount()
    val medRiskCount: Flow<Int> = dao.getMedRiskCount()
    val unsyncedCount: Flow<Int> = dao.getUnsyncedCount()
    val totalCount: Flow<Int> = dao.getTotalCount()

    suspend fun checkAndSeedInitialData() {
        val existing = dao.getAllScreenings().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH)

            val seedList = listOf(
                ScreeningEntity(
                    screeningId = "SCR-2023-ASM-001",
                    patientId = "AS-MAJ-0891",
                    patientName = "Binita Gogoi",
                    guardianName = "Hemanta Gogoi",
                    village = "Kamalabari, Majuli",
                    age = 58,
                    sex = "Female",
                    heightCm = 152.0,
                    weightKg = 61.0,
                    bmi = 26.4,
                    hasInjury = false,
                    injuryDetails = "None",
                    occupationCategory = "Homemaker / Caretaker (Ground-level domestic tasks)",
                    comorbidities = "Hypertension",
                    consentGiven = true,
                    attestingWorker = "Pranita Saikia, ASHA",
                    timestamp = now - 1000 * 60 * 45, // 45 mins ago
                    formattedDate = "Today, 10:15 AM",
                    painScore = 2,
                    stiffnessDuration = 1,
                    walkingLimitation = 1,
                    stairsDifficulty = 1,
                    standDifficulty = 1,
                    crepitus = 1,
                    nightPain = 1,
                    questionnaireTotal = 16,
                    stsTimeSec = 14.8,
                    repCount = 5,
                    kneeAngle = 89.0,
                    kneeRom = 105.0,
                    asymmetry = 0.76,
                    testSkipped = false,
                    skipReason = null,
                    riskLevel = "MEDIUM",
                    riskScore = 0.61,
                    confidence = 0.81,
                    referralDestination = "PHC Kamalabari Physiotherapy Clinic",
                    counselingSummary = "Bilateral knee pain, antalgic gait, sit-to-stand delay (>14s)",
                    syncStatus = "UNSYNCED"
                ),
                ScreeningEntity(
                    screeningId = "SCR-2023-ASM-002",
                    patientId = "AS-MAJ-0890",
                    patientName = "Pranab Saikia",
                    guardianName = "Late Girin Saikia",
                    village = "Garmur, Majuli",
                    age = 64,
                    sex = "Male",
                    heightCm = 166.0,
                    weightKg = 73.0,
                    bmi = 26.5,
                    hasInjury = true,
                    injuryDetails = "Meniscus tear from tractor fall (2019)",
                    occupationCategory = "Agricultural field worker (Paddy transplanting)",
                    comorbidities = "Hypertension, Diabetes Mellitus",
                    consentGiven = true,
                    attestingWorker = "ANM R. Devi",
                    timestamp = now - 1000 * 60 * 60 * 26, // Yesterday
                    formattedDate = "Yesterday, 02:40 PM",
                    painScore = 3,
                    stiffnessDuration = 2,
                    walkingLimitation = 2,
                    stairsDifficulty = 2,
                    standDifficulty = 2,
                    crepitus = 2,
                    nightPain = 2,
                    questionnaireTotal = 22,
                    stsTimeSec = 17.5,
                    repCount = 5,
                    kneeAngle = 82.0,
                    kneeRom = 92.0,
                    asymmetry = 0.68,
                    testSkipped = false,
                    skipReason = null,
                    riskLevel = "HIGH",
                    riskScore = 0.84,
                    confidence = 0.88,
                    referralDestination = "District Orthopaedic OPD (Token #DH-812)",
                    counselingSummary = "Referred to District Orthopaedic OPD • Suspected Advanced Gonarthrosis",
                    syncStatus = "SYNCED"
                ),
                ScreeningEntity(
                    screeningId = "SCR-2023-ASM-003",
                    patientId = "AS-MAJ-0889",
                    patientName = "Monorama Kalita",
                    guardianName = "Bhaben Kalita",
                    village = "Jengraimukh",
                    age = 52,
                    sex = "Female",
                    heightCm = 155.0,
                    weightKg = 53.0,
                    bmi = 22.1,
                    hasInjury = false,
                    injuryDetails = "None",
                    occupationCategory = "Handloom artisan / Mishing traditional weaver",
                    comorbidities = "None",
                    consentGiven = true,
                    attestingWorker = "Pranita Saikia, ASHA",
                    timestamp = now - 1000 * 60 * 60 * 30, // Yesterday
                    formattedDate = "Yesterday, 11:20 AM",
                    painScore = 0,
                    stiffnessDuration = 0,
                    walkingLimitation = 0,
                    stairsDifficulty = 0,
                    standDifficulty = 0,
                    crepitus = 0,
                    nightPain = 0,
                    questionnaireTotal = 4,
                    stsTimeSec = 10.8,
                    repCount = 5,
                    kneeAngle = 92.0,
                    kneeRom = 125.0,
                    asymmetry = 0.94,
                    testSkipped = false,
                    skipReason = null,
                    riskLevel = "LOW",
                    riskScore = 0.19,
                    confidence = 0.92,
                    referralDestination = "Community Health Worker / Sub-Centre Follow-up",
                    counselingSummary = "Normal range of flexion/extension. Recommended home exercises and calcium counseling.",
                    syncStatus = "SYNCED"
                ),
                ScreeningEntity(
                    screeningId = "SCR-2023-ASM-004",
                    patientId = "AS-MAJ-0888",
                    patientName = "Tarun Barman",
                    guardianName = "Late K. Barman",
                    village = "Kamalabari",
                    age = 71,
                    sex = "Male",
                    heightCm = 160.0,
                    weightKg = 70.0,
                    bmi = 27.3,
                    hasInjury = true,
                    injuryDetails = "Knee joint trauma (2015)",
                    occupationCategory = "Agricultural field worker (Retired)",
                    comorbidities = "Hypertension",
                    consentGiven = true,
                    attestingWorker = "ANM R. Devi",
                    timestamp = now - 1000 * 60 * 60 * 72,
                    formattedDate = "12 Oct, 09:30 AM",
                    painScore = 4,
                    stiffnessDuration = 3,
                    walkingLimitation = 3,
                    stairsDifficulty = 3,
                    standDifficulty = 3,
                    crepitus = 2,
                    nightPain = 3,
                    questionnaireTotal = 26,
                    stsTimeSec = 0.0,
                    repCount = 0,
                    kneeAngle = 0.0,
                    kneeRom = 0.0,
                    asymmetry = 0.0,
                    testSkipped = true,
                    skipReason = "Severe arthralgia / acute weight-bearing pain",
                    riskLevel = "HIGH",
                    riskScore = 0.89,
                    confidence = 0.90,
                    referralDestination = "Immediate OPD Referral to Medical Officer",
                    counselingSummary = "Joint crepitus, valgus asymmetry noted; priority referral to PHC Medical Officer.",
                    syncStatus = "UNSYNCED"
                )
            )
            dao.insertAll(seedList)
        }
    }

    suspend fun insertScreening(screening: ScreeningEntity) {
        dao.insertScreening(screening)
    }

    suspend fun getScreening(id: String): ScreeningEntity? {
        return dao.getScreeningById(id)
    }

    suspend fun markAllSynced() {
        dao.markAllSynced()
    }

    suspend fun deleteScreening(id: String) {
        dao.deleteScreening(id)
    }
}
