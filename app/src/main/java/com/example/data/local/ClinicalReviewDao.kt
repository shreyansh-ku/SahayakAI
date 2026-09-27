package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicalReviewDao {
    @Query("SELECT * FROM clinical_reviews ORDER BY reviewedAt DESC")
    fun getAllReviews(): Flow<List<ClinicalReviewEntity>>

    @Query("SELECT * FROM clinical_reviews WHERE caseId = :caseId LIMIT 1")
    fun getReviewForCase(caseId: String): Flow<ClinicalReviewEntity?>

    @Query("SELECT * FROM clinical_reviews WHERE caseId = :caseId LIMIT 1")
    suspend fun getReviewForCaseOnce(caseId: String): ClinicalReviewEntity?

    @Query("SELECT COUNT(*) FROM clinical_reviews WHERE status = 'REVIEWED'")
    fun getReviewedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM clinical_reviews WHERE status = 'IN_REVIEW'")
    fun getInReviewCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReview(review: ClinicalReviewEntity)

    @Query("DELETE FROM clinical_reviews WHERE reviewId = :reviewId")
    suspend fun deleteReview(reviewId: String)
}
