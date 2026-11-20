package com.example

import com.example.data.DeviceCatalog
import com.example.data.SensitivityCalculator
import com.example.model.AimPrecision
import com.example.model.CalibrationConfig
import com.example.model.DeviceBrand
import com.example.model.FingerGrip
import com.example.model.FpsSetting
import com.example.model.GraphicsSetting
import com.example.model.PlayStyle
import com.example.model.RefreshRateOption
import com.example.model.TouchResponseTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SensitivityCalculatorTest {

  @Test
  fun everyDeviceHasDistinctValues() {
    val samsung = DeviceCatalog.byBrand(DeviceBrand.SAMSUNG)
    val vivo = DeviceCatalog.byBrand(DeviceBrand.VIVO)
    val iphone = DeviceCatalog.byBrand(DeviceBrand.IPHONE)

    assertTrue(samsung.isNotEmpty())
    assertTrue(vivo.isNotEmpty())
    assertTrue(iphone.isNotEmpty())

    val s24Ultra = DeviceCatalog.findById("samsung_s24_ultra")!!
    val vivoV29 = DeviceCatalog.findById("vivo_v29")!!
    val iphone15Pro = DeviceCatalog.findById("iphone_15_pro")!!

    val baseConfig = CalibrationConfig()
    val resSamsung = SensitivityCalculator.calculate(s24Ultra, baseConfig)
    val resVivo = SensitivityCalculator.calculate(vivoV29, baseConfig)
    val resIphone = SensitivityCalculator.calculate(iphone15Pro, baseConfig)

    assertNotEquals(resSamsung.general, resVivo.general)
    assertNotEquals(resVivo.general, resIphone.general)
    assertEquals(0, resIphone.recommendedDpi)
    assertTrue(resSamsung.recommendedDpi > 0)
  }

  @Test
  fun multiFactorChangesAdjustSensitivityWithinValidRange() {
    val vivoV30 = DeviceCatalog.findById("vivo_v30")!!

    val config60HzStandard = CalibrationConfig(
      touchResponse = TouchResponseTier.STANDARD_120,
      refreshRate = RefreshRateOption.HZ_60,
      graphics = GraphicsSetting.MAX,
      fps = FpsSetting.NORMAL,
      aimPrecision = AimPrecision.DEFAULT,
      playStyle = PlayStyle.ONE_TAP,
      fingerGrip = FingerGrip.TWO_FINGER,
    )

    val config144HzUltra = CalibrationConfig(
      touchResponse = TouchResponseTier.ULTRA_360,
      refreshRate = RefreshRateOption.HZ_144,
      graphics = GraphicsSetting.SMOOTH,
      fps = FpsSetting.HIGH,
      aimPrecision = AimPrecision.FULL_CONTROL,
      playStyle = PlayStyle.SMG_SPRAY,
      fingerGrip = FingerGrip.FOUR_FINGER,
    )

    val resSlow = SensitivityCalculator.calculate(vivoV30, config60HzStandard)
    val resFast = SensitivityCalculator.calculate(vivoV30, config144HzUltra)

    assertTrue(resSlow.general > resFast.general)
    assertTrue(resSlow.redDot > resFast.redDot)
    assertTrue(resSlow.general in 0..200)
    assertTrue(resFast.general in 0..200)
    assertTrue(resSlow.fireButtonSize in 35..70)
    assertTrue(resFast.fireButtonSize in 35..70)
  }

  @Test
  fun customDragVelocityCalibrationAdaptsToSwipeSpeed() {
    val device = DeviceCatalog.findById("samsung_a55")!!
    val slowSwipe = CalibrationConfig(
      touchResponse = TouchResponseTier.CUSTOM_CALIBRATED,
      measuredSwipeVelocityPxPerSec = 950f,
    )
    val fastSwipe = CalibrationConfig(
      touchResponse = TouchResponseTier.CUSTOM_CALIBRATED,
      measuredSwipeVelocityPxPerSec = 2900f,
    )

    val slowRes = SensitivityCalculator.calculate(device, slowSwipe)
    val fastRes = SensitivityCalculator.calculate(device, fastSwipe)

    assertTrue(slowRes.general > fastRes.general)
    assertTrue(slowRes.redDot > fastRes.redDot)
  }
}
