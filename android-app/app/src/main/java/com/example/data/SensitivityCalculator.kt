package com.example.data

import com.example.model.CalculatedSensitivity
import com.example.model.CalibrationConfig
import com.example.model.DeviceProfile
import com.example.model.FactorImpact
import com.example.model.TouchResponseTier
import kotlin.math.roundToInt

object SensitivityCalculator {

  fun calculate(
    device: DeviceProfile,
    config: CalibrationConfig,
  ): CalculatedSensitivity {
    // 1. Touch Response Impact (either preset tier or live drag-pad calibration)
    val (touchGeneralDelta, touchRedDotDelta, touchScopeDelta, touchFireDelta) =
      if (config.touchResponse == TouchResponseTier.CUSTOM_CALIBRATED &&
        config.measuredSwipeVelocityPxPerSec != null
      ) {
        val vel = config.measuredSwipeVelocityPxPerSec
        // Typical drag headshot swipe velocity ranges from ~900 px/s (slow) to ~3200 px/s (ultra fast)
        when {
          vel < 1300f -> listOf(7, 6, 4, -2)   // Slower finger drag -> boost sensitivity
          vel > 2500f -> listOf(-6, -7, -4, 2) // Very fast finger flick -> tighten to prevent overshoot
          else -> listOf(1, 0, 0, 0)           // Balanced drag speed
        }
      } else {
        listOf(
          config.touchResponse.generalDelta,
          config.touchResponse.redDotDelta,
          config.touchResponse.scopeDelta,
          config.touchResponse.fireButtonDelta,
        )
      }

    // 2. Refresh Rate Impact relative to device's native display curve
    val hzGeneralDelta = config.refreshRate.generalDelta
    val hzRedDotDelta = config.refreshRate.redDotDelta
    val hzScopeDelta = config.refreshRate.scopeDelta

    // 3. Game Settings Impact (Graphics + FPS + Aim Precision + Playstyle + Finger Grip)
    val gameGeneralDelta =
      config.graphics.generalDelta +
        config.fps.generalDelta +
        config.playStyle.generalDelta +
        config.fingerGrip.generalDelta

    val gameRedDotDelta =
      config.graphics.redDotDelta +
        config.fps.generalDelta +
        config.aimPrecision.redDotDelta +
        config.playStyle.redDotDelta

    val gameScopeDelta =
      config.aimPrecision.scopeDelta +
        config.playStyle.scopeDelta

    val gameFreeLookDelta = config.fingerGrip.freeLookDelta
    val gameFireDelta = config.playStyle.fireButtonDelta + config.fingerGrip.fireButtonDelta

    // Compute final 0..200 values (modern Free Fire India scale)
    val general = (device.baseGeneral + touchGeneralDelta + hzGeneralDelta + gameGeneralDelta)
      .coerceIn(70, 200)
    val redDot = (device.baseRedDot + touchRedDotDelta + hzRedDotDelta + gameRedDotDelta)
      .coerceIn(60, 200)
    val scope2x = (device.base2xScope + touchScopeDelta + hzScopeDelta + gameScopeDelta)
      .coerceIn(50, 200)
    val scope4x = (device.base4xScope + touchScopeDelta + hzScopeDelta + gameScopeDelta)
      .coerceIn(45, 200)
    val sniperScope = (device.baseSniperScope + (touchScopeDelta / 2) + (gameScopeDelta / 2))
      .coerceIn(30, 185)
    val freeLook = (device.baseFreeLook + gameFreeLookDelta + (hzGeneralDelta / 2))
      .coerceIn(50, 200)
    val fireButton = (device.baseFireButton + touchFireDelta + gameFireDelta)
      .coerceIn(36, 68)

    // Consistency score reflects how well Hz, graphics, FPS & touch response harmonize
    var score = 84
    if (config.refreshRate.hz >= 120 && device.nativeMaxHz >= 120) score += 5
    if (config.fps.name == "HIGH") score += 4
    if (config.graphics.name == "SMOOTH" || config.graphics.name == "STANDARD") score += 3
    if (config.touchResponse == TouchResponseTier.CUSTOM_CALIBRATED) score += 3
    score = score.coerceIn(76, 99)

    val factors = listOf(
      FactorImpact(
        titleEn = "Mobile Model (${device.marketingName})",
        titleTa = "மொபைல் மாடல் (${device.marketingName})",
        summaryEn = "${device.screenInches}\" • ${device.touchSamplingHz}Hz Touch • ${device.chipsetTier}",
        summaryTa = "${device.screenInches}\" திரை • ${device.touchSamplingHz}Hz டச் • ${device.chipsetTier}",
        generalDelta = 0,
        redDotDelta = 0,
      ),
      FactorImpact(
        titleEn = "Touch Response",
        titleTa = "டச் ரெஸ்பான்ஸ்",
        summaryEn = config.touchResponse.labelEn,
        summaryTa = config.touchResponse.labelTa,
        generalDelta = touchGeneralDelta,
        redDotDelta = touchRedDotDelta,
      ),
      FactorImpact(
        titleEn = "Refresh Rate (${config.refreshRate.label})",
        titleTa = "ரெஃப்ரெஷ் ரேட் (${config.refreshRate.label})",
        summaryEn = if (config.refreshRate.hz >= 120) {
          "Smooth high-Hz motion allows sharper recoil lock"
        } else {
          "Higher boost compensates for ${config.refreshRate.hz}Hz frame spacing"
        },
        summaryTa = if (config.refreshRate.hz >= 120) {
          "அதிக Hz திரையில் துல்லியமான ஹெட் லாக் கிடைக்கும்"
        } else {
          "${config.refreshRate.hz}Hz திரைக்காக கூடுதல் டிராக் வேகம் சேர்க்கப்பட்டது"
        },
        generalDelta = hzGeneralDelta,
        redDotDelta = hzRedDotDelta,
      ),
      FactorImpact(
        titleEn = "Game Settings (${config.graphics.labelEn} • ${config.playStyle.labelEn})",
        titleTa = "கேம் செட்டிங்ஸ் (${config.graphics.labelTa})",
        summaryEn = "${config.fps.labelEn} • ${config.aimPrecision.labelEn} • ${config.fingerGrip.labelEn}",
        summaryTa = "${config.fps.labelTa} • ${config.aimPrecision.labelTa} • ${config.fingerGrip.labelTa}",
        generalDelta = gameGeneralDelta,
        redDotDelta = gameRedDotDelta,
      ),
    )

    val (tipEn, tipTa) = buildDragTip(device, config, fireButton)

    // Adjust recommended DPI slightly for touch tier (Android only; 0 means iOS)
    val dpi = if (device.isIos) {
      0
    } else {
      val dpiOffset = when {
        touchGeneralDelta > 4 -> 20
        touchGeneralDelta < -4 -> -15
        else -> 0
      }
      (device.recommendedDpi + dpiOffset).coerceIn(380, 560)
    }

    return CalculatedSensitivity(
      device = device,
      general = general,
      redDot = redDot,
      scope2x = scope2x,
      scope4x = scope4x,
      sniperScope = sniperScope,
      freeLook = freeLook,
      fireButtonSize = fireButton,
      recommendedDpi = dpi,
      consistencyScore = score,
      dragTipEn = tipEn,
      dragTipTa = tipTa,
      factors = factors,
    )
  }

