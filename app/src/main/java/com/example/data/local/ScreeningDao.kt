package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreeningDao {
    @Query("SELECT * FROM screenings ORDER BY timestamp DESC")
    fun getAllScreenings(): Flow<List<ScreeningEntity>>

    @Query("SELECT * FROM screenings WHERE screeningId = :id LIMIT 1")
    suspend fun getScreeningById(id: String): ScreeningEntity?

    @Query("SELECT * FROM screenings WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getScreeningsForPatient(patientId: String): Flow<List<ScreeningEntity>>

    @Query("SELECT COUNT(*) FROM screenings")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM screenings WHERE riskLevel = 'HIGH'")
    fun getHighRiskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM screenings WHERE riskLevel = 'MEDIUM'")
    fun getMedRiskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM screenings WHERE syncStatus = 'UNSYNCED'")
    fun getUnsyncedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreening(screening: ScreeningEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(screenings: List<ScreeningEntity>)

    @Update
    suspend fun updateScreening(screening: ScreeningEntity)

    @Query("UPDATE screenings SET syncStatus = 'SYNCED' WHERE syncStatus = 'UNSYNCED'")
    suspend fun markAllSynced()

    @Query("DELETE FROM screenings WHERE screeningId = :id")
    suspend fun deleteScreening(id: String)
}
