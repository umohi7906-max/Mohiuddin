package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GameScreen
import com.example.ui.screens.GameOverDialog
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.GarageScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.PauseDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F172A)
                ) {
                    TurboCarApp()
                }
            }
        }
    }
}

@Composable
fun TurboCarApp(gameViewModel: GameViewModel = viewModel()) {
    val state by gameViewModel.state.collectAsState()
    val highestScore by gameViewModel.highestScore.collectAsState()
    val playerProfile by gameViewModel.playerProfile.collectAsState()

    val isBn = playerProfile.languageBn

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        when (state.currentScreen) {
            GameScreen.MAIN_MENU -> {
                MainMenuScreen(
                    selectedCar = state.selectedCar,
                    highScore = highestScore,
                    totalCoins = playerProfile.totalCoins,
                    soundEnabled = playerProfile.soundEnabled,
                    isBn = isBn,
                    viewModel = gameViewModel
                )
            }
            GameScreen.PLAYING -> {
                GamePlayScreen(
                    state = state,
                    viewModel = gameViewModel
                )

                if (state.isPaused) {
                    PauseDialog(
                        soundEnabled = playerProfile.soundEnabled,
                        vibrationEnabled = playerProfile.vibrationEnabled,
                        isBn = isBn,
                        onResume = { gameViewModel.resumeGame() },
                        onRestart = { gameViewModel.startGame() },
                        onToggleSound = { gameViewModel.toggleSound(it) },
                        onToggleVibration = { gameViewModel.toggleVibration(it) },
                        onMainMenu = { gameViewModel.setScreen(GameScreen.MAIN_MENU) }
                    )
                }

                if (state.isGameOver) {
                    GameOverDialog(
                        state = state,
                        highestScore = highestScore,
                        isBn = isBn,
                        onRestart = { gameViewModel.startGame() },
                        onGarage = { gameViewModel.setScreen(GameScreen.GARAGE) },
                        onMainMenu = { gameViewModel.setScreen(GameScreen.MAIN_MENU) }
                    )
                }
            }
            GameScreen.GARAGE -> {
                GarageScreen(
                    profile = playerProfile,
                    viewModel = gameViewModel,
                    isBn = isBn
                )
            }
            GameScreen.LEADERBOARD -> {
                LeaderboardScreen(
                    viewModel = gameViewModel,
                    isBn = isBn
                )
            }
            GameScreen.SETTINGS -> {
                MainMenuScreen(
                    selectedCar = state.selectedCar,
                    highScore = highestScore,
                    totalCoins = playerProfile.totalCoins,
                    soundEnabled = playerProfile.soundEnabled,
                    isBn = isBn,
                    viewModel = gameViewModel
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Turbo Car: $name", modifier = modifier)
}
