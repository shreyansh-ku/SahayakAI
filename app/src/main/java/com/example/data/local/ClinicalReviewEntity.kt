package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clinical_reviews")
data class ClinicalReviewEntity(
    @PrimaryKey
    val reviewId: String,
    val caseId: String,
    val clinicianId: String,
    val status: String, // NEW, IN_REVIEW, REVIEWED, REPEAT_REQUIRED, REFERRED
    val clinicalNote: String,
    val actionTaken: String,
    val reviewedAt: Long,
    val formattedReviewDate: String
)
