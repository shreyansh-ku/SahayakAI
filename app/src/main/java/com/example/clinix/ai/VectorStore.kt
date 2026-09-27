package com.example.clinix.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Vector search abstraction to decouple the mobile client from underlying vector stores (FAISS, pgvector, or local in-memory index).
 */
interface VectorStore {
    suspend fun indexCases(cases: List<Pair<String, FloatArray>>)
    suspend fun searchSimilar(targetVector: FloatArray, topK: Int, excludeCaseId: String): List<Pair<String, Double>>
}

class MockVectorStore(
    private val embeddingProvider: ClinicalEmbeddingProvider = MockEmbeddingProvider()
) : VectorStore {
    private val memoryIndex = mutableMapOf<String, FloatArray>()

    override suspend fun indexCases(cases: List<Pair<String, FloatArray>>) = withContext(Dispatchers.Default) {
        synchronized(memoryIndex) {
            cases.forEach { (caseId, vector) ->
                memoryIndex[caseId] = vector
            }
        }
    }

    override suspend fun searchSimilar(
        targetVector: FloatArray,
        topK: Int,
        excludeCaseId: String
    ): List<Pair<String, Double>> = withContext(Dispatchers.Default) {
        val candidates: List<Pair<String, FloatArray>>
        synchronized(memoryIndex) {
            candidates = memoryIndex.filterKeys { it != excludeCaseId }.toList()
        }

        candidates.map { (id, vector) ->
            val sim = embeddingProvider.calculateCosineSimilarity(targetVector, vector)
            Pair(id, sim)
        }
            .sortedByDescending { it.second }
            .take(topK)
    }
}

/**
 * Contract implementation stub for FAISS backend integration.
 */
class FAISSVectorStore : VectorStore {
    private val fallback = MockVectorStore()
    override suspend fun indexCases(cases: List<Pair<String, FloatArray>>) = fallback.indexCases(cases)
    override suspend fun searchSimilar(targetVector: FloatArray, topK: Int, excludeCaseId: String) =
        fallback.searchSimilar(targetVector, topK, excludeCaseId)
}

/**
 * Contract implementation stub for PostgreSQL + pgvector backend integration.
 */
class PgVectorStore : VectorStore {
    private val fallback = MockVectorStore()
    override suspend fun indexCases(cases: List<Pair<String, FloatArray>>) = fallback.indexCases(cases)
    override suspend fun searchSimilar(targetVector: FloatArray, topK: Int, excludeCaseId: String) =
        fallback.searchSimilar(targetVector, topK, excludeCaseId)
}
