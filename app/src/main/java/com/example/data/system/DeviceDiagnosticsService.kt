package com.example.data.system

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import com.example.data.model.DeviceMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.abs

class DeviceDiagnosticsService(private val context: Context) {

    fun getImmediateMetrics(): DeviceMetrics {
        val (batteryPct, isCharging, tempCelsius) = readBatteryInfo()
        val (availRam, totalRam) = readRamInfo()
        val (availStorage, totalStorage) = readStorageInfo()
        val netType = readNetworkType()

        return DeviceMetrics(
            batteryPercent = batteryPct,
            isCharging = isCharging,
            batteryTempCelsius = tempCelsius,
            availableRamMb = availRam,
            totalRamMb = totalRam,
            availableStorageGb = availStorage,
            totalStorageGb = totalStorage,
            networkType = netType,
            testTarget = "DNS Public (8.8.8.8 : 53)"
        )
    }

    private fun readBatteryInfo(): Triple<Int, Boolean, Float?> {
        return try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, filter)
            if (batteryStatus != null) {
                val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                val pct = if (level >= 0 && scale > 0) ((level / scale.toFloat()) * 100).toInt() else -1

                val rawTemp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
                val tempCelsius = if (rawTemp > 0) rawTemp / 10.0f else null

                Triple(pct, isCharging, tempCelsius)
            } else {
                Triple(-1, false, null)
            }
        } catch (_: Exception) {
            Triple(-1, false, null)
        }
    }

    private fun readRamInfo(): Pair<Long, Long> {
        return try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            if (actManager != null) {
                actManager.getMemoryInfo(memInfo)
                val availMb = memInfo.availMem / (1024 * 1024)
                val totalMb = memInfo.totalMem / (1024 * 1024)
                Pair(availMb, totalMb)
            } else {
                Pair(-1L, -1L)
            }
        } catch (_: Exception) {
            Pair(-1L, -1L)
        }
    }

    private fun readStorageInfo(): Pair<Float, Float> {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availBlocks = stat.availableBlocksLong

            val totalGb = (totalBlocks * blockSize) / (1024f * 1024f * 1024f)
            val availGb = (availBlocks * blockSize) / (1024f * 1024f * 1024f)
            Pair(
                (Math.round(availGb * 10.0f) / 10.0f),
                (Math.round(totalGb * 10.0f) / 10.0f)
            )
        } catch (_: Exception) {
            Pair(-1f, -1f)
        }
    }

    private fun readNetworkType(): String {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return "Non disponible"
            val activeNetwork = cm.activeNetwork ?: return "Déconnecté"
            val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return "Déconnecté"

            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Données mobiles (4G/5G)"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                else -> "Connecté"
            }
        } catch (_: Exception) {
            "Non disponible"
        }
    }

    /**
     * Executes real socket network latency measurement against a known public target
     * with progress callbacks and jitter computation.
     */
    suspend fun performNetworkPingTest(
        onStepProgress: (step: String, progress: Float) -> Unit
    ): NetworkTestResult = withContext(Dispatchers.IO) {
        val targetHost = "8.8.8.8"
        val targetPort = 53
        val sampleCount = 4
        val latencies = mutableListOf<Long>()
        var failures = 0

        onStepProgress("Initialisation de la connexion test...", 0.15f)

        for (i in 1..sampleCount) {
            onStepProgress("Mesure du paquet $i/$sampleCount vers $targetHost:$targetPort...", 0.2f + (i * 0.18f))
            try {
                val start = System.currentTimeMillis()
                val socket = Socket()
                socket.connect(InetSocketAddress(targetHost, targetPort), 1200)
                val duration = System.currentTimeMillis() - start
                socket.close()
                latencies.add(duration)
            } catch (_: Exception) {
                failures++
            }
            kotlinx.coroutines.delay(100)
        }

        onStepProgress("Analyse de la gigue et consolidation...", 0.95f)

        if (latencies.isEmpty()) {
            return@withContext NetworkTestResult(
                target = "$targetHost:$targetPort",
                averageMs = null,
                minMs = null,
                maxMs = null,
                jitterMs = null,
                failures = failures,
                sampleCount = sampleCount
            )
        }

        val avg = latencies.average().toInt()
        val min = latencies.minOrNull()?.toInt() ?: avg
        val max = latencies.maxOrNull()?.toInt() ?: avg
        val jitter = abs(max - min)

        NetworkTestResult(
            target = "$targetHost:$targetPort",
            averageMs = avg,
            minMs = min,
            maxMs = max,
            jitterMs = jitter,
            failures = failures,
            sampleCount = sampleCount
        )
    }
}

data class NetworkTestResult(
    val target: String,
    val averageMs: Int?,
    val minMs: Int?,
    val maxMs: Int?,
    val jitterMs: Int?,
    val failures: Int,
    val sampleCount: Int
)
