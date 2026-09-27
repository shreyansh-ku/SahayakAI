package com.example.clinix.service

import com.example.clinix.ai.ClinicalEmbeddingProvider
import com.example.clinix.ai.MockEmbeddingProvider
import com.example.clinix.ai.MockVectorStore
import com.example.clinix.ai.VectorStore
import com.example.clinix.model.AnalyticalCohortType
import com.example.data.local.ScreeningEntity
import kotlin.math.abs

class ClinixAnalyticsService(
    val embeddingProvider: ClinicalEmbeddingProvider = MockEmbeddingProvider(),
    val vectorStore: VectorStore = MockVectorStore(embeddingProvider)
) {

    fun determineCohort(screening: ScreeningEntity): AnalyticalCohortType {
        val painDominance = screening.painScore >= 6 || screening.nightPain >= 2
        val movementDominance = screening.stsTimeSec >= 15.0 || screening.asymmetry >= 0.25 || screening.kneeRom < 100.0

        return when {
            painDominance && movementDominance -> AnalyticalCohortType.COHORT_C
            painDominance -> AnalyticalCohortType.COHORT_A
            movementDominance -> AnalyticalCohortType.COHORT_B
            screening.age < 40 && screening.hasInjury -> AnalyticalCohortType.COHORT_D
            screening.riskLevel == "INCONCLUSIVE" -> AnalyticalCohortType.COHORT_D
            else -> AnalyticalCohortType.COHORT_A
        }
    }

    fun evaluateAtypical(screening: ScreeningEntity): Pair<Boolean, String?> {
        val reasons = mutableListOf<String>()

        if (screening.testSkipped) {
            reasons.add("5xSTS movement test was bypassed due to functional safety criteria.")
        } else if (screening.riskLevel == "INCONCLUSIVE") {
            reasons.add("Landmark tracking or video quality insufficient for standard telemetry profile.")
        }

        if (screening.painScore >= 8 && screening.stsTimeSec <= 10.0 && !screening.testSkipped) {
            reasons.add("High subjective pain score (8+/10) accompanied by rapid 5xSTS completion (${screening.stsTimeSec}s).")
        }

        if (screening.asymmetry >= 0.45) {
            reasons.add("Extreme single-leg offloading pattern (asymmetry ${String.format("%.2f", screening.asymmetry)}).")
        }

        if (screening.age in 18..37 && (screening.hasInjury || screening.crepitus > 0)) {
            reasons.add("Younger demographic cohort (age ${screening.age}) presenting with early focal symptoms or injury history.")
        }

        val isAtypical = reasons.isNotEmpty()
        val reason = if (isAtypical) reasons.joinToString(" • ") else null
        return Pair(isAtypical, reason)
    }

    fun generateAiAnalyticalSummary(screening: ScreeningEntity): List<String> {
        val observations = mutableListOf<String>()

        if (screening.painScore >= 6) {
            observations.add("Elevated subjective knee discomfort score (${screening.painScore}/10)")
        } else if (screening.painScore > 0) {
            observations.add("Mild-to-moderate knee pain reported (${screening.painScore}/10)")
        }

        if (screening.stiffnessDuration >= 30) {
            observations.add("Prolonged morning joint stiffness (${screening.stiffnessDuration} mins)")
        } else if (screening.stiffnessDuration > 0) {
            observations.add("Transient morning stiffness (${screening.stiffnessDuration} mins)")
        }

        if (screening.testSkipped) {
            observations.add("5xSTS protocol bypassed: ${screening.skipReason ?: "Safety pause"}")
        } else {
            if (screening.stsTimeSec >= 15.0) {
                observations.add("Slower 5xSTS sit-to-stand transition (${String.format("%.1f", screening.stsTimeSec)} sec)")
            } else {
                observations.add("Preserved 5xSTS transition velocity (${String.format("%.1f", screening.stsTimeSec)} sec)")
            }

            if (screening.asymmetry >= 0.20) {
                observations.add("Bilateral weight-bearing asymmetry detected during ascent (${(screening.asymmetry * 100).toInt()}%)")
            }

            if (screening.kneeRom < 100.0) {
                observations.add("Reduced knee flexion range of motion (${screening.kneeRom.toInt()}°)")
            }
        }

        if (screening.bmi >= 27.5) {
            observations.add("Elevated BMI (${String.format("%.1f", screening.bmi)}) contributing to joint loading")
        }

        if (screening.crepitus > 0) {
            observations.add("Palpable or audible joint crepitus noted during flexion")
        }

        if (screening.nightPain >= 2) {
            observations.add("Discomfort interfering with sleep or rest")
        }

        return observations
    }

    fun compareCases(
        base: ScreeningEntity,
        target: ScreeningEntity
    ): Pair<List<String>, List<String>> {
        val shared = mutableListOf<String>()
        val diffs = mutableListOf<String>()

        if (abs(base.painScore - target.painScore) <= 2) {
            shared.add("Similar pain intensity (${target.painScore}/10 vs ${base.painScore}/10)")
        } else {
            diffs.add("Divergent pain score (${target.painScore}/10 vs ${base.painScore}/10)")
        }

        if (abs(base.stiffnessDuration - target.stiffnessDuration) <= 15) {
            shared.add("Comparable stiffness duration (~${target.stiffnessDuration}m)")
        } else {
            diffs.add("Stiffness duration differs (${target.stiffnessDuration}m vs ${base.stiffnessDuration}m)")
        }

        if (!base.testSkipped && !target.testSkipped) {
            if (abs(base.stsTimeSec - target.stsTimeSec) <= 3.0) {
                shared.add("Comparable 5xSTS completion speed (~${String.format("%.1f", target.stsTimeSec)}s)")
            } else {
                diffs.add("Different 5xSTS speed (${String.format("%.1f", target.stsTimeSec)}s vs ${String.format("%.1f", base.stsTimeSec)}s)")
            }
        }

        if (abs(base.age - target.age) <= 8) {
            shared.add("Similar age cohort (${target.age}y vs ${base.age}y)")
        } else {
            diffs.add("Age variation (${target.age}y vs ${base.age}y)")
        }

        if (base.crepitus == target.crepitus) {
            if (base.crepitus > 0) shared.add("Shared presence of joint crepitus")
        } else {
            diffs.add(if (target.crepitus > 0) "Target exhibits crepitus" else "Target has no crepitus")
        }

        if (shared.isEmpty()) {
            shared.add("General functional mobility profile")
        }

        return Pair(shared, diffs)
    }

    fun createSyntheticDemoCases(): List<ScreeningEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            ScreeningEntity(
                screeningId = "DEMO-CASE-1001",
                patientId = "SYN-PAT-1001",
                patientName = "Savitri Das (Demo)",
                guardianName = "R. Das",
                village = "Garamur Phulguri",
                age = 62,
                sex = "Female",
                heightCm = 152.0,
                weightKg = 68.0,
                bmi = 29.4,
                hasInjury = false,
                injuryDetails = "None",
                occupationCategory = "Agricultural Fieldwork",
                comorbidities = "Hypertension",
                consentGiven = true,
                attestingWorker = "AS-ASHA-0492",
                timestamp = now - 3600000 * 5,
                formattedDate = "Today, 10:15 AM",
                painScore = 8,
                stiffnessDuration = 45,
                walkingLimitation = 3,
                stairsDifficulty = 4,
                standDifficulty = 3,
                crepitus = 1,
                nightPain = 3,
                questionnaireTotal = 22,
                stsTimeSec = 17.8,
                repCount = 5,
                kneeAngle = 84.0,
                kneeRom = 92.0,
                asymmetry = 0.28,
                testSkipped = false,
                skipReason = null,
                riskLevel = "HIGH",
                riskScore = 0.86,
                confidence = 0.94,
                referralDestination = "District Hospital Orthopedics (Jorhat)",
                counselingSummary = "High symptom and functional mobility burden. Priority secondary evaluation advised.",
                syncStatus = "SYNCED"
            ),
            ScreeningEntity(
                screeningId = "DEMO-CASE-1002",
                patientId = "SYN-PAT-1002",
                patientName = "Bipul Saikia (Demo)",
                guardianName = "M. Saikia",
                village = "Kamalabari",
                age = 54,
                sex = "Male",
                heightCm = 168.0,
                weightKg = 72.0,
                bmi = 25.5,
                hasInjury = true,
                injuryDetails = "Right knee sprain 4 years ago",
                occupationCategory = "Ferry Operator",
                comorbidities = "None",
                consentGiven = true,
                attestingWorker = "AS-ASHA-0492",
                timestamp = now - 3600000 * 22,
                formattedDate = "Yesterday, 02:40 PM",
                painScore = 5,
                stiffnessDuration = 20,
                walkingLimitation = 2,
                stairsDifficulty = 3,
                standDifficulty = 2,
                crepitus = 1,
                nightPain = 1,
                questionnaireTotal = 14,
                stsTimeSec = 14.2,
                repCount = 5,
                kneeAngle = 88.0,
                kneeRom = 112.0,
                asymmetry = 0.22,
                testSkipped = false,
                skipReason = null,
                riskLevel = "MEDIUM",
                riskScore = 0.58,
                confidence = 0.91,
                referralDestination = "PHC Physiotherapy Unit",
                counselingSummary = "Moderate mobility strain with prior right knee trauma. Quadriceps strengthening suggested.",
                syncStatus = "SYNCED"
            ),
            ScreeningEntity(
                screeningId = "DEMO-CASE-1003",
                patientId = "SYN-PAT-1003",
                patientName = "Moina Begum (Demo)",
                guardianName = "A. Begum",
                village = "Dakhinpat",
                age = 68,
                sex = "Female",
                heightCm = 150.0,
                weightKg = 59.0,
                bmi = 26.2,
                hasInjury = false,
                injuryDetails = "None",
                occupationCategory = "Domestic / Handloom",
                comorbidities = "Type 2 Diabetes",
                consentGiven = true,
                attestingWorker = "AS-ASHA-0492",
                timestamp = now - 3600000 * 30,
                formattedDate = "Sep 8, 11:00 AM",
                painScore = 7,
                stiffnessDuration = 35,
                walkingLimitation = 3,
                stairsDifficulty = 3,
                standDifficulty = 3,
                crepitus = 1,
                nightPain = 2,
                questionnaireTotal = 19,
                stsTimeSec = 0.0,
                repCount = 0,
                kneeAngle = 0.0,
                kneeRom = 0.0,
                asymmetry = 0.0,
                testSkipped = true,
                skipReason = "Severe acute balance instability and unassisted fall risk.",
                riskLevel = "INCONCLUSIVE",
                riskScore = 0.72,
                confidence = 0.65,
                referralDestination = "Sub-Divisional Civil Hospital",
                counselingSummary = "Functional movement assessment safely paused due to fall risk. Physician review advised.",
                syncStatus = "UNSYNCED"
            ),
            ScreeningEntity(
                screeningId = "DEMO-CASE-1004",
                patientId = "SYN-PAT-1004",
                patientName = "Tarun Gogoi (Demo)",
                guardianName = "K. Gogoi",
                village = "Bongaon",
                age = 34,
                sex = "Male",
                heightCm = 174.0,
                weightKg = 79.0,
                bmi = 26.1,
                hasInjury = true,
                injuryDetails = "Sports meniscus repair 2021",
                occupationCategory = "Primary School Teacher",
                comorbidities = "None",
                consentGiven = true,
                attestingWorker = "AS-ASHA-0492",
                timestamp = now - 3600000 * 48,
                formattedDate = "Sep 7, 04:15 PM",
                painScore = 8,
                stiffnessDuration = 10,
                walkingLimitation = 1,
                stairsDifficulty = 2,
                standDifficulty = 1,
                crepitus = 0,
                nightPain = 0,
                questionnaireTotal = 12,
                stsTimeSec = 9.8,
                repCount = 5,
                kneeAngle = 92.0,
                kneeRom = 125.0,
                asymmetry = 0.12,
                testSkipped = false,
                skipReason = null,
                riskLevel = "MEDIUM",
                riskScore = 0.52,
                confidence = 0.95,
                referralDestination = "Sports Injury & Physical Therapy Clinic",
                counselingSummary = "Atypical pattern: High subjective pain in young male with rapid 5xSTS transition and previous surgical repair.",
                syncStatus = "SYNCED"
            )
        )
    }
}