  private fun buildDragTip(
    device: DeviceProfile,
    config: CalibrationConfig,
    fireButton: Int,
  ): Pair<String, String> {
    val styleEn = when (config.playStyle.name) {
      "ONE_TAP" -> "Place crosshair slightly below opponent chest, then flick smoothly upward in a quick 'J' arc with $fireButton% Fire Button."
      "SMG_SPRAY" -> "Lock white dot on chest and glide straight upward smoothly—avoid sudden jerks so SMG bullets stay on the head."
      else -> "Mix short upward drags for close fights and controlled 2x bursts at mid-range."
    }
    val styleTa = when (config.playStyle.name) {
      "ONE_TAP" -> "எய்மை எதிரியின் நெஞ்சுக்குச் சற்று கீழே வைத்து, $fireButton% Fire Button-ஐ விரைவாக 'J' வடிவில் மேல்நோக்கி இழுக்கவும்."
      "SMG_SPRAY" -> "நெஞ்சு பகுதியில் எய்ம் வைத்து சீராக மேல்நோக்கி இழுத்தால் MP40/UMP தோட்டாக்கள் தலையில் லாக் ஆகும்."
      else -> "குளோஸ் ரேஞ்சில் சிறிய அப்-டிராக் மற்றும் மிட்-ரேஞ்சில் 2x பர்ஸ்ட் ஷாட் பயன்படுத்தவும்."
    }

    val dpiNoteEn = if (device.isIos) {
      " On iOS, set AssistiveTouch Tracking Speed to 75%."
    } else {
      " Keep ${device.recommendedDpi} DPI or default width for safe touch accuracy."
    }
    val dpiNoteTa = if (device.isIos) {
      " iOS-ல் Tracking Speed 75% வைக்கவும்."
    } else {
      " Android Developer Options-ல் ${device.recommendedDpi} DPI வைத்தால் துல்லியம் இன்னும் கூடும்."
    }

    return (styleEn + dpiNoteEn) to (styleTa + dpiNoteTa)
  }

  fun formatClipboardSummary(result: CalculatedSensitivity, config: CalibrationConfig): String {
    val dpiLine = if (result.device.isIos) "iOS Pointer Speed: 75%" else "Recommended DPI: ${result.recommendedDpi}"
    return buildString {
      appendLine("🔥 Free Fire India Headshot Sensitivity")
      appendLine("📱 Model: ${result.device.marketingName}")
      appendLine("⚡ Setup: ${config.refreshRate.label} | ${config.touchResponse.labelEn} | ${config.playStyle.labelEn}")
      appendLine("────────────────────")
      appendLine("• General: ${result.general}")
      appendLine("• Red Dot: ${result.redDot}")
      appendLine("• 2x Scope: ${result.scope2x}")
      appendLine("• 4x Scope: ${result.scope4x}")
      appendLine("• Sniper Scope: ${result.sniperScope}")
      appendLine("• Free Look: ${result.freeLook}")
      appendLine("────────────────────")
      appendLine("🎯 Fire Button Size: ${result.fireButtonSize}%")
      appendLine("📐 $dpiLine")
    }
  }
}
