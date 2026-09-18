package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "high_scores")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val score: Int,
    val coins: Int,
    val distanceMeters: Int,
    val maxSpeedKmh: Int,
    val carName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalCoins: Int = 200, // starting bonus coins
    val selectedCarId: String = "turbo_blaze",
    val unlockedCarsCsv: String = "turbo_blaze", // comma-separated ids
    val speedUpgradeLevel: Int = 1,
    val handlingUpgradeLevel: Int = 1,
    val shieldUpgradeLevel: Int = 1,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val languageBn: Boolean = false
)
