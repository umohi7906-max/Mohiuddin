package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.local.AppDatabase
import com.example.data.local.GameRepository
import com.example.data.local.PlayerProfileEntity
import com.example.data.local.ScoreEntity
import com.example.model.CarCatalog
import com.example.model.CarModel
import com.example.model.CollectItemType
import com.example.model.CollectibleItem
import com.example.model.GameScreen
import com.example.model.NearMissToast
import com.example.model.Particle
import com.example.model.TrafficCar
import com.example.model.TrafficType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class GamePlayState(
    val currentScreen: GameScreen = GameScreen.MAIN_MENU,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,
    val score: Int = 0,
    val coinsThisRun: Int = 0,
    val distanceMeters: Float = 0f,
    val currentSpeedKmh: Float = 90f,
    val maxSpeedReached: Float = 90f,
    val nitroFuel: Float = 1.0f,
    val isNitroActive: Boolean = false,
    val playerLaneX: Float = 0.5f, // 0.1f (left) to 0.9f (right)
    val steerTilt: Float = 0f,
    val health: Int = 3,
    val maxHealth: Int = 3,
    val hasShield: Boolean = false,
    val hasMagnet: Boolean = false,
    val magnetTimerSec: Float = 0f,
    val comboMultiplier: Int = 1,
    val nearMissCount: Int = 0,
    val activeToasts: List<NearMissToast> = emptyList(),
    val trafficCars: List<TrafficCar> = emptyList(),
    val collectibles: List<CollectibleItem> = emptyList(),
    val particles: List<Particle> = emptyList(),
    val roadScroll: Float = 0f,
    val selectedCar: CarModel = CarCatalog.ALL_CARS.first(),
    val animTicks: Long = 0L,
    val newHighScoreAlert: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = GameRepository(db.scoreDao(), db.playerProfileDao())
    val soundManager = SoundManager(application)

    private val _state = MutableStateFlow(GamePlayState())
    val state: StateFlow<GamePlayState> = _state.asStateFlow()

    val topScores: StateFlow<List<ScoreEntity>> = repository.topScores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val highestScore: StateFlow<Int> = repository.highestScore
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val playerProfile: StateFlow<PlayerProfileEntity> = repository.playerProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerProfileEntity())

    private var steerLeftHeld = false
    private var steerRightHeld = false
    private var nitroHeld = false
    private var nextTrafficSpawnDistance = 150f
    private var nextCollectibleSpawnDistance = 80f
    private var trafficIdCounter = 0L
    private var itemIdCounter = 0L

    init {
        viewModelScope.launch {
            playerProfile.collect { profile ->
                val car = CarCatalog.getCarById(profile.selectedCarId)
                val healthBonus = profile.shieldUpgradeLevel - 1
                val maxHp = car.baseHealth + healthBonus
                soundManager.isSoundEnabled = profile.soundEnabled
                soundManager.isVibrationEnabled = profile.vibrationEnabled

                _state.value = _state.value.copy(
                    selectedCar = car,
                    maxHealth = maxHp,
                    health = if (!_state.value.isPlaying) maxHp else _state.value.health
                )
            }
        }
    }

    fun setScreen(screen: GameScreen) {
        _state.value = _state.value.copy(currentScreen = screen)
    }

    fun startGame() {
        val car = _state.value.selectedCar
        val profile = playerProfile.value
        val speedBonus = (profile.speedUpgradeLevel - 1) * 10
        val maxHp = car.baseHealth + (profile.shieldUpgradeLevel - 1)

        steerLeftHeld = false
        steerRightHeld = false
        nitroHeld = false
        nextTrafficSpawnDistance = 120f
        nextCollectibleSpawnDistance = 80f

        _state.value = GamePlayState(
            currentScreen = GameScreen.PLAYING,
            isPlaying = true,
            isPaused = false,
            isGameOver = false,
            score = 0,
            coinsThisRun = 0,
            distanceMeters = 0f,
            currentSpeedKmh = 90f + speedBonus * 0.5f,
            maxSpeedReached = 90f + speedBonus * 0.5f,
            nitroFuel = 1.0f,
            isNitroActive = false,
            playerLaneX = 0.5f,
            steerTilt = 0f,
            health = maxHp,
            maxHealth = maxHp,
            hasShield = false,
            hasMagnet = car.id == "golden_fury", // Perk for Golden Fury!
            magnetTimerSec = if (car.id == "golden_fury") 9999f else 0f,
            comboMultiplier = 1,
            nearMissCount = 0,
            activeToasts = emptyList(),
            trafficCars = emptyList(),
            collectibles = emptyList(),
            particles = emptyList(),
            roadScroll = 0f,
            selectedCar = car,
            newHighScoreAlert = false
        )
    }

    fun pauseGame() {
        if (_state.value.isPlaying && !_state.value.isGameOver) {
            _state.value = _state.value.copy(isPaused = true)
        }
    }

    fun resumeGame() {
        if (_state.value.isPlaying && !_state.value.isGameOver) {
            _state.value = _state.value.copy(isPaused = false)
        }
    }

    fun setSteerLeft(pressed: Boolean) {
        steerLeftHeld = pressed
    }

    fun setSteerRight(pressed: Boolean) {
        steerRightHeld = pressed
    }

    fun setNitro(pressed: Boolean) {
        nitroHeld = pressed
        if (pressed && _state.value.nitroFuel > 0.15f) {
            soundManager.playNitro()
        }
    }

    fun honkHorn() {
        soundManager.playHorn()
        // Honking causes cars directly ahead of player to signal and steer sideways slightly
        val current = _state.value
        val updatedTraffic = current.trafficCars.map { car ->
            if (car.yPixels in 200f..700f && abs(car.xPercent - current.playerLaneX) < 0.22f) {
                val steerToRight = car.xPercent < 0.5f
                car.copy(
                    indicatorLeft = !steerToRight,
                    indicatorRight = steerToRight,
                    xPercent = if (steerToRight) min(0.85f, car.xPercent + 0.18f) else max(0.15f, car.xPercent - 0.18f)
                )
            } else car
        }
        _state.value = _state.value.copy(trafficCars = updatedTraffic)
    }

    fun onPlayerDirectSteer(targetPercent: Float) {
        val clamped = targetPercent.coerceIn(0.14f, 0.86f)
        val delta = clamped - _state.value.playerLaneX
        val tilt = (delta * 5f).coerceIn(-1f, 1f)
        _state.value = _state.value.copy(
            playerLaneX = clamped,
            steerTilt = tilt
        )
    }

    // Main Engine Tick called on each Compose frame
    fun onGameTick(deltaSeconds: Float, screenHeightPx: Float) {
        val s = _state.value
        if (!s.isPlaying || s.isPaused || s.isGameOver) return

        val profile = playerProfile.value
        val speedBonus = (profile.speedUpgradeLevel - 1) * 8f
        val handlingMultiplier = s.selectedCar.handling * (1f + (profile.handlingUpgradeLevel - 1) * 0.12f)
        val maxCarSpeed = s.selectedCar.topSpeedKmh + speedBonus

        // 1. Calculate Target Speed & Nitro
        var isNitro = false
        var currentNitro = s.nitroFuel

        if (nitroHeld && currentNitro > 0.05f) {
            isNitro = true
            currentNitro = max(0f, currentNitro - (deltaSeconds * 0.28f))
        } else {
            // gradual refill
            currentNitro = min(1.0f, currentNitro + (deltaSeconds * 0.08f))
        }

        val targetSpeed = if (isNitro) maxCarSpeed + 50f else maxCarSpeed
        val accelRate = if (isNitro) 120f else 35f
        val newSpeed = if (s.currentSpeedKmh < targetSpeed) {
            min(targetSpeed, s.currentSpeedKmh + accelRate * deltaSeconds)
        } else {
            max(targetSpeed, s.currentSpeedKmh - 40f * deltaSeconds)
        }

        // Distance and Score progression
        val distanceDeltaMeters = (newSpeed * (1000f / 3600f)) * deltaSeconds
        val newDistance = s.distanceMeters + distanceDeltaMeters
        val scoreIncrement = ((newSpeed / 10f) * s.comboMultiplier * (if (isNitro) 2f else 1f) * deltaSeconds).toInt()
        val newScore = s.score + max(1, scoreIncrement)

        // 2. Steering handling
        var newX = s.playerLaneX
        var tilt = 0f
        val steerSpeed = 0.65f * handlingMultiplier * deltaSeconds

        if (steerLeftHeld) {
            newX = max(0.14f, newX - steerSpeed)
            tilt = -1f
        } else if (steerRightHeld) {
            newX = min(0.86f, newX + steerSpeed)
            tilt = 1f
        }

        // 3. Road scroll
        val scrollSpeedPixels = newSpeed * 8.5f * deltaSeconds
        val newScroll = (s.roadScroll + scrollSpeedPixels) % 2000f

        // 4. Update and spawn traffic
        val trafficList = s.trafficCars.toMutableList()
        var updatedNearMissCount = s.nearMissCount
        var combo = s.comboMultiplier
        val toasts = s.activeToasts.filter { System.currentTimeMillis() - it.timestamp < 1800 }.toMutableList()

        // Spawn traffic if distance threshold reached
        if (newDistance >= nextTrafficSpawnDistance) {
            trafficIdCounter++
            val lanes = listOf(0.18f, 0.39f, 0.61f, 0.82f)
            val chosenLane = lanes.random()
            val trafficType = TrafficType.values().random()
            val trafficSpeed = (newSpeed * trafficType.baseSpeedFactor * Random.nextDouble(0.8, 1.2)).toFloat()
            trafficList.add(
                TrafficCar(
                    id = trafficIdCounter,
                    xPercent = chosenLane,
                    yPixels = -180f,
                    targetLane = lanes.indexOf(chosenLane),
                    type = trafficType,
                    color = trafficType.baseColor,
                    speedPxPerSec = trafficSpeed * 7.5f
                )
            )
            val distanceInterval = Random.nextDouble(60.0, 110.0).toFloat()
            nextTrafficSpawnDistance = newDistance + distanceInterval
        }

        // Move traffic cars
        val survivingTraffic = mutableListOf<TrafficCar>()
        var hitOccurred = false

        val playerCarY = screenHeightPx * 0.78f
        val playerHitboxWidthPercent = 0.13f
        val playerHitboxHeightPx = 105f

        for (car in trafficList) {
            // Traffic relative downward motion on screen
            val relativeSpeedPx = scrollSpeedPixels - (car.speedPxPerSec * deltaSeconds)
            car.yPixels += relativeSpeedPx

            // Check collision with player
            val xDistance = abs(car.xPercent - newX)
            val yDistance = abs(car.yPixels - playerCarY)

            val isColliding = xDistance < playerHitboxWidthPercent && yDistance < (playerHitboxHeightPx * 0.85f)

            if (isColliding && !hitOccurred) {
                hitOccurred = true
                // Drop combo on hit
                combo = 1
            }

            // Check near miss (close shave without collision)
            if (!car.isNearMissAwarded && !isColliding && xDistance in (playerHitboxWidthPercent)..(playerHitboxWidthPercent * 1.6f) && yDistance < 60f) {
                car.isNearMissAwarded = true
                updatedNearMissCount++
                combo = min(8, combo + 1)
                soundManager.playNearMiss()
                toasts.add(
                    NearMissToast(
                        text = "NEAR MISS! +150",
                        points = 150 * combo
                    )
                )
            }

            // Keep car if still on screen
            if (car.yPixels < screenHeightPx + 250f) {
                survivingTraffic.add(car)
            }
        }

        // 5. Update and spawn collectibles
        val collectList = s.collectibles.toMutableList()
        var newCoins = s.coinsThisRun
        var hasShield = s.hasShield
        var hasMagnet = s.hasMagnet
        var magnetTimer = max(0f, s.magnetTimerSec - deltaSeconds)
        if (s.selectedCar.id != "golden_fury" && magnetTimer <= 0f) {
            hasMagnet = false
        }
        var health = s.health

        if (newDistance >= nextCollectibleSpawnDistance) {
            itemIdCounter++
            val lanes = listOf(0.18f, 0.39f, 0.61f, 0.82f)
            val chosenLane = lanes.random()
            val typeRoll = Random.nextFloat()
            val itemType = when {
                typeRoll < 0.65f -> CollectItemType.COIN
                typeRoll < 0.78f -> CollectItemType.NITRO
                typeRoll < 0.88f -> CollectItemType.SHIELD
                typeRoll < 0.95f -> CollectItemType.MAGNET
                else -> CollectItemType.REPAIR
            }
            collectList.add(
                CollectibleItem(
                    id = itemIdCounter,
                    xPercent = chosenLane,
                    yPixels = -90f,
                    type = itemType
                )
            )
            nextCollectibleSpawnDistance = newDistance + Random.nextDouble(35.0, 70.0).toFloat()
        }

        val survivingCollectibles = mutableListOf<CollectibleItem>()
        for (item in collectList) {
            item.yPixels += scrollSpeedPixels
            item.rotationAngle += 90f * deltaSeconds

            // Magnet attraction towards player
            if (hasMagnet && item.type == CollectItemType.COIN && abs(item.yPixels - playerCarY) < 320f) {
                val xDiff = newX - item.xPercent
                item.xPercent += xDiff * (4.5f * deltaSeconds)
            }

            val dx = abs(item.xPercent - newX)
            val dy = abs(item.yPixels - playerCarY)
            val isPickedUp = dx < 0.12f && dy < 75f

            if (isPickedUp) {
                when (item.type) {
                    CollectItemType.COIN -> {
                        newCoins += 1 * (if (s.selectedCar.id == "golden_fury") 2 else 1)
                        soundManager.playCoin()
                    }
                    CollectItemType.NITRO -> {
                        currentNitro = min(1.0f, currentNitro + 0.45f)
                        soundManager.playPowerUp()
                        toasts.add(NearMissToast(text = "+NITRO REFILL!", points = 50))
                    }
                    CollectItemType.SHIELD -> {
                        hasShield = true
                        soundManager.playPowerUp()
                        toasts.add(NearMissToast(text = "ENERGY SHIELD!", points = 50))
                    }
                    CollectItemType.MAGNET -> {
                        hasMagnet = true
                        magnetTimer = 12f
                        soundManager.playPowerUp()
                        toasts.add(NearMissToast(text = "COIN MAGNET!", points = 50))
                    }
                    CollectItemType.REPAIR -> {
                        if (health < s.maxHealth) health++
                        soundManager.playPowerUp()
                        toasts.add(NearMissToast(text = "+1 REPAIR!", points = 50))
                    }
                }
            } else if (item.yPixels < screenHeightPx + 150f) {
                survivingCollectibles.add(item)
            }
        }

        // 6. Collision resolution
        var isGameOver = false
        var newHighScore = s.newHighScoreAlert

        if (hitOccurred) {
            if (hasShield) {
                hasShield = false
                soundManager.playCrash()
                toasts.add(NearMissToast(text = "SHIELD BROKEN!", points = 0))
            } else {
                health--
                soundManager.playCrash()
                if (health <= 0) {
                    isGameOver = true
                    // Save score to Room database
                    val finalScore = newScore + toasts.sumOf { it.points }
                    if (finalScore > highestScore.value) {
                        newHighScore = true
                    }
                    viewModelScope.launch {
                        repository.recordRun(
                            score = finalScore,
                            coins = newCoins,
                            distanceMeters = newDistance.toInt(),
                            maxSpeedKmh = s.maxSpeedReached.toInt(),
                            carName = s.selectedCar.name
                        )
                    }
                }
            }
        }

        _state.value = s.copy(
            score = newScore + (if (toasts.isNotEmpty()) toasts.lastOrNull()?.points ?: 0 else 0),
            coinsThisRun = newCoins,
            distanceMeters = newDistance,
            currentSpeedKmh = newSpeed,
            maxSpeedReached = max(s.maxSpeedReached, newSpeed),
            nitroFuel = currentNitro,
            isNitroActive = isNitro,
            playerLaneX = newX,
            steerTilt = tilt,
            health = health,
            hasShield = hasShield,
            hasMagnet = hasMagnet,
            magnetTimerSec = magnetTimer,
            comboMultiplier = combo,
            nearMissCount = updatedNearMissCount,
            activeToasts = toasts,
            trafficCars = survivingTraffic,
            collectibles = survivingCollectibles,
            roadScroll = newScroll,
            animTicks = s.animTicks + 1L,
            isGameOver = isGameOver,
            newHighScoreAlert = newHighScore
        )
    }

    // Garage operations
    fun unlockCar(carId: String, cost: Int) {
        viewModelScope.launch {
            repository.unlockCar(carId, cost)
        }
    }

    fun selectCar(carId: String) {
        viewModelScope.launch {
            repository.selectCar(carId)
        }
    }

    fun upgradeStat(statType: String, cost: Int) {
        viewModelScope.launch {
            repository.upgradeStat(statType, cost)
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            val p = playerProfile.value
            repository.updateProfile(p.copy(soundEnabled = enabled))
        }
    }

    fun toggleVibration(enabled: Boolean) {
        viewModelScope.launch {
            val p = playerProfile.value
            repository.updateProfile(p.copy(vibrationEnabled = enabled))
        }
    }

    fun toggleLanguage(isBn: Boolean) {
        viewModelScope.launch {
            val p = playerProfile.value
            repository.updateProfile(p.copy(languageBn = isBn))
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
