package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.RiskLevel
import com.example.data.model.SyncStatus

@Entity(tableName = "screenings")
data class ScreeningEntity(
    @PrimaryKey val screeningId: String,
    val patientId: String,
    val patientName: String,
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
    val comorbidities: String, // comma-separated
    val consentGiven: Boolean,
    val attestingWorker: String,
    val timestamp: Long,
    val formattedDate: String,

    // Questionnaire
    val painScore: Int,
    val stiffnessDuration: Int,
    val walkingLimitation: Int,
    val stairsDifficulty: Int,
    val standDifficulty: Int,
    val crepitus: Int,
    val nightPain: Int,
    val questionnaireTotal: Int,

    // Movement Features
    val stsTimeSec: Double,
    val repCount: Int,
    val kneeAngle: Double,
    val kneeRom: Double,
    val asymmetry: Double,
    val testSkipped: Boolean,
    val skipReason: String?,

    // Results
    val riskLevel: String, // LOW, MEDIUM, HIGH, INCONCLUSIVE
    val riskScore: Double,
    val confidence: Double,
    val referralDestination: String,
    val counselingSummary: String,
    val syncStatus: String // UNSYNCED, SYNCED
)
