package com.example.model

import androidx.compose.ui.graphics.Color

enum class TrafficType(
    val widthDp: Float,
    val heightDp: Float,
    val baseSpeedFactor: Float,
    val baseColor: Color
) {
    SEDAN(50f, 90f, 0.55f, Color(0xFF3B82F6)),
    SPORT_COUPE(48f, 86f, 0.70f, Color(0xFFEC4899)),
    TAXI(50f, 90f, 0.50f, Color(0xFFFACC15)),
    TRUCK(58f, 130f, 0.38f, Color(0xFF64748B)),
    POLICE(50f, 92f, 0.65f, Color(0xFF0F172A))
}

data class TrafficCar(
    val id: Long,
    var xPercent: Float, // 0.0f (left) to 1.0f (right)
    var yPixels: Float,
    val targetLane: Int, // 0, 1, 2, 3
    val type: TrafficType,
    val color: Color,
    val speedPxPerSec: Float,
    var isNearMissAwarded: Boolean = false,
    var indicatorLeft: Boolean = false,
    var indicatorRight: Boolean = false
)

enum class CollectItemType {
    COIN,
    NITRO,
    SHIELD,
    MAGNET,
    REPAIR
}

data class CollectibleItem(
    val id: Long,
    var xPercent: Float,
    var yPixels: Float,
    val type: CollectItemType,
    var rotationAngle: Float = 0f
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float = 1f,
    val color: Color,
    var radius: Float,
    val maxLife: Float = 0.5f,
    var life: Float = 0.5f
)

data class NearMissToast(
    val text: String,
    val points: Int,
    val timestamp: Long = System.currentTimeMillis()
)

enum class GameScreen {
    MAIN_MENU,
    PLAYING,
    GARAGE,
    LEADERBOARD,
    SETTINGS
}
