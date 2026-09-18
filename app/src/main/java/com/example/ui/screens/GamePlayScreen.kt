package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameScreen
import com.example.ui.components.CarVisuals
import com.example.ui.components.GameControlsBottomBar
import com.example.ui.components.GameHudTopBar
import com.example.viewmodel.GamePlayState
import com.example.viewmodel.GameViewModel

@Composable
fun GamePlayScreen(
    state: GamePlayState,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF022C22))
    ) {
        val screenHeightPx = constraints.maxHeight.toFloat()
        val screenWidthPx = constraints.maxWidth.toFloat()
        val density = LocalDensity.current.density

        // Road geometric dimensions
        val roadWidth = screenWidthPx * 0.82f
        val roadLeft = (screenWidthPx - roadWidth) / 2f
        val roadRight = roadLeft + roadWidth

        // Game Loop Hook via LaunchedEffect
        LaunchedEffect(state.isPlaying, state.isPaused, state.isGameOver) {
            if (state.isPlaying && !state.isPaused && !state.isGameOver) {
                var lastTimeNanos = 0L
                while (true) {
                    withFrameNanos { timeNanos ->
                        if (lastTimeNanos != 0L) {
                            val dt = ((timeNanos - lastTimeNanos) / 1_000_000_000f).coerceIn(0.005f, 0.05f)
                            viewModel.onGameTick(dt, screenHeightPx)
                        }
                        lastTimeNanos = timeNanos
                    }
                }
            }
        }

        // Road Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val touchX = change.position.x
                        val relativeX = ((touchX - roadLeft) / roadWidth).coerceIn(0.12f, 0.88f)
                        viewModel.onPlayerDirectSteer(relativeX)
                    }
                }
        ) {
            // 1. Roadside Grass & Verges
            val roadsideBrushLeft = Brush.horizontalGradient(
                colors = listOf(Color(0xFF14532D), Color(0xFF166534)),
                startX = 0f,
                endX = roadLeft
            )
            drawRect(
                brush = roadsideBrushLeft,
                topLeft = Offset(0f, 0f),
                size = Size(roadLeft, screenHeightPx)
            )

            val roadsideBrushRight = Brush.horizontalGradient(
                colors = listOf(Color(0xFF166534), Color(0xFF14532D)),
                startX = roadRight,
                endX = screenWidthPx
            )
            drawRect(
                brush = roadsideBrushRight,
                topLeft = Offset(roadRight, 0f),
                size = Size(screenWidthPx - roadRight, screenHeightPx)
            )

            // 2. Road Barriers / Curbs (Red and White striped curbs scrolling)
            val curbWidth = 14.dp.toPx()
            val stripeHeight = 40.dp.toPx()
            val offsetScroll = state.roadScroll % (stripeHeight * 2f)

            var curbY = -stripeHeight * 2f + offsetScroll
            while (curbY < screenHeightPx + stripeHeight) {
                val isRed = ((curbY - offsetScroll) / stripeHeight).toInt() % 2 == 0
                val curbColor = if (isRed) Color(0xFFDC2626) else Color(0xFFF8FAFC)

                // Left curb
                drawRect(
                    color = curbColor,
                    topLeft = Offset(roadLeft - curbWidth, curbY),
                    size = Size(curbWidth, stripeHeight)
                )
                // Right curb
                drawRect(
                    color = curbColor,
                    topLeft = Offset(roadRight, curbY),
                    size = Size(curbWidth, stripeHeight)
                )
                curbY += stripeHeight
            }

            // 3. Main Asphalt Highway
            val asphaltBrush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF1E293B),
                    Color(0xFF334155),
                    Color(0xFF1E293B)
                ),
                startX = roadLeft,
                endX = roadRight
            )
            drawRect(
                brush = asphaltBrush,
                topLeft = Offset(roadLeft, 0f),
                size = Size(roadWidth, screenHeightPx)
            )

            // 4. Outer Solid White Shoulders
            drawRect(
                color = Color.White,
                topLeft = Offset(roadLeft + 3f, 0f),
                size = Size(4.dp.toPx(), screenHeightPx)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(roadRight - 4.dp.toPx() - 3f, 0f),
                size = Size(4.dp.toPx(), screenHeightPx)
            )

            // 5. Dashed Lane Separators (3 lane lines for 4 lanes)
            val numLanes = 4
            val dashHeight = 45.dp.toPx()
            val gapHeight = 35.dp.toPx()
            val cycleHeight = dashHeight + gapHeight
            val laneScroll = state.roadScroll % cycleHeight

            for (i in 1 until numLanes) {
                val laneX = roadLeft + (roadWidth * (i.toFloat() / numLanes))
                var y = -cycleHeight + laneScroll
                while (y < screenHeightPx + cycleHeight) {
                    drawRoundRect(
                        color = Color(0xDDFFFFFF),
                        topLeft = Offset(laneX - 2.5.dp.toPx(), y),
                        size = Size(5.dp.toPx(), dashHeight),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                    y += cycleHeight
                }
            }

            // 6. Draw Collectible Items
            for (item in state.collectibles) {
                CarVisuals.drawCollectible(
                    drawScope = this,
                    item = item,
                    roadLeft = roadLeft,
                    roadWidth = roadWidth,
                    animTicks = state.animTicks
                )
            }

            // 7. Draw Traffic Cars
            for (traffic in state.trafficCars) {
                CarVisuals.drawTrafficCar(
                    drawScope = this,
                    car = traffic,
                    roadLeft = roadLeft,
                    roadWidth = roadWidth,
                    densityRatio = density,
                    animTicks = state.animTicks
                )
            }

            // 8. Draw Player Sports Car
            val playerCarCenterX = roadLeft + state.playerLaneX * roadWidth
            val playerCarCenterY = screenHeightPx * 0.78f
            val playerCarWidth = 52.dp.toPx()
            val playerCarHeight = 98.dp.toPx()

            CarVisuals.drawPlayerCar(
                drawScope = this,
                car = state.selectedCar,
                centerX = playerCarCenterX,
                centerY = playerCarCenterY,
                carWidth = playerCarWidth,
                carHeight = playerCarHeight,
                steerTilt = state.steerTilt,
                isNitroActive = state.isNitroActive,
                hasShield = state.hasShield,
                hasMagnet = state.hasMagnet,
                animTicks = state.animTicks
            )
        }

        // Overlay: Top HUD Bar
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            GameHudTopBar(
                score = state.score,
                coins = state.coinsThisRun,
                distanceMeters = state.distanceMeters,
                speedKmh = state.currentSpeedKmh,
                maxSpeedKmh = state.selectedCar.topSpeedKmh.toFloat(),
                health = state.health,
                maxHealth = state.maxHealth,
                nitroPercent = state.nitroFuel,
                isNitroActive = state.isNitroActive,
                comboMultiplier = state.comboMultiplier
            )
        }

        // Pause Button top-right
        IconButton(
            onClick = { viewModel.pauseGame() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 22.dp, end = 16.dp)
                .clip(CircleShape)
                .background(Color(0xCC0F172A))
                .size(44.dp)
                .testTag("pause_button")
        ) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause Game",
                tint = Color.White
            )
        }

        // Near Miss Notification Toasts (floating popups)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (toast in state.activeToasts.takeLast(2)) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { -it / 2 }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFE11D48), Color(0xFFF59E0B))
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = toast.text,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Bottom Controls Bar (Left/Right steering + Nitro + Horn)
        GameControlsBottomBar(
            onSteerLeft = { viewModel.setSteerLeft(it) },
            onSteerRight = { viewModel.setSteerRight(it) },
            onNitro = { viewModel.setNitro(it) },
            onHorn = { viewModel.honkHorn() },
            isNitroAvailable = state.nitroFuel > 0.15f,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
