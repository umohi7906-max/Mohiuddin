package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Query("SELECT * FROM high_scores ORDER BY score DESC LIMIT 20")
    fun getTopScores(): Flow<List<ScoreEntity>>

    @Query("SELECT MAX(score) FROM high_scores")
    fun getHighestScore(): Flow<Int?>

    @Query("SELECT SUM(distanceMeters) FROM high_scores")
    fun getTotalDistance(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM high_scores")
    fun getTotalGamesPlayed(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: ScoreEntity): Long
}

@Dao
interface PlayerProfileDao {
    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1")
    suspend fun getProfileSync(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: PlayerProfileEntity)
}
