package com.example.data

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Display
import com.example.model.DetectedDeviceInfo
import com.example.model.DetectionStatus
import com.example.model.DeviceBrand
import com.example.model.DeviceProfile
import com.example.model.RefreshRateOption
import kotlin.math.roundToInt

object DeviceDetectionHelper {

  fun detectCurrentDevice(context: Context): DetectedDeviceInfo {
    val manufacturer = Build.MANUFACTURER.orEmpty().trim()
    val brandRaw = Build.BRAND.orEmpty().trim()
    val modelRaw = Build.MODEL.orEmpty().trim()
    val deviceCode = Build.DEVICE.orEmpty().trim()
    val androidVersion = Build.VERSION.RELEASE.orEmpty().ifBlank { "14" }

    val (activeHz, maxHz) = readRefreshRates(context)
    val smallestWidthDp = context.resources.configuration.smallestScreenWidthDp

    val combinedSearch = "$manufacturer $brandRaw $modelRaw $deviceCode".uppercase()

    val detectedBrand = detectBrand(manufacturer, brandRaw, modelRaw)
    val exactDevice = matchDeviceProfile(combinedSearch, modelRaw, detectedBrand)

    val status = when {
      exactDevice != null -> DetectionStatus.EXACT_MATCH
      detectedBrand != null -> DetectionStatus.BRAND_MATCH_NEEDS_MODEL
      else -> DetectionStatus.UNMATCHED_DEVICE
    }

    return DetectedDeviceInfo(
      manufacturer = manufacturer.ifBlank { "Unknown" },
      brandRaw = brandRaw.ifBlank { "Android" },
      modelRaw = modelRaw.ifBlank { "Unknown Model" },
      deviceCode = deviceCode,
      androidVersion = androidVersion,
      detectedRefreshHz = activeHz,
      maxSupportedHz = maxHz,
      currentSmallestWidthDp = smallestWidthDp,
      status = status,
      matchedDevice = exactDevice,
      matchedBrand = exactDevice?.brand ?: detectedBrand,
    )
  }

  fun nearestRefreshRateOption(hz: Int): RefreshRateOption {
    return when {
      hz >= 135 -> RefreshRateOption.HZ_144
      hz >= 105 -> RefreshRateOption.HZ_120
      hz >= 75 -> RefreshRateOption.HZ_90
      else -> RefreshRateOption.HZ_60
    }
  }

  private fun readRefreshRates(context: Context): Pair<Int, Int> {
    return try {
      val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
      val display: Display? = displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
      if (display != null) {
        val active = display.mode.refreshRate.roundToInt().coerceIn(60, 165)
        val maxSupported = display.supportedModes
          .maxOfOrNull { it.refreshRate.roundToInt() }
          ?.coerceIn(60, 165) ?: active
        active to maxOf(active, maxSupported)
      } else {
        60 to 60
      }
    } catch (_: Exception) {
      60 to 60
    }
  }

  private fun detectBrand(manufacturer: String, brand: String, model: String): DeviceBrand? {
    val key = "$manufacturer $brand $model".uppercase()
    return when {
      key.contains("SAMSUNG") || key.startsWith("SM-") || key.contains("GALAXY") -> DeviceBrand.SAMSUNG
      key.contains("VIVO") || Regex("\\bV2[0-9]{3}\\b").containsMatchIn(key) -> DeviceBrand.VIVO
      key.contains("IQOO") || Regex("\\bI2[0-9]{3}\\b").containsMatchIn(key) -> DeviceBrand.IQOO
      key.contains("APPLE") || key.contains("IPHONE") -> DeviceBrand.IPHONE
      key.contains("ONEPLUS") || key.contains("CPH2") -> DeviceBrand.ONEPLUS
      key.contains("XIAOMI") || key.contains("REDMI") || key.contains("POCO") -> DeviceBrand.XIAOMI_POCO
      key.contains("REALME") || key.contains("RMX") || key.contains("NOTHING") || key.contains("ASUS") -> DeviceBrand.REALME
      else -> null
    }
  }

  private fun matchDeviceProfile(
    combinedUpper: String,
    modelRaw: String,
    detectedBrand: DeviceBrand?,
  ): DeviceProfile? {
    // 1. Exact or prefix match on model codes
    DeviceCatalog.allDevices.firstOrNull { profile ->
      profile.modelCodes.any { code ->
        val upperCode = code.uppercase()
        combinedUpper.contains(upperCode)
      }
    }?.let { return it }

    // 2. Fuzzy match on marketing name tokens
    val modelUpper = modelRaw.uppercase()
    val candidates = if (detectedBrand != null) {
      DeviceCatalog.byBrand(detectedBrand)
    } else {
      DeviceCatalog.allDevices
    }

    return candidates.firstOrNull { profile ->
      val cleanName = profile.marketingName.uppercase()
        .replace("SAMSUNG ", "")
        .replace("APPLE ", "")
        .replace(" 5G", "")
      modelUpper.contains(cleanName) || cleanName.contains(modelUpper) && modelUpper.length >= 4
    }
  }
}
