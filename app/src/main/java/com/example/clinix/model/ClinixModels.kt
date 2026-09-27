package com.example.clinix.model

import com.example.data.local.ClinicalReviewEntity
import com.example.data.local.ScreeningEntity

enum class ActiveModule(val title: String, val subtitle: String) {
    ORTHOSCREEN("OrthoScreen AI", "Field Screening"),
    CLINIXAI("ClinixAI", "Clinical Review")
}

enum class UserRole(val label: String, val canAccessOrthoScreen: Boolean, val canAccessClinixAI: Boolean) {
    HEALTH_WORKER("Health Worker (ASHA / ANM / CHO)", canAccessOrthoScreen = true, canAccessClinixAI = false),
    CLINICIAN("Clinician (Doctor / Orthopedic / Physio)", canAccessOrthoScreen = false, canAccessClinixAI = true),
    AUTHORIZED_USER("Authorized User (Field & Clinical Review)", canAccessOrthoScreen = true, canAccessClinixAI = true)
}

enum class ReviewStatus(val label: String, val badgeBgHex: Long, val badgeTextHex: Long) {
    NEW("NEW", 0xFFE2E8F0, 0xFF475569),
    IN_REVIEW("IN REVIEW", 0xFFFEF3C7, 0xFFB45309),
    REVIEWED("REVIEWED", 0xFFDCFCE7, 0xFF15803D),
    REPEAT_REQUIRED("REPEAT REQUIRED", 0xFFFFEDD5, 0xFFC2410C),
    REFERRED("REFERRED", 0xFFE0E7FF, 0xFF4338CA)
}

enum class AnalyticalCohortType(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String
) {
    COHORT_A(
        id = "cohort_a",
        title = "Cohort A",
        subtitle = "Pain-dominant functional limitation",
        description = "High reported symptom discomfort with relatively preserved kinematic execution."
    ),
    COHORT_B(
        id = "cohort_b",
        title = "Cohort B",
        subtitle = "Movement-dominant limitation",
        description = "Marked kinematic impairment, asymmetric offloading, and prolonged 5xSTS completion."
    ),
    COHORT_C(
        id = "cohort_c",
        title = "Cohort C",
        subtitle = "Higher symptom + mobility burden",
        description = "Combined elevation across subjective pain, stiffness duration, and kinematic strain."
    ),
    COHORT_D(
        id = "cohort_d",
        title = "Cohort D",
        subtitle = "Atypical / mixed pattern",
        description = "Feature combinations presenting divergence from surrounding clustered groups."
    )
}

data class ClinixCase(
    val caseId: String,
    val screening: ScreeningEntity,
    val review: ClinicalReviewEntity? = null,
    val assignedCohort: AnalyticalCohortType = AnalyticalCohortType.COHORT_A,
    val isAtypical: Boolean = false,
    val atypicalReason: String? = null,
    val isDemo: Boolean = false,
    val featureVector: FloatArray = floatArrayOf()
) {
    val reviewStatus: ReviewStatus
        get() = when (review?.status) {
            "REVIEWED" -> ReviewStatus.REVIEWED
            "IN_REVIEW" -> ReviewStatus.IN_REVIEW
            "REPEAT_REQUIRED" -> ReviewStatus.REPEAT_REQUIRED
            "REFERRED" -> ReviewStatus.REFERRED
            else -> ReviewStatus.NEW
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ClinixCase
        return caseId == other.caseId
    }

    override fun hashCode(): Int {
        return caseId.hashCode()
    }
}

data class SimilarCase(
    val caseId: String,
    val patientId: String,
    val age: Int,
    val sex: String,
    val screeningRisk: String,
    val similarityScore: Double,
    val sharedPatterns: List<String>,
    val differences: List<String>,
    val stsTimeSec: Double,
    val painScore: Int,
    val isDemo: Boolean = false
)

data class AnalyticalCohortInfo(
    val cohortType: AnalyticalCohortType,
    val caseCount: Int,
    val commonPatterns: List<String>,
    val riskDistribution: Map<String, Int>,
    val recentCases: List<ClinixCase>
)

data class AtypicalCase(
    val caseId: String,
    val patientId: String,
    val patientName: String,
    val age: Int,
    val riskLevel: String,
    val outlierScore: Double,
    val nearestCohort: String,
    val explanation: String,
    val unusualFeatures: List<String>,
    val screening: ScreeningEntity,
    val isDemo: Boolean = false
)

data class ClinixOverviewMetrics(
    val newCasesCount: Int,
    val highRiskCount: Int,
    val awaitingReviewCount: Int,
    val inconclusiveCount: Int,
    val atypicalCount: Int
)
