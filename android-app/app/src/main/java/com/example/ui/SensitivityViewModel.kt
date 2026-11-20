package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DeviceCatalog
import com.example.data.DeviceDetectionHelper
import com.example.data.PreferencesRepository
import com.example.data.SensitivityCalculator
import com.example.model.AimPrecision
import com.example.model.AppLanguage
import com.example.model.CalculatedSensitivity
import com.example.model.CalibrationConfig
import com.example.model.DetectedDeviceInfo
import com.example.model.DetectionStatus
import com.example.model.DeviceBrand
import com.example.model.DeviceProfile
import com.example.model.FingerGrip
import com.example.model.FpsSetting
import com.example.model.GraphicsSetting
import com.example.model.PlayStyle
import com.example.model.RefreshRateOption
import com.example.model.SavedSensitivityPreset
import com.example.model.TouchResponseTier
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SensiUiState(
  val detectedInfo: DetectedDeviceInfo,
  val activeDevice: DeviceProfile,
  val isUsingAutoDetectedDevice: Boolean,
  val needsManualModelConfirmation: Boolean,
  val config: CalibrationConfig,
  val calculated: CalculatedSensitivity,
  val language: AppLanguage = AppLanguage.TA,
  val savedPresets: List<SavedSensitivityPreset> = emptyList(),
  val selectedBrandFilter: DeviceBrand? = null,
  val searchQuery: String = "",
  val showDevicePickerSheet: Boolean = false,
  val showSaveDialog: Boolean = false,
)

class SensitivityViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = PreferencesRepository(application.applicationContext)
  private val detectedInfo: DetectedDeviceInfo =
    DeviceDetectionHelper.detectCurrentDevice(application.applicationContext)

  private val initialDevice: DeviceProfile =
    detectedInfo.matchedDevice
      ?: detectedInfo.matchedBrand?.let { DeviceCatalog.genericBrandFallback(it) }
      ?: DeviceCatalog.allDevices.first()

  private val initialConfig = CalibrationConfig(
    refreshRate = DeviceDetectionHelper.nearestRefreshRateOption(detectedInfo.maxSupportedHz),
  )

  private val _uiState = MutableStateFlow(
    SensiUiState(
      detectedInfo = detectedInfo,
      activeDevice = initialDevice,
      isUsingAutoDetectedDevice = detectedInfo.status == DetectionStatus.EXACT_MATCH,
      needsManualModelConfirmation = detectedInfo.status != DetectionStatus.EXACT_MATCH,
      config = initialConfig,
      calculated = SensitivityCalculator.calculate(initialDevice, initialConfig),
    ),
  )
  val uiState: StateFlow<SensiUiState> = _uiState.asStateFlow()

  private var initializedFromStore = false

  init {
    viewModelScope.launch {
      repository.stateFlow.collect { stored ->
        val selectedId = stored.selectedDeviceId
        val resolvedDevice = when {
          selectedId != null -> {
            DeviceCatalog.findById(selectedId)
              ?: parseGenericBrandId(selectedId)
              ?: initialDevice
          }
          detectedInfo.matchedDevice != null -> detectedInfo.matchedDevice
          detectedInfo.matchedBrand != null -> DeviceCatalog.genericBrandFallback(detectedInfo.matchedBrand)
          else -> initialDevice
        }

        val effectiveConfig = if (!initializedFromStore && selectedId == null) {
          // On first launch, sync detected hardware Hz cleanly
          stored.config.copy(
            refreshRate = DeviceDetectionHelper.nearestRefreshRateOption(detectedInfo.maxSupportedHz),
          )
        } else {
          stored.config
        }
        initializedFromStore = true

        val isAuto = selectedId == null && detectedInfo.status == DetectionStatus.EXACT_MATCH
        val needsManual = selectedId == null && detectedInfo.status != DetectionStatus.EXACT_MATCH

        _uiState.update { current ->
          current.copy(
            activeDevice = resolvedDevice,
            isUsingAutoDetectedDevice = isAuto,
            needsManualModelConfirmation = needsManual,
            config = effectiveConfig,
            calculated = SensitivityCalculator.calculate(resolvedDevice, effectiveConfig),
            language = stored.language,
            savedPresets = stored.savedPresets,
          )
        }
      }
    }
  }

  private fun parseGenericBrandId(id: String): DeviceProfile? {
    if (!id.startsWith("generic_")) return null
    val brandKey = id.removePrefix("generic_").uppercase()
    val brand = DeviceBrand.entries.firstOrNull { it.name == brandKey } ?: return null
    return DeviceCatalog.genericBrandFallback(brand)
  }

  fun toggleLanguage() {
    val next = if (_uiState.value.language == AppLanguage.TA) AppLanguage.EN else AppLanguage.TA
    _uiState.update { it.copy(language = next) }
    viewModelScope.launch { repository.saveLanguage(next) }
  }

  fun openDevicePicker(preselectBrand: DeviceBrand? = null) {
    _uiState.update {
      it.copy(
        showDevicePickerSheet = true,
        selectedBrandFilter = preselectBrand ?: it.selectedBrandFilter,
      )
    }
  }

  fun closeDevicePicker() {
    _uiState.update { it.copy(showDevicePickerSheet = false) }
  }

  fun setBrandFilter(brand: DeviceBrand?) {
    _uiState.update { it.copy(selectedBrandFilter = brand) }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun selectDevice(device: DeviceProfile) {
    val suggestedHz = DeviceDetectionHelper.nearestRefreshRateOption(device.nativeMaxHz)
    val updatedConfig = _uiState.value.config.copy(refreshRate = suggestedHz)
    _uiState.update {
      it.copy(
        activeDevice = device,
        isUsingAutoDetectedDevice = device.id == detectedInfo.matchedDevice?.id,
        needsManualModelConfirmation = false,
        config = updatedConfig,
        calculated = SensitivityCalculator.calculate(device, updatedConfig),
        showDevicePickerSheet = false,
      )
    }
    viewModelScope.launch {
      repository.saveSelectedDeviceId(device.id)
      repository.saveCalibrationConfig(updatedConfig)
    }
  }

  fun resetToAutoDetectedDevice() {
    val target = detectedInfo.matchedDevice
      ?: detectedInfo.matchedBrand?.let { DeviceCatalog.genericBrandFallback(it) }
      ?: initialDevice
    val hwHz = DeviceDetectionHelper.nearestRefreshRateOption(detectedInfo.maxSupportedHz)
    val updatedConfig = _uiState.value.config.copy(refreshRate = hwHz)
    _uiState.update {
      it.copy(
        activeDevice = target,
        isUsingAutoDetectedDevice = detectedInfo.status == DetectionStatus.EXACT_MATCH,
        needsManualModelConfirmation = detectedInfo.status != DetectionStatus.EXACT_MATCH,
        config = updatedConfig,
        calculated = SensitivityCalculator.calculate(target, updatedConfig),
      )
    }
    viewModelScope.launch {
      repository.saveSelectedDeviceId(null)
      repository.saveCalibrationConfig(updatedConfig)
    }
  }

  fun updateTouchResponse(tier: TouchResponseTier) {
    updateConfig { it.copy(touchResponse = tier) }
  }

  fun applyMeasuredDragVelocity(velocityPxPerSec: Float) {
    updateConfig {
      it.copy(
        touchResponse = TouchResponseTier.CUSTOM_CALIBRATED,
        measuredSwipeVelocityPxPerSec = velocityPxPerSec,
      )
    }
  }

  fun updateRefreshRate(hz: RefreshRateOption) {
    updateConfig { it.copy(refreshRate = hz) }
  }

  fun updateGraphics(graphics: GraphicsSetting) {
    updateConfig { it.copy(graphics = graphics) }
  }

  fun updateFps(fps: FpsSetting) {
    updateConfig { it.copy(fps = fps) }
  }

  fun updateAimPrecision(aim: AimPrecision) {
    updateConfig { it.copy(aimPrecision = aim) }
  }

  fun updatePlayStyle(style: PlayStyle) {
    updateConfig { it.copy(playStyle = style) }
  }

  fun updateFingerGrip(grip: FingerGrip) {
    updateConfig { it.copy(fingerGrip = grip) }
  }

  private fun updateConfig(transform: (CalibrationConfig) -> CalibrationConfig) {
    val current = _uiState.value
    val updated = transform(current.config)
    _uiState.update {
      it.copy(
        config = updated,
        calculated = SensitivityCalculator.calculate(current.activeDevice, updated),
      )
    }
    viewModelScope.launch { repository.saveCalibrationConfig(updated) }
  }

  fun openSaveDialog() {
    _uiState.update { it.copy(showSaveDialog = true) }
  }

  fun closeSaveDialog() {
    _uiState.update { it.copy(showSaveDialog = false) }
  }

  fun saveCurrentPreset(customTitle: String) {
    val state = _uiState.value
    val calc = state.calculated
    val title = customTitle.trim().ifBlank {
      "${state.activeDevice.marketingName} • ${state.config.refreshRate.label}"
    }
    val preset = SavedSensitivityPreset(
      id = UUID.randomUUID().toString(),
      title = title,
      deviceId = state.activeDevice.id,
      deviceName = state.activeDevice.marketingName,
      brand = state.activeDevice.brand,
      general = calc.general,
      redDot = calc.redDot,
      scope2x = calc.scope2x,
      scope4x = calc.scope4x,
      sniperScope = calc.sniperScope,
      freeLook = calc.freeLook,
      fireButtonSize = calc.fireButtonSize,
      refreshHz = state.config.refreshRate.hz,
      touchLabel = state.config.touchResponse.labelEn,
      createdAtMillis = System.currentTimeMillis(),
    )
    _uiState.update { it.copy(showSaveDialog = false) }
    viewModelScope.launch { repository.addPreset(preset) }
  }

  fun loadPreset(preset: SavedSensitivityPreset) {
    val device = DeviceCatalog.findById(preset.deviceId)
      ?: DeviceCatalog.genericBrandFallback(preset.brand)
    val hzOption = DeviceDetectionHelper.nearestRefreshRateOption(preset.refreshHz)
    val updatedConfig = _uiState.value.config.copy(refreshRate = hzOption)
    _uiState.update {
      it.copy(
        activeDevice = device,
        isUsingAutoDetectedDevice = device.id == detectedInfo.matchedDevice?.id,
        needsManualModelConfirmation = false,
        config = updatedConfig,
        calculated = SensitivityCalculator.calculate(device, updatedConfig),
      )
    }
    viewModelScope.launch {
      repository.saveSelectedDeviceId(device.id)
      repository.saveCalibrationConfig(updatedConfig)
    }
  }

  fun deletePreset(presetId: String) {
    viewModelScope.launch { repository.deletePreset(presetId) }
  }
}
