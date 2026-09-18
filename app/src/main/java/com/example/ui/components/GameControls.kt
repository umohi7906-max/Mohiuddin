package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameControlsBottomBar(
    onSteerLeft: (Boolean) -> Unit,
    onSteerRight: (Boolean) -> Unit,
    onNitro: (Boolean) -> Unit,
    onHorn: () -> Unit,
    isNitroAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    var leftPressed by remember { mutableStateOf(false) }
    var rightPressed by remember { mutableStateOf(false) }
    var nitroPressed by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Left Steering & Horn group
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Steer Left Button
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        if (leftPressed) Color(0xFF1E293B) else Color(0xCC0F172A)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                leftPressed = true
                                onSteerLeft(true)
                                tryAwaitRelease()
                                leftPressed = false
                                onSteerLeft(false)
                            }
                        )
                    }
                    .testTag("steer_left_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Steer Left",
                    tint = if (leftPressed) Color(0xFF38BDF8) else Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Steer Right Button
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        if (rightPressed) Color(0xFF1E293B) else Color(0xCC0F172A)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                rightPressed = true
                                onSteerRight(true)
                                tryAwaitRelease()
                                rightPressed = false
                                onSteerRight(false)
                            }
                        )
                    }
                    .testTag("steer_right_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Steer Right",
                    tint = if (rightPressed) Color(0xFF38BDF8) else Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Action Group: Horn and Nitro
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Horn Button
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xCC1E293B))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onHorn() }
                        )
                    }
                    .testTag("horn_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = "Honk Horn",
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Turbo Nitro Pedal Button
            val nitroBrush = if (isNitroAvailable) {
                if (nitroPressed) {
                    Brush.verticalGradient(listOf(Color(0xFF00E5FF), Color(0xFF0284C7)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF0369A1)))
                }
            } else {
                Brush.verticalGradient(listOf(Color(0xFF475569), Color(0xFF334155)))
            }

            Box(
                modifier = Modifier
                    .size(width = 82.dp, height = 72.dp)
                    .shadow(10.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(nitroBrush)
                    .pointerInput(isNitroAvailable) {
                        if (isNitroAvailable) {
                            detectTapGestures(
                                onPress = {
                                    nitroPressed = true
                                    onNitro(true)
                                    tryAwaitRelease()
                                    nitroPressed = false
                                    onNitro(false)
                                }
                            )
                        }
                    }
                    .testTag("nitro_button"),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Turbo Nitro Boost",
                        tint = if (isNitroAvailable) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "NITRO",
                        color = if (isNitroAvailable) Color.White else Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
