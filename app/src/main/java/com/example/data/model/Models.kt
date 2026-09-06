package com.example.data.model

enum class RiskLevel(val label: String, val badgeColorHex: Long, val badgeTextHex: Long) {
    LOW("LOW RISK", 0xFFAFEBE4, 0xFF316C66),
    MEDIUM("MED RISK", 0xFFFFDCC3, 0xFF713B00),
    HIGH("HIGH RISK", 0xFFFFDAD6, 0xFFBA1A1A),
    INCONCLUSIVE("INCONCLUSIVE", 0xFFE6EEFF, 0xFF00534E)
}

enum class SyncStatus {
    UNSYNCED,
    SYNCING,
    SYNCED
}

data class ContributingFactor(
    val title: String,
    val description: String,
    val impactPercent: Int,
    val category: String,
    val isPrimary: Boolean = false
)

data class CounselingTask(
    val title: String,
    val description: String,
    val iconName: String
)

data class MovementFeatures(
    val stsCompletionTimeSec: Double = 16.4,
    val repetitionCount: Int = 5,
    val kneeAngle: Double = 86.5,
    val kneeRom: Double = 108.0,
    val asymmetryScore: Double = 0.71,
    val movementConsistency: Double = 0.82,
    val landmarkConfidence: Double = 0.94,
    val videoQualityScore: Double = 0.96,
    val testSkipped: Boolean = false,
    val skipReason: String? = null
)

data class RiskResult(
    val riskLevel: RiskLevel,
    val riskScore: Double,
    val confidence: Double,
    val fiveStsSpeedDesc: String,
    val speedExcessPercent: Int,
    val symmetryDesc: String,
    val symmetryOffloading: String,
    val contributingFactors: List<ContributingFactor>,
    val primaryReferral: String,
    val referralDestination: String,
    val counselingTasks: List<CounselingTask>,
    val disclaimer: String = "Screening result — not a diagnosis. Clinical validation is required before deployment.",
    val isInconclusive: Boolean = false,
    val inconclusiveReason: String? = null
)

data class PatientRecord(
    val patientId: String,
    val name: String,
    val guardianName: String,
    val village: String,
    val age: Int,
    val sex: String,
    val heightCm: Double,
    val weightKg: Double,
    val bmi: Double,
    val hasInjury: Boolean,
    val injuryDetails: String,
    val occupationCategory: String,
    val comorbidities: List<String>,
    val consentGiven: Boolean,
    val attestingWorker: String
)
