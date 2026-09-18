package com.example.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(
    private val scoreDao: ScoreDao,
    private val playerProfileDao: PlayerProfileDao
) {
    val topScores: Flow<List<ScoreEntity>> = scoreDao.getTopScores()
    val highestScore: Flow<Int> = scoreDao.getHighestScore().map { it ?: 0 }
    val totalDistance: Flow<Int> = scoreDao.getTotalDistance().map { it ?: 0 }
    val totalGamesPlayed: Flow<Int> = scoreDao.getTotalGamesPlayed()
    val playerProfile: Flow<PlayerProfileEntity> = playerProfileDao.getProfile().map {
        it ?: PlayerProfileEntity()
    }

    suspend fun recordRun(
        score: Int,
        coins: Int,
        distanceMeters: Int,
        maxSpeedKmh: Int,
        carName: String
    ) {
        scoreDao.insertScore(
            ScoreEntity(
                score = score,
                coins = coins,
                distanceMeters = distanceMeters,
                maxSpeedKmh = maxSpeedKmh,
                carName = carName
            )
        )
        // Update coins in player profile
        val currentProfile = playerProfileDao.getProfileSync() ?: PlayerProfileEntity()
        playerProfileDao.saveProfile(
            currentProfile.copy(
                totalCoins = currentProfile.totalCoins + coins
            )
        )
    }

    suspend fun updateProfile(updated: PlayerProfileEntity) {
        playerProfileDao.saveProfile(updated)
    }

    suspend fun unlockCar(carId: String, cost: Int): Boolean {
        val current = playerProfileDao.getProfileSync() ?: PlayerProfileEntity()
        if (current.totalCoins >= cost) {
            val unlockedSet = current.unlockedCarsCsv.split(",").map { it.trim() }.toMutableSet()
            unlockedSet.add(carId)
            playerProfileDao.saveProfile(
                current.copy(
                    totalCoins = current.totalCoins - cost,
                    unlockedCarsCsv = unlockedSet.joinToString(","),
                    selectedCarId = carId
                )
            )
            return true
        }
        return false
    }

    suspend fun selectCar(carId: String) {
        val current = playerProfileDao.getProfileSync() ?: PlayerProfileEntity()
        playerProfileDao.saveProfile(current.copy(selectedCarId = carId))
    }

    suspend fun upgradeStat(statType: String, cost: Int): Boolean {
        val current = playerProfileDao.getProfileSync() ?: PlayerProfileEntity()
        if (current.totalCoins >= cost) {
            val updated = when (statType) {
                "speed" -> current.copy(
                    totalCoins = current.totalCoins - cost,
                    speedUpgradeLevel = current.speedUpgradeLevel + 1
                )
                "handling" -> current.copy(
                    totalCoins = current.totalCoins - cost,
                    handlingUpgradeLevel = current.handlingUpgradeLevel + 1
                )
                "shield" -> current.copy(
                    totalCoins = current.totalCoins - cost,
                    shieldUpgradeLevel = current.shieldUpgradeLevel + 1
                )
                else -> current
            }
            playerProfileDao.saveProfile(updated)
            return true
        }
        return false
    }
}
