package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.MovementFeatureExtractor
import com.example.ai.RiskEngine
import com.example.data.local.AppDatabase
import com.example.data.local.ScreeningEntity
import com.example.data.model.MovementFeatures
import com.example.data.model.PatientRecord
import com.example.data.model.RiskLevel
import com.example.data.model.RiskResult
import com.example.data.repository.OrthoScreenRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

import com.example.clinix.model.ActiveModule
import com.example.clinix.model.AnalyticalCohortInfo
import com.example.clinix.model.AnalyticalCohortType
import com.example.clinix.model.AtypicalCase
import com.example.clinix.model.ClinixCase
import com.example.clinix.model.ClinixOverviewMetrics
import com.example.clinix.model.ReviewStatus
import com.example.clinix.model.SimilarCase
import com.example.clinix.model.UserRole
import com.example.clinix.service.ClinixAnalyticsService
import com.example.data.local.ClinicalReviewEntity

sealed class Screen {
    object Login : Screen()
    object Dashboard : Screen()
    object Registration : Screen()       // Step 1
    object Questionnaire : Screen()      // Step 2
    object MovementAssessment : Screen() // Step 3
    object Result : Screen()             // Step 4
    object SyncCenter : Screen()
    data class PatientDetail(val screening: ScreeningEntity) : Screen()

    // ClinixAI Module Screens
    object ClinixOverview : Screen()
    object ClinixCases : Screen()
    data class ClinixCaseDetail(val caseId: String) : Screen()
    data class ClinixSimilarCases(val caseId: String, val topK: Int = 5) : Screen()
    object ClinixCohorts : Screen()
    data class ClinixCohortDetail(val cohortType: AnalyticalCohortType) : Screen()
    object ClinixOutliers : Screen()
    object ClinixProfile : Screen()
}

enum class FilterChipType {
    ALL,
    UNSYNCED,
    HIGH_RISK,
    PENDING_REFERRAL,
    TODAY
}

class OrthoScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OrthoScreenRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = OrthoScreenRepository(db.screeningDao(), db.clinicalReviewDao())
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    // Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Login)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screener Session Info
    var screenerId = "EVAL-FLD-78401"
    var screenerRole = "Field Screener"
    var fieldSite = "Majuli Field Evaluation Site • Catchment Alpha"
    var selectedLanguage = "অসমীয়া / EN"

    // Network & Sync Simulation
    private val _isOnline = MutableStateFlow(false) // Defaults to offline field environment!
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Dashboard Search & Filters
    val searchQuery = MutableStateFlow("")
    val activeFilter = MutableStateFlow(FilterChipType.ALL)

    val allScreenings = repository.allScreenings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredScreenings: StateFlow<List<ScreeningEntity>> = combine(
        allScreenings,
        searchQuery,
        activeFilter
    ) { list, query, filter ->
        list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.patientName.contains(query, ignoreCase = true) ||
                    item.patientId.contains(query, ignoreCase = true) ||
                    item.village.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                FilterChipType.ALL -> true
                FilterChipType.UNSYNCED -> item.syncStatus == "UNSYNCED"
                FilterChipType.HIGH_RISK -> item.riskLevel == "HIGH"
                FilterChipType.PENDING_REFERRAL -> item.riskLevel == "HIGH" || item.riskLevel == "MEDIUM"
                FilterChipType.TODAY -> item.formattedDate.contains("Today", ignoreCase = true)
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalScreenedCount = repository.totalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val highRiskCount = repository.highRiskCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val medRiskCount = repository.medRiskCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val unsyncedCount = repository.unsyncedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Current Screening in Progress State
    val generatedCaseId = MutableStateFlow(generateNewCaseId())
    val patientName = MutableStateFlow("Debojit Hazarika")
    val guardianName = MutableStateFlow("Late Mohendra Hazarika")
    val selectedVillage = MutableStateFlow("Garamur Phutuki Pathar")
    val localRefId = MutableStateFlow("")
    val patientAge = MutableStateFlow(62)
    val patientSex = MutableStateFlow("Male")
    val patientHeightCm = MutableStateFlow(164.0)
    val patientWeightKg = MutableStateFlow(68.0)
    val hasPriorInjury = MutableStateFlow(true)
    val injuryDetails = MutableStateFlow("Right knee meniscus tear & sprain (2018 farming fall)")
    val occupationCategory = MutableStateFlow("Agricultural field worker (Paddy transplanting)")
    val comorbidityHypertension = MutableStateFlow(true)
    val comorbidityDiabetes = MutableStateFlow(true)
    val comorbidityRheumatoid = MutableStateFlow(false)
    val comorbidityNone = MutableStateFlow(false)
    val consentAccepted = MutableStateFlow(false)
    val attestingWorker = MutableStateFlow("Pranita Saikia, ASHA (SC-Garamur)")

    // Questionnaire Answers (WOMAC-adapted)
    val qPainScore = MutableStateFlow(2) // 0-4
    val qStiffness = MutableStateFlow(1) // 0-3 (10-30 min)
    val qWalkingLimit = MutableStateFlow(1) // 0-3 (100m to 500m)
    val qStairsDifficulty = MutableStateFlow(1) // 0-3 (Moderate)
    val qStandDifficulty = MutableStateFlow(1) // 0-3 (Needs hand support)
    val qCrepitus = MutableStateFlow(2) // 0-2 (Present on every flexion)
    val qNightPain = MutableStateFlow(1) // 0-3 (Sometimes)

    // Movement Assessment State
    val movementFeatures = MutableStateFlow(MovementFeatures())
    val movementSkipped = MutableStateFlow(false)
    val movementSkipReason = MutableStateFlow<String?>(null)

    // Evaluated Result
    val currentRiskResult = MutableStateFlow<RiskResult?>(null)
    val lastSavedScreening = MutableStateFlow<ScreeningEntity?>(null)

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleNetworkMode() {
        _isOnline.value = !_isOnline.value
    }

    fun setLanguage(lang: String) {
        selectedLanguage = lang
    }

    fun startNewScreening() {
        generatedCaseId.value = generateNewCaseId()
        patientName.value = ""
        guardianName.value = ""
        localRefId.value = ""
        patientAge.value = 55
        patientSex.value = "Female"
        patientHeightCm.value = 155.0
        patientWeightKg.value = 60.0
        hasPriorInjury.value = false
        injuryDetails.value = ""
        occupationCategory.value = "Agricultural field worker (Paddy transplanting)"
        comorbidityHypertension.value = false
        comorbidityDiabetes.value = false
        comorbidityRheumatoid.value = false
        comorbidityNone.value = true
        consentAccepted.value = false

        // Default symptoms
        qPainScore.value = 2
        qStiffness.value = 1
        qWalkingLimit.value = 1
        qStairsDifficulty.value = 1
        qStandDifficulty.value = 1
        qCrepitus.value = 1
        qNightPain.value = 1

        movementFeatures.value = MovementFeatures()
        movementSkipped.value = false
        movementSkipReason.value = null
        currentRiskResult.value = null

        navigateTo(Screen.Registration)
    }

    fun calculateBmi(heightCm: Double, weightKg: Double): Double {
        if (heightCm <= 0) return 0.0
        val m = heightCm / 100.0
        return (weightKg / (m * m)).coerceIn(10.0, 60.0)
    }

    fun computeQuestionnaireTotal(): Int {
        return qPainScore.value * 2 +
                qStiffness.value * 2 +
                qWalkingLimit.value * 2 +
                qStairsDifficulty.value * 2 +
                qStandDifficulty.value * 2 +
                qCrepitus.value * 2 +
                qNightPain.value * 2
    }

    fun evaluateScreening(forceInconclusive: Boolean = false) {
        val height = patientHeightCm.value
        val weight = patientWeightKg.value
        val bmi = calculateBmi(height, weight)

        val comorbiditiesList = mutableListOf<String>()
        if (comorbidityHypertension.value) comorbiditiesList.add("Hypertension")
        if (comorbidityDiabetes.value) comorbiditiesList.add("Diabetes")
        if (comorbidityRheumatoid.value) comorbiditiesList.add("Rheumatoid")
        if (comorbiditiesList.isEmpty()) comorbiditiesList.add("None")

        val patientRecord = PatientRecord(
            patientId = generatedCaseId.value,
            name = patientName.value.ifBlank { "Screening Patient" },
            guardianName = guardianName.value,
            village = selectedVillage.value,
            age = patientAge.value,
            sex = patientSex.value,
            heightCm = height,
            weightKg = weight,
            bmi = bmi,
            hasInjury = hasPriorInjury.value,
            injuryDetails = injuryDetails.value,
            occupationCategory = occupationCategory.value,
            comorbidities = comorbiditiesList,
            consentGiven = consentAccepted.value,
            attestingWorker = attestingWorker.value
        )

        val motion = if (movementSkipped.value) {
            MovementFeatures(
                stsCompletionTimeSec = 0.0,
                repetitionCount = 0,
                kneeAngle = 0.0,
                kneeRom = 0.0,
                asymmetryScore = 0.0,
                testSkipped = true,
                skipReason = movementSkipReason.value
            )
        } else {
            movementFeatures.value
        }

        val result = RiskEngine.evaluate(
            patient = patientRecord,
            painScore = qPainScore.value,
            stiffnessDuration = qStiffness.value,
            walkingLimitation = qWalkingLimit.value,
            stairsDifficulty = qStairsDifficulty.value,
            standDifficulty = qStandDifficulty.value,
            crepitus = qCrepitus.value,
            nightPain = qNightPain.value,
            movement = motion,
            forceInconclusive = forceInconclusive
        )

        currentRiskResult.value = result
        navigateTo(Screen.Result)
    }

    fun saveCurrentScreening() {
        val result = currentRiskResult.value ?: return
        val height = patientHeightCm.value
        val weight = patientWeightKg.value
        val bmi = calculateBmi(height, weight)
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH)

        val entity = ScreeningEntity(
            screeningId = "SCR-" + UUID.randomUUID().toString().take(8).uppercase(),
            patientId = generatedCaseId.value,
            patientName = patientName.value.ifBlank { "Screening Subject" },
            guardianName = guardianName.value,
            village = selectedVillage.value,
            age = patientAge.value,
            sex = patientSex.value,
            heightCm = height,
            weightKg = weight,
            bmi = bmi,
            hasInjury = hasPriorInjury.value,
            injuryDetails = injuryDetails.value,
            occupationCategory = occupationCategory.value,
            comorbidities = if (comorbidityHypertension.value) "Hypertension" else "None",
            consentGiven = consentAccepted.value,
            attestingWorker = attestingWorker.value,
            timestamp = now,
            formattedDate = "Today, " + SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(now)),
            painScore = qPainScore.value,
            stiffnessDuration = qStiffness.value,
            walkingLimitation = qWalkingLimit.value,
            stairsDifficulty = qStairsDifficulty.value,
            standDifficulty = qStandDifficulty.value,
            crepitus = qCrepitus.value,
            nightPain = qNightPain.value,
            questionnaireTotal = computeQuestionnaireTotal(),
            stsTimeSec = movementFeatures.value.stsCompletionTimeSec,
            repCount = movementFeatures.value.repetitionCount,
            kneeAngle = movementFeatures.value.kneeAngle,
            kneeRom = movementFeatures.value.kneeRom,
            asymmetry = movementFeatures.value.asymmetryScore,
            testSkipped = movementSkipped.value,
            skipReason = movementSkipReason.value,
            riskLevel = result.riskLevel.name,
            riskScore = result.riskScore,
            confidence = result.confidence,
            referralDestination = result.referralDestination,
            counselingSummary = result.primaryReferral,
            syncStatus = if (_isOnline.value) "SYNCED" else "UNSYNCED"
        )

        lastSavedScreening.value = entity
        viewModelScope.launch {
            repository.insertScreening(entity)
        }
    }

    fun syncAllRecords() {
        if (!_isOnline.value) {
            _syncMessage.value = "Sync Failed: Device is offline. Connect to network before synchronizing."
            viewModelScope.launch {
                delay(3000)
                _syncMessage.value = null
            }
            return
        }
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Connecting to Assam State Health Portal / Majuli PHC Gateway..."
            delay(1200)
            repository.markAllSynced()
            _isSyncing.value = false
            _syncMessage.value = "All offline records successfully uploaded and synced."
            delay(3000)
            _syncMessage.value = null
        }
    }

    fun toggleNetwork() {
        _isOnline.value = !_isOnline.value
    }

    private fun generateNewCaseId(): String {
        val rand = (1000..9999).random()
        return "PT-2023-ASM-$rand"
    }

    // ==========================================
    // CLINIXAI MODULE INTEGRATION & STATE FLOWS
    // ==========================================

    val analyticsService = ClinixAnalyticsService()
    private val demoEntities = analyticsService.createSyntheticDemoCases()

    private val _activeModule = MutableStateFlow(ActiveModule.ORTHOSCREEN)
    val activeModule: StateFlow<ActiveModule> = _activeModule.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.AUTHORIZED_USER)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _clinixSearchQuery = MutableStateFlow("")
    val clinixSearchQuery: StateFlow<String> = _clinixSearchQuery.asStateFlow()

    private val _clinixFilter = MutableStateFlow("ALL")
    val clinixFilter: StateFlow<String> = _clinixFilter.asStateFlow()

    // Combined Clinix Cases (Room Screenings + Demo Synthetics + Room Reviews)
    val allClinixCases: StateFlow<List<ClinixCase>> = combine(
        repository.allScreenings,
        repository.allReviews
    ) { screenings, reviews ->
        val reviewMap = reviews.associateBy { it.caseId }
        val combinedEntities = (screenings + demoEntities).distinctBy { it.screeningId }

        val indexed = combinedEntities.map { entity ->
            val isDemo = entity.screeningId.startsWith("DEMO-")
            val review = reviewMap[entity.screeningId]
            val cohort = analyticsService.determineCohort(entity)
            val (isAtypical, reason) = analyticsService.evaluateAtypical(entity)
            val vector = analyticsService.embeddingProvider.extractEmbedding(entity)
            ClinixCase(
                caseId = entity.screeningId,
                screening = entity,
                review = review,
                assignedCohort = cohort,
                isAtypical = isAtypical,
                atypicalReason = reason,
                isDemo = isDemo,
                featureVector = vector
            )
        }

        // Pre-index into vector store for fast Cosine retrieval
        viewModelScope.launch {
            analyticsService.vectorStore.indexCases(
                indexed.map { it.caseId to it.featureVector }
            )
        }

        indexed
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filtered Cases in Case Queue
    val filteredClinixCases: StateFlow<List<ClinixCase>> = combine(
        allClinixCases,
        _clinixSearchQuery,
        _clinixFilter
    ) { cases, query, filter ->
        cases.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.caseId.contains(query, ignoreCase = true) ||
                item.screening.patientId.contains(query, ignoreCase = true) ||
                item.screening.patientName.contains(query, ignoreCase = true) ||
                item.screening.village.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "ALL" -> true
                "HIGH" -> item.screening.riskLevel == "HIGH"
                "MEDIUM" -> item.screening.riskLevel == "MEDIUM"
                "LOW" -> item.screening.riskLevel == "LOW"
                "INCONCLUSIVE" -> item.screening.riskLevel == "INCONCLUSIVE"
                "NEW" -> item.reviewStatus == ReviewStatus.NEW || item.reviewStatus == ReviewStatus.IN_REVIEW
                "REVIEWED" -> item.reviewStatus == ReviewStatus.REVIEWED
                "ATYPICAL" -> item.isAtypical
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Priority Cases for Clinician Overview
    val clinixPriorityCases: StateFlow<List<ClinixCase>> = allClinixCases.combine(_activeModule) { cases, _ ->
        cases.filter { it.screening.riskLevel == "HIGH" || it.isAtypical || it.screening.riskLevel == "INCONCLUSIVE" }
            .sortedWith(
                compareByDescending<ClinixCase> { it.screening.riskLevel == "HIGH" }
                    .thenByDescending { it.reviewStatus == ReviewStatus.NEW }
                    .thenByDescending { it.screening.timestamp }
            )
            .take(6)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Clinix Overview Metrics
    val clinixOverviewMetrics: StateFlow<ClinixOverviewMetrics> = allClinixCases.combine(repository.allReviews) { cases, _ ->
        val newCases = cases.count { it.reviewStatus == ReviewStatus.NEW }
        val highRisk = cases.count { it.screening.riskLevel == "HIGH" }
        val awaiting = cases.count { it.reviewStatus == ReviewStatus.NEW || it.reviewStatus == ReviewStatus.IN_REVIEW }
        val inconclusive = cases.count { it.screening.riskLevel == "INCONCLUSIVE" }
        val atypical = cases.count { it.isAtypical }
        ClinixOverviewMetrics(
            newCasesCount = newCases,
            highRiskCount = highRisk,
            awaitingReviewCount = awaiting,
            inconclusiveCount = inconclusive,
            atypicalCount = atypical
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, ClinixOverviewMetrics(0, 0, 0, 0, 0))

    // Analytical Cohort Info List
    val analyticalCohortInfos: StateFlow<List<AnalyticalCohortInfo>> = allClinixCases.combine(_activeModule) { cases, _ ->
        AnalyticalCohortType.values().map { cohortType ->
            val cohortCases = cases.filter { it.assignedCohort == cohortType }
            val riskDist = cohortCases.groupingBy { it.screening.riskLevel }.eachCount()
            val commonPatterns = when (cohortType) {
                AnalyticalCohortType.COHORT_A -> listOf(
                    "High subjective discomfort (Pain 6-9/10)",
                    "Morning stiffness under 30 minutes",
                    "Relatively preserved 5xSTS transition velocity (<14 sec)"
                )
                AnalyticalCohortType.COHORT_B -> listOf(
                    "Prolonged sit-to-stand transition (>15 sec)",
                    "Marked single-leg offloading / asymmetry (>25%)",
                    "Reduced knee flexion ROM (<100°)"
                )
                AnalyticalCohortType.COHORT_C -> listOf(
                    "Combined severe pain (7+/10) and functional delay",
                    "Elevated morning stiffness (>30 mins)",
                    "Multicomponent mobility impairment"
                )
                AnalyticalCohortType.COHORT_D -> listOf(
                    "Atypical demographic or past traumatic joint injury",
                    "Discordance between reported discomfort and movement velocity",
                    "Safety-paused movement assessments"
                )
            }
            AnalyticalCohortInfo(
                cohortType = cohortType,
                caseCount = cohortCases.size,
                commonPatterns = commonPatterns,
                riskDistribution = riskDist,
                recentCases = cohortCases
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Atypical Cases List
    val atypicalCasesList: StateFlow<List<AtypicalCase>> = allClinixCases.combine(_activeModule) { cases, _ ->
        cases.filter { it.isAtypical }.map { item ->
            AtypicalCase(
                caseId = item.caseId,
                patientId = item.screening.patientId,
                patientName = item.screening.patientName,
                age = item.screening.age,
                riskLevel = item.screening.riskLevel,
                outlierScore = if (item.screening.testSkipped) 0.88 else 0.65,
                nearestCohort = item.assignedCohort.title,
                explanation = item.atypicalReason ?: "Distinct feature variance compared to cluster centers",
                unusualFeatures = listOfNotNull(
                    if (item.screening.testSkipped) "Movement protocol safely bypassed" else null,
                    if (item.screening.asymmetry >= 0.35) "Pronounced offloading asymmetry (${(item.screening.asymmetry * 100).toInt()}%)" else null,
                    if (item.screening.hasInjury) "Documented past knee injury / surgical history" else null,
                    if (item.screening.painScore >= 8 && item.screening.stsTimeSec <= 10.0) "High pain with preserved rapid transition speed" else null
                ),
                screening = item.screening,
                isDemo = item.isDemo
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Similar Cases State
    private val _similarCasesList = MutableStateFlow<List<SimilarCase>>(emptyList())
    val similarCasesList: StateFlow<List<SimilarCase>> = _similarCasesList.asStateFlow()

    fun switchModule(module: ActiveModule) {
        _activeModule.value = module
        when (module) {
            ActiveModule.CLINIXAI -> {
                if (_currentScreen.value !is Screen.ClinixOverview &&
                    _currentScreen.value !is Screen.ClinixCases &&
                    _currentScreen.value !is Screen.ClinixCaseDetail &&
                    _currentScreen.value !is Screen.ClinixSimilarCases &&
                    _currentScreen.value !is Screen.ClinixCohorts &&
                    _currentScreen.value !is Screen.ClinixCohortDetail &&
                    _currentScreen.value !is Screen.ClinixOutliers &&
                    _currentScreen.value !is Screen.ClinixProfile
                ) {
                    _currentScreen.value = Screen.ClinixOverview
                }
            }
            ActiveModule.ORTHOSCREEN -> {
                if (_currentScreen.value is Screen.ClinixOverview ||
                    _currentScreen.value is Screen.ClinixCases ||
                    _currentScreen.value is Screen.ClinixCaseDetail ||
                    _currentScreen.value is Screen.ClinixSimilarCases ||
                    _currentScreen.value is Screen.ClinixCohorts ||
                    _currentScreen.value is Screen.ClinixCohortDetail ||
                    _currentScreen.value is Screen.ClinixOutliers ||
                    _currentScreen.value is Screen.ClinixProfile
                ) {
                    _currentScreen.value = Screen.Dashboard
                }
            }
        }
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
        if (!role.canAccessClinixAI && _activeModule.value == ActiveModule.CLINIXAI) {
            switchModule(ActiveModule.ORTHOSCREEN)
        } else if (!role.canAccessOrthoScreen && _activeModule.value == ActiveModule.ORTHOSCREEN) {
            switchModule(ActiveModule.CLINIXAI)
        }
    }

    fun setClinixSearchQuery(query: String) {
        _clinixSearchQuery.value = query
    }

    fun setClinixFilter(filter: String) {
        _clinixFilter.value = filter
    }

    fun openClinixCase(clinixCase: ClinixCase) {
        _currentScreen.value = Screen.ClinixCaseDetail(clinixCase.caseId)
    }

    fun openSimilarCases(clinixCase: ClinixCase, topK: Int = 5) {
        _currentScreen.value = Screen.ClinixSimilarCases(clinixCase.caseId, topK)
    }

    fun openCohortDetail(cohortType: AnalyticalCohortType) {
        _currentScreen.value = Screen.ClinixCohortDetail(cohortType)
    }

    fun getClinixCaseById(caseId: String): ClinixCase? {
        return allClinixCases.value.firstOrNull { it.caseId == caseId }
    }

    fun getCohortInfo(cohortType: AnalyticalCohortType): AnalyticalCohortInfo {
        return analyticalCohortInfos.value.firstOrNull { it.cohortType == cohortType }
            ?: AnalyticalCohortInfo(cohortType, 0, emptyList(), emptyMap(), emptyList())
    }

    fun getAiAnalyticalSummary(screening: ScreeningEntity): List<String> {
        return analyticsService.generateAiAnalyticalSummary(screening)
    }

    fun computeSimilarCases(caseId: String, topK: Int) {
        viewModelScope.launch {
            val baseCase = getClinixCaseById(caseId) ?: return@launch
            val candidates = allClinixCases.value.filter { it.caseId != caseId }

            val ranked = candidates.map { candidate ->
                val simScore = analyticsService.embeddingProvider.calculateCosineSimilarity(
                    baseCase.featureVector,
                    candidate.featureVector
                )
                val (shared, diffs) = analyticsService.compareCases(baseCase.screening, candidate.screening)
                SimilarCase(
                    caseId = candidate.caseId,
                    patientId = candidate.screening.patientId,
                    age = candidate.screening.age,
                    sex = candidate.screening.sex,
                    screeningRisk = candidate.screening.riskLevel,
                    similarityScore = simScore,
                    sharedPatterns = shared,
                    differences = diffs,
                    stsTimeSec = candidate.screening.stsTimeSec,
                    painScore = candidate.screening.painScore,
                    isDemo = candidate.isDemo
                )
            }
                .sortedByDescending { it.similarityScore }
                .take(topK)

            _similarCasesList.value = ranked
        }
    }

    fun saveClinicianReview(
        caseId: String,
        status: ReviewStatus,
        clinicalNote: String,
        actionTaken: String
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH)
            val review = ClinicalReviewEntity(
                reviewId = "REV-" + UUID.randomUUID().toString().take(8),
                caseId = caseId,
                clinicianId = "DR-BARUAH-091",
                status = status.name,
                clinicalNote = clinicalNote,
                actionTaken = actionTaken,
                reviewedAt = now,
                formattedReviewDate = dateFormat.format(Date(now))
            )
            repository.saveReview(review)
        }
    }
}
