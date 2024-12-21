package com.polygloot.mobile.android.ui.utils

import android.media.MediaRecorder
import android.util.Log
import com.polygloot.mobile.android.ui.utils.Consts.Companion.SILENCE_DETECTOR_DURATION
import com.polygloot.mobile.android.ui.utils.Consts.Companion.SILENCE_DETECTOR_THRESHOLD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class SilenceDetector(
    private val recorder: MediaRecorder,
    private val silenceThreshold: Int = SILENCE_DETECTOR_THRESHOLD,
    private val silenceDuration: Long = SILENCE_DETECTOR_DURATION
) {
    private var lastNonSilentTime = System.currentTimeMillis()
    private var isMonitoring = false
    private var monitoringJob: Job? = null

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    interface SilenceCallback {
        suspend fun onSilenceDetected()
    }

    fun startMonitoring(callback: SilenceCallback) {
        resetMonitoring()
        isMonitoring = true
        monitoringJob = scope.launch {
            try {
                while (isMonitoring) {
                    delay(100)
                    val currentAmplitude = recorder.maxAmplitude

                    if (currentAmplitude > silenceThreshold) {
                        lastNonSilentTime = System.currentTimeMillis()
                    } else if (System.currentTimeMillis() - lastNonSilentTime > silenceDuration) {
                        if (isActive) {
                            try {
                                withContext(Dispatchers.Main) {
                                    callback.onSilenceDetected()
                                }
                            } catch (e: CancellationException) {
                                Log.e("SilenceDetector", "Callback execution canceled")
                            }
                            resetMonitoring()
                        }
                    }
                }
            } catch (e: CancellationException) {
                Log.e("Silence detector", "Cancelation exception - ${e.message}")
            }
        }
    }

    fun resetMonitoring() {
        Log.d("SilenceDetector", "Resetting monitoring")
        isMonitoring = false
        monitoringJob?.cancel() // Cancel the ongoing monitoring job
        monitoringJob = null
    }

    fun release() {
        resetMonitoring()
        scope.cancel() // Cancel the scope to clean up resources
    }
}