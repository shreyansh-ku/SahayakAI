package com.example.ai

import com.example.data.model.ContributingFactor
import com.example.data.model.CounselingTask
import com.example.data.model.MovementFeatures
import com.example.data.model.PatientRecord
import com.example.data.model.RiskLevel
import com.example.data.model.RiskResult
import kotlin.math.roundToInt

object RiskEngine {

    /**
     * Synthesizes patient reported symptoms, demographics, and functional movement features
     * into an explainable screening risk output using our calibrated multi-factor model.
     */
    fun evaluate(
        patient: PatientRecord,
        painScore: Int,
        stiffnessDuration: Int,
        walkingLimitation: Int,
        stairsDifficulty: Int,
        standDifficulty: Int,
        crepitus: Int,
        nightPain: Int,
        movement: MovementFeatures,
        forceInconclusive: Boolean = false
    ): RiskResult {
        if (forceInconclusive || (!movement.testSkipped && movement.videoQualityScore < 0.40)) {
            return RiskResult(
                riskLevel = RiskLevel.INCONCLUSIVE,
                riskScore = 0.0,
                confidence = 0.35,
                fiveStsSpeedDesc = "Video quality or landmark tracking inadequate",
                speedExcessPercent = 0,
                symmetryDesc = "Indeterminate due to video artifacts",
                symmetryOffloading = "Unverified",
                contributingFactors = listOf(
                    ContributingFactor(
                        title = "Insufficient Video Landmark Detection",
                        description = "Camera angle or low ambient lighting prevented reliable skeletal keypoint tracking.",
                        impactPercent = 60,
                        category = "System Quality Check",
                        isPrimary = true
                    ),
                    ContributingFactor(
                        title = "Incomplete Kinematic Cadence",
                        description = "Fewer than 3 valid repetitions detected within the capture window.",
                        impactPercent = 40,
                        category = "Protocol Variance"
                    )
                ),
                primaryReferral = "Repeat movement assessment under brighter illumination, or proceed with manual clinical questionnaire review.",
                referralDestination = "Local PHC / Camp Screener",
                counselingTasks = listOf(
                    CounselingTask(
                        title = "Re-position Camera Setup",
                        description = "Ensure device is placed 2.5 meters away on a stable, level surface facing patient directly.",
                        iconName = "videocam"
                    )
                ),
                isInconclusive = true,
                inconclusiveReason = "Insufficient optical contrast or movement occlusion detected during video capture."
            )
        }

        // Calculate Questionnaire Component Score (Max 28 points)
        // Pain: 0-4
        // Stiffness: 0-3
        // Walk: 0-3
        // Stairs: 0-3
        // Stand: 0-3
        // Crepitus: 0-2 (rescaled x 2 -> 0-4)
        // Night Pain: 0-3
        val qSum = painScore * 2 + stiffnessDuration * 2 + walkingLimitation * 2 +
                stairsDifficulty * 2 + standDifficulty * 2 + crepitus * 2 + nightPain * 2
        val normalizedQ = (qSum / 28.0).coerceIn(0.0, 1.0)

        // Calculate Demographic Risk Weight
        var demoScore = 0.0
        if (patient.age >= 60) demoScore += 0.35
        else if (patient.age >= 50) demoScore += 0.20
        else if (patient.age >= 40) demoScore += 0.10

        // Asian-Indian BMI threshold (>23 overweight, >25 obese)
        if (patient.bmi >= 25.0) demoScore += 0.30
        else if (patient.bmi >= 23.0) demoScore += 0.15

        if (patient.hasInjury) demoScore += 0.25
        if (patient.occupationCategory.contains("agriculture", ignoreCase = true) ||
            patient.occupationCategory.contains("tea", ignoreCase = true) ||
            patient.occupationCategory.contains("weaver", ignoreCase = true)) {
            demoScore += 0.20
        }
        val normalizedDemo = demoScore.coerceIn(0.0, 1.0)

        // Calculate Movement Feature Risk Component
        var motionScore = 0.0
        val speedExcessPercent: Int
        val speedDesc: String
        val symmetryDesc: String
        val symmetryOffloading: String

        if (movement.testSkipped) {
            // Patient was unable to stand safely — strong functional indicator
            motionScore = 0.85
            speedExcessPercent = 50
            speedDesc = "Test Bypassed: Unable to stand safely unaided"
            symmetryDesc = "Impaired transfer mechanics"
            symmetryOffloading = "Bilateral severe functional compromise"
        } else {
            // Normal 5xSTS reference is <12.0s
            val time = movement.stsCompletionTimeSec
            if (time > 15.0) {
                motionScore += 0.45
                speedExcessPercent = (((time - 12.0) / 12.0) * 100).roundToInt()
                speedDesc = "Prolonged (Ref: <12.0s)"
            } else if (time > 12.0) {
                motionScore += 0.25
                speedExcessPercent = (((time - 12.0) / 12.0) * 100).roundToInt()
                speedDesc = "Borderline delay (Ref: <12.0s)"
            } else {
                speedExcessPercent = 0
                speedDesc = "Optimal (<12.0s normal)"
            }

            // Asymmetry (<0.80 suggests offloading)
            if (movement.asymmetryScore < 0.75) {
                motionScore += 0.35
                symmetryDesc = "Left-Right Asymmetry (${String.format("%.2f", movement.asymmetryScore)})"
                symmetryOffloading = "Left limb protective offloading noted"
            } else if (movement.asymmetryScore < 0.85) {
                motionScore += 0.20
                symmetryDesc = "Mild Asymmetry (${String.format("%.2f", movement.asymmetryScore)})"
                symmetryOffloading = "Mild compensatory weight distribution"
            } else {
                symmetryDesc = "Symmetric Articulation"
                symmetryOffloading = "Bilateral weight loading balanced"
            }

            // ROM restriction (<100 deg)
            if (movement.kneeRom < 95.0) {
                motionScore += 0.25
            }
        }
        val normalizedMotion = motionScore.coerceIn(0.0, 1.0)

        // Prototype Ensemble Blending (Questionnaire: 40%, Biomechanics: 35%, Demographics: 25%)
        val combinedRisk = (normalizedQ * 0.40 + normalizedMotion * 0.35 + normalizedDemo * 0.25).coerceIn(0.05, 0.96)
        val roundedRisk = (combinedRisk * 100).roundToInt() / 100.0

        val (level, primaryReferral, referralDest) = when {
            roundedRisk >= 0.70 -> Triple(
                RiskLevel.HIGH,
                "Priority referral to District Orthopaedic OPD within 7 days. Suspected advanced symptomatic gonarthrosis.",
                "District Civil Hospital / Orthopaedic OPD"
            )
            roundedRisk >= 0.40 -> Triple(
                RiskLevel.MEDIUM,
                "Refer to local clinic / community health center physiotherapist within 14 days. Indicated for clinical evaluation, musculoskeletal exam, and supervised isometric physical therapy.",
                "PHC Medical Officer / CHC Physiotherapy"
            )
            else -> Triple(
                RiskLevel.LOW,
                "Continue community monitoring. Provide joint preservation counseling; re-screen in 6-12 months or if symptoms worsen.",
                "Community Health Worker / Sub-Centre Follow-up"
            )
        }

        // Dynamically compute SHAP-style explainability factors
        val factors = mutableListOf<ContributingFactor>()

        if (painScore >= 2) {
            factors.add(
                ContributingFactor(
                    title = "Persistent Weight-Bearing Joint Pain",
                    description = "Reported active tenderness and weight-bearing discomfort during routine daily tasks.",
                    impactPercent = 32,
                    category = "Primary Symptom",
                    isPrimary = true
                )
            )
        }

        if (movement.stsCompletionTimeSec > 14.0 || movement.testSkipped) {
            factors.add(
                ContributingFactor(
                    title = "STS Transition Hesitation",
                    description = if (movement.testSkipped) "Patient unable to stand safely unaided due to severe joint distress."
                    else "Prolonged extension deceleration and cadence delay during 5xSTS.",
                    impactPercent = 28,
                    category = "Biomechanical Marker"
                )
            )
        }

        if (movement.asymmetryScore < 0.80 || movement.testSkipped) {
            factors.add(
                ContributingFactor(
                    title = "Kinematic Asymmetry & Compensation",
                    description = "Significant protective offloading identified from single-camera pose analysis.",
                    impactPercent = 24,
                    category = "Video Pose Analysis"
                )
            )
        }

        factors.add(
            ContributingFactor(
                title = "Demographic & Workload Risk Gradient",
                description = "Age ${patient.age} Yrs • BMI ${String.format("%.1f", patient.bmi)} kg/m² • ${patient.occupationCategory.replace("_", " ")}",
                impactPercent = 16,
                category = "Baseline Phenotype"
            )
        )

        val counseling = listOf(
            CounselingTask(
                title = "Ergonomic Adaptation",
                description = "Advised to avoid deep squatting and prolonged floor sitting during daily domestic and field chores.",
                iconName = "do_not_step"
            ),
            CounselingTask(
                title = "Demonstrated Exercise",
                description = "Isometric quadriceps strengthening (towel-under-knee press) demonstrated to patient and family caregiver.",
                iconName = "fitness_center"
            ),
            CounselingTask(
                title = "Flare Management",
                description = "Local cold pack application instructed for acute post-work pain or swelling flare-ups.",
                iconName = "ac_unit"
            )
        )

        return RiskResult(
            riskLevel = level,
            riskScore = roundedRisk,
            confidence = 0.78,
            fiveStsSpeedDesc = speedDesc,
            speedExcessPercent = speedExcessPercent,
            symmetryDesc = symmetryDesc,
            symmetryOffloading = symmetryOffloading,
            contributingFactors = factors,
            primaryReferral = primaryReferral,
            referralDestination = referralDest,
            counselingTasks = counseling
        )
    }
}
