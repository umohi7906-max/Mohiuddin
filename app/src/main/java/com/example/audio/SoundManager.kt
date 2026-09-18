package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class SoundManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 22050

    var isSoundEnabled: Boolean = true
    var isVibrationEnabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var engineJob: Job? = null

    fun playCoin() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 140
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val half = numSamples / 2
            val freq1 = 987.77 // B5
            val freq2 = 1318.51 // E6

            for (i in 0 until numSamples) {
                val freq = if (i < half) freq1 else freq2
                val envelope = 1.0 - (i.toDouble() / numSamples)
                val sample = (sin(2.0 * PI * i * freq / sampleRate) * Short.MAX_VALUE * 0.45 * envelope).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playNitro() {
        vibrate(80)
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 350
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val freq = 220.0 + progress * 700.0
                val envelope = sin(progress * PI)
                val noise = (Random.nextDouble(-0.3, 0.3))
                val tone = sin(2.0 * PI * i * freq / sampleRate) * 0.7
                val sample = ((tone + noise) * Short.MAX_VALUE * 0.4 * envelope).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playCrash() {
        vibrate(300)
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 450
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val envelope = (1.0 - progress) * (1.0 - progress)
                val noise = Random.nextDouble(-1.0, 1.0)
                val rumble = sin(2.0 * PI * i * 65.0 / sampleRate)
                val sample = ((noise * 0.7 + rumble * 0.5) * Short.MAX_VALUE * 0.6 * envelope).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playNearMiss() {
        vibrate(40)
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 120
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val freq = 600.0 - progress * 300.0
                val envelope = sin(progress * PI)
                val sample = (sin(2.0 * PI * i * freq / sampleRate) * Short.MAX_VALUE * 0.35 * envelope).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playHorn() {
        vibrate(60)
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 220
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val envelope = if (i < 200) i / 200.0 else (1.0 - (i.toDouble() / numSamples))
                val wave1 = sin(2.0 * PI * i * 440.0 / sampleRate)
                val wave2 = sin(2.0 * PI * i * 554.0 / sampleRate)
                val sample = ((wave1 + wave2) * 0.5 * Short.MAX_VALUE * 0.4 * envelope).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playPowerUp() {
        vibrate(60)
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 260
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val step = numSamples / 3
            val f1 = 523.25 // C5
            val f2 = 659.25 // E5
            val f3 = 783.99 // G5

            for (i in 0 until numSamples) {
                val f = when {
                    i < step -> f1
                    i < step * 2 -> f2
                    else -> f3
                }
                val envelope = 1.0 - (i.toDouble() / numSamples) * 0.5
                val sample = (sin(2.0 * PI * i * f / sampleRate) * Short.MAX_VALUE * 0.45 * envelope).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            scope.launch {
                delay(600)
                audioTrack.release()
            }
        } catch (_: Exception) {
            // Ignore audio generation issues safely
        }
    }

    private fun vibrate(durationMs: Long) {
        if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        durationMs,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Ignore vibration failure safely
        }
    }

    fun release() {
        engineJob?.cancel()
    }
}
