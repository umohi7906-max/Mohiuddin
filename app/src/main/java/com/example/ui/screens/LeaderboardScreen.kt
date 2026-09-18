package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ScoreEntity
import com.example.model.GameScreen
import com.example.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeaderboardScreen(
    viewModel: GameViewModel,
    isBn: Boolean,
    modifier: Modifier = Modifier
) {
    val topScores by viewModel.topScores.collectAsState()
    val highestScore by viewModel.highestScore.collectAsState()
    val totalDistance by viewModel.repository.totalDistance.collectAsState(initial = 0)
    val totalGames by viewModel.repository.totalGamesPlayed.collectAsState(initial = 0)

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
                        .testTag("leaderboard_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = if (isBn) "সেরা রেকর্ডস" else "HALL OF FAME",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )

                // Placeholder to balance back button
                Box(modifier = Modifier.size(42.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Career Stats Card
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
                            Text(
                                text = if (isBn) "ক্যারিয়ার পরিসংখ্যান" else "CAREER STATS",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                // Highest Score
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$highestScore",
                                        color = Color(0xFFFBBF24),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = if (isBn) "টপ স্কোর" else "High Score",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }

                                // Total Distance
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val distStr = if (totalDistance > 1000) {
                                        String.format(Locale.US, "%.1f km", totalDistance / 1000f)
                                    } else {
                                        "$totalDistance m"
                                    }
                                    Text(
                                        text = distStr,
                                        color = Color(0xFF38BDF8),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = if (isBn) "মোট দূরত্ব" else "Distance",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }

                                // Total Races
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$totalGames",
                                        color = Color(0xFF10B981),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = if (isBn) "মোট রেস" else "Races",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Header for Top Runs
                item {
                    Text(
                        text = if (isBn) "শীর্ষ ১০টি রেসের তালিকা" else "TOP RACING RUNS",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (topScores.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = "No Records",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (isBn) "এখনও কোনো রেস খেলা হয়নি!" else "No race records yet!",
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isBn) "প্রথম রেস শুরু করে ইতিহাস গড়ুন!" else "Hit Race Now to set your first record!",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(topScores) { index, scoreItem ->
                        ScoreRecordCard(
                            rank = index + 1,
                            scoreItem = scoreItem,
                            isBn = isBn
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ScoreRecordCard(
    rank: Int,
    scoreItem: ScoreEntity,
    isBn: Boolean
) {
    val rankColor = when (rank) {
        1 -> Color(0xFFFBBF24) // Gold
        2 -> Color(0xFFE2E8F0) // Silver
        3 -> Color(0xFFD97706) // Bronze
        else -> Color(0xFF94A3B8)
    }

    val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val dateText = dateFormatter.format(Date(scoreItem.timestamp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (rank <= 3) 1.dp else 0.dp,
                color = if (rank <= 3) rankColor.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Rank & Car Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(rankColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$rank",
                        color = rankColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = scoreItem.carName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${scoreItem.distanceMeters}m",
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = " • $dateText",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Right Score & Coins Info
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${scoreItem.score}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "+${scoreItem.coins}",
                        color = Color(0xFFFDE047),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
