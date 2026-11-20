package com.example.model

enum class AppLanguage(val code: String, val label: String) {
  TA("ta", "தமிழ்"),
  EN("en", "EN"),
}

enum class DeviceBrand(
  val displayName: String,
  val displayNameTa: String,
  val taglineEn: String,
  val taglineTa: String,
) {
  SAMSUNG("Samsung", "சாம்சங்", "AMOLED & OneUI Touch Curve", "AMOLED & OneUI டச் அமைப்பு"),
  VIVO("Vivo", "விவோ", "Funtouch Smooth Drag Engine", "Funtouch மென்மையான டிராக்"),
  IPHONE("iPhone", "ஐபோன்", "iOS 120Hz/60Hz Precision Touch", "iOS துல்லிய டச் கட்டுப்பாடு"),
  IQOO("iQOO", "ஐக்யூ", "Monster Esports Touch Sampling", "Esports அதிவேக டச் ரெஸ்பான்ஸ்"),
  ONEPLUS("OnePlus", "ஒன்பிளஸ்", "OxygenOS HyperTouch Curve", "OxygenOS ஹைப்பர் டச்"),
  XIAOMI_POCO("Redmi / POCO", "ரெட்மி / போக்கோ", "MIUI / HyperOS Turbo Touch", "HyperOS டர்போ டச்"),
  REALME("Realme / Other", "ரியல்மி / பிற", "High-Sensitivity Gaming Curve", "கேமிங் ஹை-சென்சிட்டிவிட்டி"),
}

enum class TouchResponseTier(
  val labelEn: String,
  val labelTa: String,
  val detailEn: String,
  val detailTa: String,
  val generalDelta: Int,
  val redDotDelta: Int,
  val scopeDelta: Int,
  val fireButtonDelta: Int,
) {
  STANDARD_120(
    labelEn = "Standard (120Hz / Glass)",
    labelTa = "சாதாரணம் (120Hz / டெம்பர் கிளாஸ்)",
    detailEn = "Extra drag boost for thick tempered glass or 120Hz touch.",
    detailTa = "தடிமனான டெம்பர் கிளாஸ் அல்லது சாதாரண டச்சுக்கு கூடுதல் வேகம்.",
    generalDelta = 8,
    redDotDelta = 7,
    scopeDelta = 5,
    fireButtonDelta = -2,
  ),
  BALANCED_240(
    labelEn = "Balanced (180–240Hz)",
    labelTa = "சமநிலை (180–240Hz)",
    detailEn = "Default clean touch response for daily ranked matches.",
    detailTa = "தினசரி ரேங்க் போட்டிகளுக்கான நிலையான டச் பதில்.",
    generalDelta = 0,
    redDotDelta = 0,
    scopeDelta = 0,
    fireButtonDelta = 0,
  ),
  ULTRA_360(
    labelEn = "Ultra Gaming (360Hz+)",
    labelTa = "அல்ட்ரா கேமிங் (360Hz+)",
    detailEn = "Tightens Red Dot slightly to prevent head-overshoot.",
    detailTa = "தலைக்கு மேல் எய்ம் போவதைத் தடுக்க துல்லியக் கட்டுப்பாடு.",
    generalDelta = -5,
    redDotDelta = -7,
    scopeDelta = -4,
    fireButtonDelta = 2,
  ),
  CUSTOM_CALIBRATED(
    labelEn = "Drag-Test Calibrated",
    labelTa = "டிராக் டெஸ்ட் கலிப்ரேஷன்",
    detailEn = "Uses your live swipe velocity measured in the Drag Pad.",
    detailTa = "உங்கள் விரல் ஸ்வைப் வேகத்தை அளந்து கணக்கிடப்பட்டது.",
    generalDelta = 0,
    redDotDelta = 0,
    scopeDelta = 0,
    fireButtonDelta = 0,
  ),
}

enum class RefreshRateOption(
  val hz: Int,
  val label: String,
  val generalDelta: Int,
  val redDotDelta: Int,
  val scopeDelta: Int,
) {
  HZ_60(60, "60 Hz", 6, 5, 4),
  HZ_90(90, "90 Hz", 2, 2, 1),
  HZ_120(120, "120 Hz", -3, -2, -2),
  HZ_144(144, "144 Hz", -6, -4, -3),
}

enum class GraphicsSetting(
  val labelEn: String,
  val labelTa: String,
  val generalDelta: Int,
  val redDotDelta: Int,
) {
  SMOOTH("Smooth", "ஸ்மூத் (Smooth)", -2, -1),
  STANDARD("Standard", "ஸ்டாண்டர்ட் (Standard)", 0, 0),
  ULTRA("Ultra", "அல்ட்ரா (Ultra)", 3, 2),
  MAX("Max", "மேக்ஸ் (Max)", 5, 4),
}

