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

sealed class Screen {
    object Login : Screen()
    object Dashboard : Screen()
    object Registration : Screen()       // Step 1
    object Questionnaire : Screen()      // Step 2
    object MovementAssessment : Screen() // Step 3
    object Result : Screen()             // Step 4
    object SyncCenter : Screen()
    data class PatientDetail(val screening: ScreeningEntity) : Screen()
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
        repository = OrthoScreenRepository(db.screeningDao())
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
    val consentAccepted = MutableStateFlow(true)
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
        consentAccepted.value = true

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

    private fun generateNewCaseId(): String {
        val rand = (1000..9999).random()
        return "PT-2023-ASM-$rand"
    }
}
