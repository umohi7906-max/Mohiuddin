package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlayerProfileEntity
import com.example.model.CarCatalog
import com.example.model.CarModel
import com.example.model.GameScreen
import com.example.ui.components.CarVisuals
import com.example.viewmodel.GameViewModel

@Composable
fun GarageScreen(
    profile: PlayerProfileEntity,
    viewModel: GameViewModel,
    isBn: Boolean,
    modifier: Modifier = Modifier
) {
    val unlockedSet = remember(profile.unlockedCarsCsv) {
        profile.unlockedCarsCsv.split(",").map { it.trim() }.toSet()
    }

    var viewingCarId by remember(profile.selectedCarId) {
        mutableStateOf(profile.selectedCarId)
    }

    val viewingCar = CarCatalog.getCarById(viewingCarId)
    val isUnlocked = unlockedSet.contains(viewingCarId)
    val isSelected = profile.selectedCarId == viewingCarId

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.setScreen(GameScreen.MAIN_MENU) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .size(42.dp)
                        .testTag("garage_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = if (isBn) "গ্যারেজ ও টিউনিং" else "GARAGE & TUNING",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )

                // Coin balance
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile.totalCoins}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Car Carousel Selector (horizontal list)
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(CarCatalog.ALL_CARS) { car ->
                            val carIsUnlocked = unlockedSet.contains(car.id)
                            val carIsChosen = car.id == viewingCarId

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (carIsChosen) Color(0xFF1E293B) else Color(0xFF0F172A)
                                ),
                                modifier = Modifier
                                    .size(width = 120.dp, height = 110.dp)
                                    .border(
                                        width = if (carIsChosen) 2.dp else 1.dp,
                                        color = if (carIsChosen) Color(0xFF38BDF8) else Color(0xFF334155),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewingCarId = car.id }
                                    .testTag("car_card_${car.id}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Mini Car Canvas
                                    Canvas(modifier = Modifier.size(40.dp, 56.dp)) {
                                        CarVisuals.drawPlayerCar(
                                            drawScope = this,
                                            car = car,
                                            centerX = size.width / 2f,
                                            centerY = size.height / 2f,
                                            carWidth = 24.dp.toPx(),
                                            carHeight = 46.dp.toPx(),
                                            steerTilt = 0f,
                                            isNitroActive = false,
                                            hasShield = false,
                                            hasMagnet = false,
                                            animTicks = 0L
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isBn) car.nameBn else car.name,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    if (!carIsUnlocked) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Locked",
                                                tint = Color(0xFF94A3B8),
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "${car.price}",
                                                color = Color(0xFFFBBF24),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Selected Car Showcase & Stats Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isBn) viewingCar.nameBn else viewingCar.name,
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = if (isBn) viewingCar.descriptionBn else viewingCar.description,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                }

                                // Selection/Unlock Button
                                if (!isUnlocked) {
                                    val canAfford = profile.totalCoins >= viewingCar.price
                                    Button(
                                        onClick = { viewModel.unlockCar(viewingCar.id, viewingCar.price) },
                                        enabled = canAfford,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("unlock_car_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Unlock", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${viewingCar.price}")
                                    }
                                } else if (!isSelected) {
                                    Button(
                                        onClick = { viewModel.selectCar(viewingCar.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("select_car_button")
                                    ) {
                                        Text(text = if (isBn) "নির্বাচন" else "SELECT")
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF10B981))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = if (isBn) "সক্রিয়" else "EQUIPPED", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Special Perk Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0F172A))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Stars,
                                        contentDescription = "Special Perk",
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${if (isBn) "বিশেষ ক্ষমতা:" else "Special Perk:"} ${if (isBn) viewingCar.perkBn else viewingCar.perk}",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Stat Bars
                            StatBarRow(
                                label = if (isBn) "টপ স্পিড" else "Top Speed",
                                valueText = "${viewingCar.topSpeedKmh} KM/H",
                                progress = (viewingCar.topSpeedKmh / 280f).coerceIn(0f, 1f),
                                barColor = Color(0xFFE11D48)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            StatBarRow(
                                label = if (isBn) "নিয়ন্ত্রণ" else "Handling",
                                valueText = "${(viewingCar.handling * 10).toInt()} / 15",
                                progress = (viewingCar.handling / 1.5f).coerceIn(0f, 1f),
                                barColor = Color(0xFF06B6D4)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            StatBarRow(
                                label = if (isBn) "আর্মার / লাইফ" else "Armor HP",
                                valueText = "${viewingCar.baseHealth} Hits",
                                progress = (viewingCar.baseHealth / 4f).coerceIn(0f, 1f),
                                barColor = Color(0xFF10B981)
                            )
                        }
                    }
                }

                // Upgrades Station Section
                item {
                    Text(
                        text = if (isBn) "পারফর্মেন্স আপগ্রেডস" else "PERFORMANCE TUNING",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Upgrade 1: Engine (Speed)
                item {
                    val speedCost = profile.speedUpgradeLevel * 150
                    UpgradeItemCard(
                        title = if (isBn) "ইঞ্জিন টিউনিং" else "Turbo Engine Tuning",
                        subtitle = if (isBn) "প্রতি লেভেলে +১০ কিমি/ঘণ্টা বৃদ্ধি" else "+10 KM/H Top Speed per level",
                        level = profile.speedUpgradeLevel,
                        maxLevel = 5,
                        cost = speedCost,
                        canAfford = profile.totalCoins >= speedCost && profile.speedUpgradeLevel < 5,
                        icon = Icons.Default.Speed,
                        iconColor = Color(0xFFE11D48),
                        isBn = isBn,
                        onUpgrade = { viewModel.upgradeStat("speed", speedCost) }
                    )
                }

                // Upgrade 2: Tires (Handling)
                item {
                    val handlingCost = profile.handlingUpgradeLevel * 120
                    UpgradeItemCard(
                        title = if (isBn) "স্পোর্টস টায়ার ও গ্রিপ" else "Racing Tires & Grip",
                        subtitle = if (isBn) "দ্রুত লেন পরিবর্তন ও ড্রাগ রেসপন্স" else "Sharper steering response & drift",
                        level = profile.handlingUpgradeLevel,
                        maxLevel = 5,
                        cost = handlingCost,
                        canAfford = profile.totalCoins >= handlingCost && profile.handlingUpgradeLevel < 5,
                        icon = Icons.Default.Tune,
                        iconColor = Color(0xFF06B6D4),
                        isBn = isBn,
                        onUpgrade = { viewModel.upgradeStat("handling", handlingCost) }
                    )
                }

                // Upgrade 3: Armor (Shield)
                item {
                    val shieldCost = profile.shieldUpgradeLevel * 200
                    UpgradeItemCard(
                        title = if (isBn) "শক্তিশালী চেসিস আর্মার" else "Reinforced Chassis",
                        subtitle = if (isBn) "অতিরিক্ত ১টি ক্র্যাশ সহ্য করার ক্ষমতা" else "+1 Max Hit Points armor",
                        level = profile.shieldUpgradeLevel,
                        maxLevel = 3,
                        cost = shieldCost,
                        canAfford = profile.totalCoins >= shieldCost && profile.shieldUpgradeLevel < 3,
                        icon = Icons.Default.Shield,
                        iconColor = Color(0xFF10B981),
                        isBn = isBn,
                        onUpgrade = { viewModel.upgradeStat("shield", shieldCost) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun StatBarRow(
    label: String,
    valueText: String,
    progress: Float,
    barColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = valueText, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = Color(0xFF0F172A)
        )
    }
}

@Composable
fun UpgradeItemCard(
    title: String,
    subtitle: String,
    level: Int,
    maxLevel: Int,
    cost: Int,
    canAfford: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    isBn: Boolean,
    onUpgrade: () -> Unit
) {
    val isMax = level >= maxLevel

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBn) "লেভেল $level/$maxLevel" else "Level $level/$maxLevel",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isMax) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF334155))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "MAX", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onUpgrade,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = "Cost", tint = Color(0xFFFBBF24), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$cost", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