enum class FpsSetting(
  val labelEn: String,
  val labelTa: String,
  val generalDelta: Int,
  val redDotDelta: Int,
) {
  NORMAL("Normal FPS", "நார்மல் FPS", 4, 3),
  HIGH("High FPS", "ஹை FPS", 0, 0),
}

enum class AimPrecision(
  val labelEn: String,
  val labelTa: String,
  val redDotDelta: Int,
  val scopeDelta: Int,
) {
  DEFAULT("Default", "டீஃபால்ட் (Default)", 0, 0),
  PRECISE_ON_SCOPE("Precise on Scope", "ப்ரிசெய்ஸ் ஸ்கோப்", -2, -5),
  FULL_CONTROL("Full Control", "ஃபுல் கண்ட்ரோல்", -5, -8),
}

enum class PlayStyle(
  val labelEn: String,
  val labelTa: String,
  val generalDelta: Int,
  val redDotDelta: Int,
  val scopeDelta: Int,
  val fireButtonDelta: Int,
) {
  ONE_TAP("One-Tap Headshot", "ஒன்-டாப் ஹெட்ஷாட்", 7, 5, 2, -4),
  SMG_SPRAY("SMG / MP40 Drag", "SMG / MP40 டிராக்", 2, -3, 3, 3),
  BALANCED("All-Rounder (BR/CS)", "ஆல்-ரவுண்டர் (BR/CS)", 0, 0, 0, 0),
}

enum class FingerGrip(
  val labelEn: String,
  val labelTa: String,
  val generalDelta: Int,
  val freeLookDelta: Int,
  val fireButtonDelta: Int,
) {
  TWO_FINGER("2-Finger", "2-விரல்", 3, -5, 2),
  THREE_FINGER("3-Finger Claw", "3-விரல் க்ளா", 0, 6, -1),
  FOUR_FINGER("4-Finger Claw", "4-விரல் க்ளா", -3, 12, -3),
}

data class DeviceProfile(
  val id: String,
  val brand: DeviceBrand,
  val marketingName: String,
  val modelCodes: List<String>,
  val screenInches: Double,
  val nativeMaxHz: Int,
  val touchSamplingHz: Int,
  val chipsetTier: String,
  val baseGeneral: Int,
  val baseRedDot: Int,
  val base2xScope: Int,
  val base4xScope: Int,
  val baseSniperScope: Int,
  val baseFreeLook: Int,
  val baseFireButton: Int,
  val recommendedDpi: Int,
  val isIos: Boolean = false,
  val headshotNoteEn: String,
  val headshotNoteTa: String,
)

data class CalibrationConfig(
  val touchResponse: TouchResponseTier = TouchResponseTier.BALANCED_240,
  val refreshRate: RefreshRateOption = RefreshRateOption.HZ_120,
  val graphics: GraphicsSetting = GraphicsSetting.SMOOTH,
  val fps: FpsSetting = FpsSetting.HIGH,
  val aimPrecision: AimPrecision = AimPrecision.DEFAULT,
  val playStyle: PlayStyle = PlayStyle.ONE_TAP,
  val fingerGrip: FingerGrip = FingerGrip.TWO_FINGER,
  val measuredSwipeVelocityPxPerSec: Float? = null,
)

enum class DetectionStatus {
  EXACT_MATCH,
  BRAND_MATCH_NEEDS_MODEL,
  UNMATCHED_DEVICE,
}

data class DetectedDeviceInfo(
  val manufacturer: String,
  val brandRaw: String,
  val modelRaw: String,
  val deviceCode: String,
  val androidVersion: String,
  val detectedRefreshHz: Int,
  val maxSupportedHz: Int,
  val currentSmallestWidthDp: Int,
  val status: DetectionStatus,
  val matchedDevice: DeviceProfile?,
  val matchedBrand: DeviceBrand?,
)

data class FactorImpact(
  val titleEn: String,
  val titleTa: String,
  val summaryEn: String,
  val summaryTa: String,
  val generalDelta: Int,
  val redDotDelta: Int,
)

data class CalculatedSensitivity(
  val device: DeviceProfile,
  val general: Int,
  val redDot: Int,
  val scope2x: Int,
  val scope4x: Int,
  val sniperScope: Int,
  val freeLook: Int,
  val fireButtonSize: Int,
  val recommendedDpi: Int,
  val consistencyScore: Int,
  val dragTipEn: String,
  val dragTipTa: String,
  val factors: List<FactorImpact>,
)

data class SavedSensitivityPreset(
  val id: String,
  val title: String,
  val deviceId: String,
  val deviceName: String,
  val brand: DeviceBrand,
  val general: Int,
  val redDot: Int,
  val scope2x: Int,
  val scope4x: Int,
  val sniperScope: Int,
  val freeLook: Int,
  val fireButtonSize: Int,
  val refreshHz: Int,
  val touchLabel: String,
  val createdAtMillis: Long,
)
