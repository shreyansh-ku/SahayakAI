package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.MovementFeatureExtractor
import com.example.ai.RiskEngine
import com.example.data.model.MovementFeatures
import com.example.data.model.PatientRecord
import com.example.data.model.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("OrthoScreen AI", appName)
    }

    @Test
    fun `risk engine computes high risk for symptomatic elderly patient with 5xSTS delay`() {
        val patient = PatientRecord(
            patientId = "PT-TEST-001",
            name = "Test Patient",
            guardianName = "Guardian",
            village = "Majuli",
            age = 65,
            sex = "Female",
            heightCm = 152.0,
            weightKg = 68.0,
            bmi = 29.4,
            hasInjury = true,
            injuryDetails = "Prior trauma",
            occupationCategory = "Agricultural field worker",
            comorbidities = listOf("Hypertension"),
            consentGiven = true,
            attestingWorker = "ASHA Tester"
        )

        val movement = MovementFeatures(
            stsCompletionTimeSec = 17.2,
            repetitionCount = 5,
            kneeAngle = 82.0,
            kneeRom = 90.0,
            asymmetryScore = 0.68
        )

        val result = RiskEngine.evaluate(
            patient = patient,
            painScore = 3,
            stiffnessDuration = 2,
            walkingLimitation = 2,
            stairsDifficulty = 2,
            standDifficulty = 2,
            crepitus = 2,
            nightPain = 2,
            movement = movement
        )

        assertEquals(RiskLevel.HIGH, result.riskLevel)
        assertTrue(result.riskScore > 0.70)
        assertTrue(result.disclaimer.contains("Screening result — not a diagnosis"))
        assertTrue(result.contributingFactors.isNotEmpty())
        assertNotNull(result.primaryReferral)
    }

    @Test
    fun `risk engine handles inconclusive assessment on low video quality`() {
        val patient = PatientRecord(
            patientId = "PT-TEST-002",
            name = "Inconclusive Patient",
            guardianName = "Guardian",
            village = "Majuli",
            age = 50,
            sex = "Male",
            heightCm = 165.0,
            weightKg = 60.0,
            bmi = 22.0,
            hasInjury = false,
            injuryDetails = "",
            occupationCategory = "Desk work",
            comorbidities = emptyList(),
            consentGiven = true,
            attestingWorker = "ASHA Tester"
        )

        val movement = MovementFeatures(
            stsCompletionTimeSec = 11.0,
            videoQualityScore = 0.20 // degraded lighting/occlusion
        )

        val result = RiskEngine.evaluate(
            patient = patient,
            painScore = 1,
            stiffnessDuration = 0,
            walkingLimitation = 0,
            stairsDifficulty = 0,
            standDifficulty = 0,
            crepitus = 0,
            nightPain = 0,
            movement = movement,
            forceInconclusive = true
        )

        assertEquals(RiskLevel.INCONCLUSIVE, result.riskLevel)
        assertTrue(result.isInconclusive)
    }

    @Test
    fun `movement feature extractor calculates correct angle`() {
        val hip = MovementFeatureExtractor.Point2D(0f, 10f)
        val knee = MovementFeatureExtractor.Point2D(0f, 0f)
        val ankle = MovementFeatureExtractor.Point2D(10f, 0f)

        val angle = MovementFeatureExtractor.calculateKneeAngle(hip, knee, ankle)
        assertEquals(90.0, angle, 0.5)
    }
}
