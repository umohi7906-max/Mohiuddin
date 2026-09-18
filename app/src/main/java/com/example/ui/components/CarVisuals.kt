package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import com.example.model.CarModel
import com.example.model.CollectItemType
import com.example.model.CollectibleItem
import com.example.model.TrafficCar
import com.example.model.TrafficType
import kotlin.math.sin

object CarVisuals {

    fun drawPlayerCar(
        drawScope: DrawScope,
        car: CarModel,
        centerX: Float,
        centerY: Float,
        carWidth: Float,
        carHeight: Float,
        steerTilt: Float, // -1f (left) to 1f (right)
        isNitroActive: Boolean,
        hasShield: Boolean,
        hasMagnet: Boolean,
        animTicks: Long
    ) {
        val rotationDeg = steerTilt * 7.5f

        drawScope.rotate(degrees = rotationDeg, pivot = Offset(centerX, centerY)) {
            val left = centerX - carWidth / 2f
            val top = centerY - carHeight / 2f

            // Shadow under car
            drawRoundRect(
                color = Color(0x66000000),
                topLeft = Offset(left - 2f, top + 8f),
                size = Size(carWidth + 4f, carHeight),
                cornerRadius = CornerRadius(14f, 14f)
            )

            // Headlight beams casting forward onto road
            val beamPath = Path().apply {
                moveTo(left + carWidth * 0.18f, top)
                lineTo(left - carWidth * 0.5f, top - 200f)
                lineTo(left + carWidth * 0.45f, top - 200f)
                lineTo(left + carWidth * 0.35f, top)
                close()
            }
            drawPath(
                path = beamPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x55FFFBEB), Color(0x00FFFBEB)),
                    startY = top,
                    endY = top - 200f
                )
            )
            val rightBeamPath = Path().apply {
                moveTo(left + carWidth * 0.65f, top)
                lineTo(left + carWidth * 0.55f, top - 200f)
                lineTo(left + carWidth * 1.5f, top - 200f)
                lineTo(left + carWidth * 0.82f, top)
                close()
            }
            drawPath(
                path = rightBeamPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x55FFFBEB), Color(0x00FFFBEB)),
                    startY = top,
                    endY = top - 200f
                )
            )

            // Wheels (4 black rounded rectangles with metallic rims)
            val wheelW = carWidth * 0.18f
            val wheelH = carHeight * 0.22f
            // Front Left
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(left - wheelW * 0.35f, top + carHeight * 0.12f),
                size = Size(wheelW, wheelH),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Front Right
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(left + carWidth - wheelW * 0.65f, top + carHeight * 0.12f),
                size = Size(wheelW, wheelH),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Rear Left
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(left - wheelW * 0.35f, top + carHeight * 0.68f),
                size = Size(wheelW, wheelH),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Rear Right
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(left + carWidth - wheelW * 0.65f, top + carHeight * 0.68f),
                size = Size(wheelW, wheelH),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Main Car Chassis Body
            val bodyGradient = Brush.horizontalGradient(
                colors = listOf(
                    car.primaryColor,
                    car.primaryColor.copy(alpha = 0.95f),
                    car.primaryColor
                ),
                startX = left,
                endX = left + carWidth
            )
            drawRoundRect(
                brush = bodyGradient,
                topLeft = Offset(left, top),
                size = Size(carWidth, carHeight),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // Racing center stripes
            val stripeW = carWidth * 0.18f
            drawRect(
                color = car.accentColor,
                topLeft = Offset(centerX - stripeW / 2f, top + 4f),
                size = Size(stripeW, carHeight - 8f)
            )

            // Front Hood vents
            drawRoundRect(
                color = Color(0x33000000),
                topLeft = Offset(centerX - carWidth * 0.22f, top + carHeight * 0.10f),
                size = Size(carWidth * 0.44f, carHeight * 0.10f),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Windshield (dark glass with blue-tinted highlight)
            val windshieldPath = Path().apply {
                moveTo(left + carWidth * 0.15f, top + carHeight * 0.35f)
                lineTo(left + carWidth * 0.85f, top + carHeight * 0.35f)
                lineTo(left + carWidth * 0.75f, top + carHeight * 0.24f)
                lineTo(left + carWidth * 0.25f, top + carHeight * 0.24f)
                close()
            }
            drawPath(
                path = windshieldPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF38BDF8)),
                    startY = top + carHeight * 0.24f,
                    endY = top + carHeight * 0.35f
                )
            )

            // Roof
            drawRoundRect(
                color = car.primaryColor,
                topLeft = Offset(left + carWidth * 0.18f, top + carHeight * 0.35f),
                size = Size(carWidth * 0.64f, carHeight * 0.28f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            // Inner stripe on roof
            drawRect(
                color = car.accentColor,
                topLeft = Offset(centerX - stripeW / 2f, top + carHeight * 0.35f),
                size = Size(stripeW, carHeight * 0.28f)
            )

            // Rear Windshield
            val rearWindowPath = Path().apply {
                moveTo(left + carWidth * 0.20f, top + carHeight * 0.63f)
                lineTo(left + carWidth * 0.80f, top + carHeight * 0.63f)
                lineTo(left + carWidth * 0.72f, top + carHeight * 0.72f)
                lineTo(left + carWidth * 0.28f, top + carHeight * 0.72f)
                close()
            }
            drawPath(
                path = rearWindowPath,
                color = Color(0xCC0F172A)
            )

            // Aerodynamic Rear Wing / Spoiler
            drawRoundRect(
                color = Color(0xFF0F172A),
                topLeft = Offset(left + carWidth * 0.08f, top + carHeight * 0.88f),
                size = Size(carWidth * 0.84f, carHeight * 0.08f),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Front Headlight Lamps
            drawRoundRect(
                color = Color(0xFFFEF08A),
                topLeft = Offset(left + carWidth * 0.08f, top + 2f),
                size = Size(carWidth * 0.22f, carHeight * 0.06f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color = Color(0xFFFEF08A),
                topLeft = Offset(left + carWidth * 0.70f, top + 2f),
                size = Size(carWidth * 0.22f, carHeight * 0.06f),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Rear Taillights (glow red)
            val tailLightColor = if (isNitroActive) Color(0xFFFF2222) else Color(0xFFDC2626)
            drawRoundRect(
                color = tailLightColor,
                topLeft = Offset(left + carWidth * 0.08f, top + carHeight - 4f),
                size = Size(carWidth * 0.25f, 5f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            drawRoundRect(
                color = tailLightColor,
                topLeft = Offset(left + carWidth * 0.67f, top + carHeight - 4f),
                size = Size(carWidth * 0.25f, 5f),
                cornerRadius = CornerRadius(2f, 2f)
            )

            // Nitro Exhaust Flame Jets
            if (isNitroActive) {
                val flicker = (sin((animTicks % 10) * 0.6) * 12f).toFloat()
                val flameLen = 45f + flicker

                // Left Exhaust Flame
                val leftExhaustX = left + carWidth * 0.28f
                val flamePathLeft = Path().apply {
                    moveTo(leftExhaustX - 6f, top + carHeight)
                    lineTo(leftExhaustX + 6f, top + carHeight)
                    lineTo(leftExhaustX, top + carHeight + flameLen)
                    close()
                }
                drawPath(
                    path = flamePathLeft,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFFFF9100), Color(0x00FF3D00)),
                        startY = top + carHeight,
                        endY = top + carHeight + flameLen
                    )
                )

                // Right Exhaust Flame
                val rightExhaustX = left + carWidth * 0.72f
                val flamePathRight = Path().apply {
                    moveTo(rightExhaustX - 6f, top + carHeight)
                    lineTo(rightExhaustX + 6f, top + carHeight)
                    lineTo(rightExhaustX, top + carHeight + flameLen)
                    close()
                }
                drawPath(
                    path = flamePathRight,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFFFF9100), Color(0x00FF3D00)),
                        startY = top + carHeight,
                        endY = top + carHeight + flameLen
                    )
                )
            }

            // Shield Energy Bubble Aura
            if (hasShield) {
                val pulse = (sin((animTicks % 20) * 0.3) * 4f).toFloat()
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x0006B6D4), Color(0x4406B6D4), Color(0xAA22D3EE)),
                        center = Offset(centerX, centerY),
                        radius = (carHeight * 0.65f) + pulse
                    ),
                    center = Offset(centerX, centerY),
                    radius = (carHeight * 0.65f) + pulse
                )
                drawCircle(
                    color = Color(0xFF67E8F9),
                    center = Offset(centerX, centerY),
                    radius = (carHeight * 0.65f) + pulse,
                    style = Stroke(width = 3f)
                )
            }

            // Magnet Ring Aura
            if (hasMagnet) {
                val magPulse = ((animTicks % 30) / 30f) * 40f
                drawCircle(
                    color = Color(0xFFFBBF24).copy(alpha = 1f - (magPulse / 40f)),
                    center = Offset(centerX, centerY),
                    radius = (carHeight * 0.5f) + magPulse,
                    style = Stroke(width = 2.5f)
                )
            }
        }
    }

    fun drawTrafficCar(
        drawScope: DrawScope,
        car: TrafficCar,
        roadLeft: Float,
        roadWidth: Float,
        densityRatio: Float,
        animTicks: Long
    ) {
        val centerX = roadLeft + car.xPercent * roadWidth
        val centerY = car.yPixels
        val carW = car.type.widthDp * densityRatio
        val carH = car.type.heightDp * densityRatio
        val left = centerX - carW / 2f
        val top = centerY - carH / 2f

        // Shadow
        drawScope.drawRoundRect(
            color = Color(0x55000000),
            topLeft = Offset(left - 2f, top + 6f),
            size = Size(carW + 4f, carH),
            cornerRadius = CornerRadius(12f, 12f)
        )

        // Tires
        val wW = carW * 0.16f
        val wH = carH * 0.20f
        drawScope.drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(left - wW * 0.3f, top + carH * 0.12f),
            size = Size(wW, wH),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawScope.drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(left + carW - wW * 0.7f, top + carH * 0.12f),
            size = Size(wW, wH),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawScope.drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(left - wW * 0.3f, top + carH * 0.68f),
            size = Size(wW, wH),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawScope.drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(left + carW - wW * 0.7f, top + carH * 0.68f),
            size = Size(wW, wH),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // Main Car Body
        drawScope.drawRoundRect(
            color = car.color,
            topLeft = Offset(left, top),
            size = Size(carW, carH),
            cornerRadius = CornerRadius(12f, 12f)
        )

        // Windshield and Windows
        if (car.type == TrafficType.TRUCK) {
            // Cab windshield
            drawScope.drawRoundRect(
                color = Color(0xCC0F172A),
                topLeft = Offset(left + carW * 0.12f, top + carH * 0.15f),
                size = Size(carW * 0.76f, carH * 0.15f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Cargo bed
            drawScope.drawRoundRect(
                color = Color(0xFF475569),
                topLeft = Offset(left + carW * 0.08f, top + carH * 0.38f),
                size = Size(carW * 0.84f, carH * 0.58f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            // Cargo stripes
            for (i in 1..3) {
                drawScope.drawLine(
                    color = Color(0xFF334155),
                    start = Offset(left + carW * 0.08f, top + carH * 0.38f + (carH * 0.58f * (i / 4f))),
                    end = Offset(left + carW * 0.92f, top + carH * 0.38f + (carH * 0.58f * (i / 4f))),
                    strokeWidth = 2f
                )
            }
        } else {
            // Front Windshield
            drawScope.drawRoundRect(
                color = Color(0xCC0F172A),
                topLeft = Offset(left + carW * 0.15f, top + carH * 0.22f),
                size = Size(carW * 0.70f, carH * 0.18f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Roof
            drawScope.drawRoundRect(
                color = car.color.copy(alpha = 0.9f),
                topLeft = Offset(left + carW * 0.18f, top + carH * 0.38f),
                size = Size(carW * 0.64f, carH * 0.24f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Rear Window
            drawScope.drawRoundRect(
                color = Color(0xCC0F172A),
                topLeft = Offset(left + carW * 0.18f, top + carH * 0.62f),
                size = Size(carW * 0.64f, carH * 0.12f),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }

        // Police Siren or Taxi Sign
        if (car.type == TrafficType.POLICE) {
            val isRed = (animTicks / 10) % 2 == 0L
            val redColor = if (isRed) Color(0xFFFF0033) else Color(0x44FF0033)
            val blueColor = if (!isRed) Color(0xFF0066FF) else Color(0x440066FF)

            drawScope.drawRoundRect(
                color = redColor,
                topLeft = Offset(left + carW * 0.22f, top + carH * 0.42f),
                size = Size(carW * 0.24f, carH * 0.08f),
                cornerRadius = CornerRadius(3f, 3f)
            )
            drawScope.drawRoundRect(
                color = blueColor,
                topLeft = Offset(left + carW * 0.54f, top + carH * 0.42f),
                size = Size(carW * 0.24f, carH * 0.08f),
                cornerRadius = CornerRadius(3f, 3f)
            )
        } else if (car.type == TrafficType.TAXI) {
            drawScope.drawRoundRect(
                color = Color(0xFFFEF08A),
                topLeft = Offset(left + carW * 0.30f, top + carH * 0.42f),
                size = Size(carW * 0.40f, carH * 0.08f),
                cornerRadius = CornerRadius(2f, 2f)
            )
        }

        // Taillights
        drawScope.drawRoundRect(
            color = Color(0xFFEF4444),
            topLeft = Offset(left + carW * 0.08f, top + carH - 4f),
            size = Size(carW * 0.22f, 4f),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawScope.drawRoundRect(
            color = Color(0xFFEF4444),
            topLeft = Offset(left + carW * 0.70f, top + carH - 4f),
            size = Size(carW * 0.22f, 4f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // Turn indicator flashing if yielding
        if (car.indicatorLeft && (animTicks / 8) % 2 == 0L) {
            drawScope.drawCircle(
                color = Color(0xFFF59E0B),
                radius = 6f,
                center = Offset(left + carW * 0.08f, top + carH * 0.85f)
            )
        }
        if (car.indicatorRight && (animTicks / 8) % 2 == 0L) {
            drawScope.drawCircle(
                color = Color(0xFFF59E0B),
                radius = 6f,
                center = Offset(left + carW * 0.92f, top + carH * 0.85f)
            )
        }
    }

    fun drawCollectible(
        drawScope: DrawScope,
        item: CollectibleItem,
        roadLeft: Float,
        roadWidth: Float,
        animTicks: Long
    ) {
        val centerX = roadLeft + item.xPercent * roadWidth
        val centerY = item.yPixels

        when (item.type) {
            CollectItemType.COIN -> {
                val pulse = (sin((animTicks % 20) * 0.3) * 2f).toFloat()
                val radius = 18f + pulse
                // Outer gold ring
                drawScope.drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
                // Inner bright gold
                drawScope.drawCircle(
                    color = Color(0xFFFDE047),
                    radius = radius * 0.75f,
                    center = Offset(centerX, centerY)
                )
                // Star/Dollar symbol center
                drawScope.drawCircle(
                    color = Color(0xFFD97706),
                    radius = radius * 0.35f,
                    center = Offset(centerX, centerY)
                )
            }
            CollectItemType.NITRO -> {
                val radius = 20f
                // Glowing cyan canister
                drawScope.drawCircle(
                    color = Color(0x5506B6D4),
                    radius = radius + 4f,
                    center = Offset(centerX, centerY)
                )
                drawScope.drawRoundRect(
                    color = Color(0xFF06B6D4),
                    topLeft = Offset(centerX - 12f, centerY - 18f),
                    size = Size(24f, 36f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                // Lightning bolt
                val bolt = Path().apply {
                    moveTo(centerX + 3f, centerY - 12f)
                    lineTo(centerX - 6f, centerY + 2f)
                    lineTo(centerX + 2f, centerY + 2f)
                    lineTo(centerX - 3f, centerY + 12f)
                    lineTo(centerX + 6f, centerY - 2f)
                    lineTo(centerX - 2f, centerY - 2f)
                    close()
                }
                drawScope.drawPath(bolt, Color.White, style = Fill)
            }
            CollectItemType.SHIELD -> {
                val radius = 22f
                drawScope.drawCircle(
                    color = Color(0x663B82F6),
                    radius = radius + 6f,
                    center = Offset(centerX, centerY)
                )
                drawScope.drawCircle(
                    color = Color(0xFF3B82F6),
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
                // Shield crest
                val crest = Path().apply {
                    moveTo(centerX, centerY - 12f)
                    lineTo(centerX + 12f, centerY - 6f)
                    lineTo(centerX + 10f, centerY + 6f)
                    lineTo(centerX, centerY + 14f)
                    lineTo(centerX - 10f, centerY + 6f)
                    lineTo(centerX - 12f, centerY - 6f)
                    close()
                }
                drawScope.drawPath(crest, Color.White, style = Fill)
            }
            CollectItemType.MAGNET -> {
                val radius = 20f
                drawScope.drawCircle(
                    color = Color(0x55E11D48),
                    radius = radius + 4f,
                    center = Offset(centerX, centerY)
                )
                // Horseshoe magnet
                drawScope.drawArc(
                    color = Color(0xFFE11D48),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX - 14f, centerY - 14f),
                    size = Size(28f, 28f),
                    style = Stroke(width = 8f)
                )
                // Silver tips
                drawScope.drawRect(
                    color = Color(0xFFE2E8F0),
                    topLeft = Offset(centerX - 18f, centerY),
                    size = Size(8f, 10f)
                )
                drawScope.drawRect(
                    color = Color(0xFFE2E8F0),
                    topLeft = Offset(centerX + 10f, centerY),
                    size = Size(8f, 10f)
                )
            }
            CollectItemType.REPAIR -> {
                val radius = 20f
                drawScope.drawCircle(
                    color = Color(0xFF10B981),
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
                // Medical / Repair Cross
                drawScope.drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(centerX - 4f, centerY - 12f),
                    size = Size(8f, 24f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawScope.drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(centerX - 12f, centerY - 4f),
                    size = Size(24f, 8f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }
        }
    }
}
