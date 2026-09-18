package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameHudTopBar(
    score: Int,
    coins: Int,
    distanceMeters: Float,
    speedKmh: Float,
    maxSpeedKmh: Float,
    health: Int,
    maxHealth: Int,
    nitroPercent: Float,
    isNitroActive: Boolean,
    comboMultiplier: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Upper stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xCC0F172A))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Health hearts
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (i in 1..maxHealth) {
                    val isFull = i <= health
                    Icon(
                        imageVector = if (isFull) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Health Heart",
                        tint = if (isFull) Color(0xFFEF4444) else Color(0xFF64748B),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 2.dp)
                    )
                }
            }

            // Distance
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "Distance",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${distanceMeters.toInt()}m",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Coins
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Coins",
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$coins",
                    color = Color(0xFFFDE047),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Score with combo
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$score",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (comboMultiplier > 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE11D48))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "x$comboMultiplier",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Speedometer Gauge and Nitro bar row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speedometer mini arc
            SpeedGaugeMini(
                speedKmh = speedKmh,
                maxSpeedKmh = maxSpeedKmh,
                isNitroActive = isNitroActive
            )

            // Nitro Bar
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xBB0F172A))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Nitro",
                            tint = if (isNitroActive) Color(0xFF00E5FF) else Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (isNitroActive) "NITRO ACTIVE!" else "NITRO",
                            color = if (isNitroActive) Color(0xFF00E5FF) else Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${(nitroPercent * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { nitroPercent.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isNitroActive) Color(0xFF00E5FF) else Color(0xFF0284C7),
                    trackColor = Color(0xFF334155)
                )
            }
        }
    }
}

@Composable
fun SpeedGaugeMini(
    speedKmh: Float,
    maxSpeedKmh: Float,
    isNitroActive: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = speedKmh,
        label = "speed_anim"
    )

    Box(
        modifier = modifier
            .size(width = 110.dp, height = 54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xCC0F172A))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 5.dp.toPx()
            val startAngle = 160f
            val sweepTotal = 220f
            val speedRatio = (animatedSpeed / (maxSpeedKmh * 1.25f)).coerceIn(0f, 1f)

            // Background Track Arc
            drawArc(
                color = Color(0xFF334155),
                startAngle = startAngle,
                sweepAngle = sweepTotal,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2 + 8f, strokeWidth / 2 + 2f),
                size = Size(size.width - strokeWidth - 16f, size.height * 1.5f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Value Arc with gradient
            val arcBrush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFF38BDF8),
                    Color(0xFFFBBF24),
                    if (isNitroActive) Color(0xFF00E5FF) else Color(0xFFEF4444)
                )
            )
            drawArc(
                brush = arcBrush,
                startAngle = startAngle,
                sweepAngle = sweepTotal * speedRatio,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2 + 8f, strokeWidth / 2 + 2f),
                size = Size(size.width - strokeWidth - 16f, size.height * 1.5f),
                style = Stroke(width = strokeWidth + 1f, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = "${animatedSpeed.toInt()}",
                color = if (isNitroActive) Color(0xFF00E5FF) else Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "KM/H",
                color = Color(0xFF94A3B8),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
