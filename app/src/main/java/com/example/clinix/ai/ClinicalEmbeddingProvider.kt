package com.example.clinix.ai

import com.example.data.local.ScreeningEntity
import kotlin.math.sqrt

/**
 * Abstraction for clinical feature representation.
 * Can be implemented by deterministic on-device feature extraction or deep learning embeddings.
 */
interface ClinicalEmbeddingProvider {
    fun extractEmbedding(screening: ScreeningEntity): FloatArray
    fun calculateCosineSimilarity(vecA: FloatArray, vecB: FloatArray): Double
}

class MockEmbeddingProvider : ClinicalEmbeddingProvider {

    override fun extractEmbedding(screening: ScreeningEntity): FloatArray {
        // 14 normalized clinical dimensions:
        // [0] Age / 100
        // [1] BMI / 50
        // [2] Sex (1.0 for F, 0.0 for M)
        // [3] Pain / 10
        // [4] Stiffness / 60
        // [5] Walking difficulty / 4
        // [6] Stairs difficulty / 4
        // [7] Stand difficulty / 4
        // [8] Crepitus (1.0 or 0.0)
        // [9] Night pain / 4
        // [10] 5xSTS time / 30
        // [11] Knee ROM / 140
        // [12] Asymmetry score (0.0 to 1.0)
        // [13] Has previous injury (1.0 or 0.0)

        val ageNorm = (screening.age / 100f).coerceIn(0f, 1f)
        val bmiNorm = (screening.bmi.toFloat() / 50f).coerceIn(0f, 1f)
        val sexVal = if (screening.sex.equals("Female", ignoreCase = true)) 1f else 0f
        val painNorm = (screening.painScore / 10f).coerceIn(0f, 1f)
        val stiffNorm = (screening.stiffnessDuration / 60f).coerceIn(0f, 1f)
        val walkNorm = (screening.walkingLimitation / 4f).coerceIn(0f, 1f)
        val stairNorm = (screening.stairsDifficulty / 4f).coerceIn(0f, 1f)
        val standNorm = (screening.standDifficulty / 4f).coerceIn(0f, 1f)
        val crepitusVal = if (screening.crepitus > 0) 1f else 0f
        val nightNorm = (screening.nightPain / 4f).coerceIn(0f, 1f)
        val stsNorm = (screening.stsTimeSec.toFloat() / 30f).coerceIn(0f, 1f)
        val romNorm = (screening.kneeRom.toFloat() / 140f).coerceIn(0f, 1f)
        val asymNorm = screening.asymmetry.toFloat().coerceIn(0f, 1f)
        val injuryVal = if (screening.hasInjury) 1f else 0f

        val raw = floatArrayOf(
            ageNorm, bmiNorm, sexVal, painNorm, stiffNorm,
            walkNorm, stairNorm, standNorm, crepitusVal, nightNorm,
            stsNorm, romNorm, asymNorm, injuryVal
        )

        // L2 Normalize
        var sumSquares = 0.0
        for (v in raw) {
            sumSquares += (v * v)
        }
        val norm = sqrt(sumSquares).toFloat().coerceAtLeast(1e-6f)
        return FloatArray(raw.size) { i -> raw[i] / norm }
    }

    override fun calculateCosineSimilarity(vecA: FloatArray, vecB: FloatArray): Double {
        if (vecA.isEmpty() || vecB.isEmpty() || vecA.size != vecB.size) return 0.0
        var dot = 0.0
        var normA = 0.0
        var normB = 0.0
        for (i in vecA.indices) {
            dot += (vecA[i] * vecB[i])
            normA += (vecA[i] * vecA[i])
            normB += (vecB[i] * vecB[i])
        }
        val denominator = (sqrt(normA) * sqrt(normB))
        if (denominator <= 1e-6) return 0.0
        return (dot / denominator).coerceIn(0.0, 1.0)
    }
}

/**
 * Production provider contract for remote inference or on-device ML model integration.
 */
class ProductionEmbeddingProvider(
    private val fallback: MockEmbeddingProvider = MockEmbeddingProvider()
) : ClinicalEmbeddingProvider {
    override fun extractEmbedding(screening: ScreeningEntity): FloatArray {
        // Will interface with on-device or backend microservice embedding pipeline
        return fallback.extractEmbedding(screening)
    }

    override fun calculateCosineSimilarity(vecA: FloatArray, vecB: FloatArray): Double {
        return fallback.calculateCosineSimilarity(vecA, vecB)
    }
}
