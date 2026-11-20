package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AimPrecision
import com.example.model.AppLanguage
import com.example.model.FingerGrip
import com.example.model.FpsSetting
import com.example.model.GraphicsSetting
import com.example.model.PlayStyle
import com.example.model.RefreshRateOption
import com.example.model.TouchResponseTier
import com.example.ui.SensiUiState
import com.example.ui.UiCopy
import com.example.ui.components.TouchDragCalibrationPad
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.BlazeOrangeContainer
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanContainer
import com.example.ui.theme.HeadshotGreen
import com.example.ui.theme.MoltenGold
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.ObsidianOutline
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CalibrateScreen(
  state: SensiUiState,
  copy: UiCopy,
  onTouchResponseChange: (TouchResponseTier) -> Unit,
  onDragVelocityMeasured: (Float) -> Unit,
  onRefreshRateChange: (RefreshRateOption) -> Unit,
  onGraphicsChange: (GraphicsSetting) -> Unit,
  onFpsChange: (FpsSetting) -> Unit,
  onAimPrecisionChange: (AimPrecision) -> Unit,
  onPlayStyleChange: (PlayStyle) -> Unit,
  onFingerGripChange: (FingerGrip) -> Unit,
  modifier: Modifier = Modifier,
) {
  val isTa = state.language == AppLanguage.TA
  val cfg = state.config
  val calc = state.calculated

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 720.dp),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Live Preview Banner
      item {
        Surface(
          shape = MaterialTheme.shapes.large,
          color = ObsidianCardElevated,
          border = BorderStroke(1.dp, BlazeOrange.copy(alpha = 0.45f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = copy.calibrateTitle,
              style = MaterialTheme.typography.titleLarge,
              color = TextPrimary,
            )
            Text(
              text = copy.calibrateSubtitle,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary,
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              LiveStatPill(label = "General", value = "${calc.general}", color = BlazeOrange, modifier = Modifier.weight(1f))
              LiveStatPill(label = "Red Dot", value = "${calc.redDot}", color = ElectricCyan, modifier = Modifier.weight(1f))
              LiveStatPill(label = "2x", value = "${calc.scope2x}", color = MoltenGold, modifier = Modifier.weight(1f))
              LiveStatPill(label = "Fire", value = "${calc.fireButtonSize}%", color = HeadshotGreen, modifier = Modifier.weight(1f))
            }
          }
        }
      }

      // Section 1: Touch Response
      item {
        Surface(
          shape = MaterialTheme.shapes.large,
          color = ObsidianSurface,
          border = BorderStroke(1.dp, ObsidianOutline),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = copy.touchSectionTitle,
              style = MaterialTheme.typography.titleMedium,
              color = ElectricCyan,
            )

            TouchResponseTier.entries.forEach { tier ->
              val selected = cfg.touchResponse == tier
              Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (selected) ElectricCyanContainer else ObsidianSurfaceVariant,
                border = BorderStroke(
                  width = if (selected) 1.5.dp else 1.dp,
                  color = if (selected) ElectricCyan else ObsidianOutline,
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(MaterialTheme.shapes.medium)
                  .clickable { onTouchResponseChange(tier) }
                  .testTag("touch_tier_${tier.name.lowercase()}"),
              ) {
                Column(
                  modifier = Modifier.padding(12.dp),
                  verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                  Text(
                    text = if (isTa) tier.labelTa else tier.labelEn,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (selected) ElectricCyan else TextPrimary,
                  )
                  Text(
                    text = if (isTa) tier.detailTa else tier.detailEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                  )
                }
              }
            }
          }
        }
      }

      // Interactive Swipe Drag Pad
      item {
        TouchDragCalibrationPad(
          copy = copy,
          currentVelocity = cfg.measuredSwipeVelocityPxPerSec,
          onCalibrationComplete = onDragVelocityMeasured,
        )
      }

      // Section 2: Display Refresh Rate
      item {
        Surface(
          shape = MaterialTheme.shapes.large,
          color = ObsidianSurface,
          border = BorderStroke(1.dp, ObsidianOutline),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = copy.refreshRateTitle,
              style = MaterialTheme.typography.titleMedium,
              color = MoltenGold,
            )
            Text(
              text = String.format(
                copy.detectedHzHint,
                state.detectedInfo.detectedRefreshHz,
                state.detectedInfo.maxSupportedHz,
              ),
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary,
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              RefreshRateOption.entries.forEach { option ->
                val selected = cfg.refreshRate == option
                Surface(
                  shape = MaterialTheme.shapes.medium,
                  color = if (selected) BlazeOrangeContainer else ObsidianSurfaceVariant,
                  border = BorderStroke(
                    width = if (selected) 1.5.dp else 1.dp,
                    color = if (selected) BlazeOrange else ObsidianOutline,
                  ),
                  modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onRefreshRateChange(option) }
                    .testTag("hz_option_${option.hz}"),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = option.label,
                      style = MaterialTheme.typography.titleSmall,
                      color = if (selected) BlazeOrange else TextPrimary,
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Section 3: Free Fire India Game Settings
      item {
        Surface(
          shape = MaterialTheme.shapes.large,
          color = ObsidianSurface,
          border = BorderStroke(1.dp, ObsidianOutline),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Text(
              text = copy.gameSettingsTitle,
              style = MaterialTheme.typography.titleMedium,
              color = HeadshotGreen,
            )

            // Graphics Quality
            SettingChipGroup(
              title = copy.graphicsLabel,
              items = GraphicsSetting.entries,
              selectedItem = cfg.graphics,
              labelFor = { if (isTa) it.labelTa else it.labelEn },
              tagPrefix = "graphics",
              onSelect = onGraphicsChange,
            )

            // High FPS
            SettingChipGroup(
              title = copy.fpsLabel,
              items = FpsSetting.entries,
              selectedItem = cfg.fps,
              labelFor = { if (isTa) it.labelTa else it.labelEn },
              tagPrefix = "fps",
              onSelect = onFpsChange,
            )

            // Aim Precision
            SettingChipGroup(
              title = copy.aimPrecisionLabel,
              items = AimPrecision.entries,
              selectedItem = cfg.aimPrecision,
              labelFor = { if (isTa) it.labelTa else it.labelEn },
              tagPrefix = "aim",
              onSelect = onAimPrecisionChange,
            )

            // Playstyle
            SettingChipGroup(
              title = copy.playStyleLabel,
              items = PlayStyle.entries,
              selectedItem = cfg.playStyle,
              labelFor = { if (isTa) it.labelTa else it.labelEn },
              tagPrefix = "style",
              onSelect = onPlayStyleChange,
            )

            // Finger Grip
            SettingChipGroup(
              title = copy.fingerGripLabel,
              items = FingerGrip.entries,
              selectedItem = cfg.fingerGrip,
              labelFor = { if (isTa) it.labelTa else it.labelEn },
              tagPrefix = "grip",
              onSelect = onFingerGripChange,
            )
          }
        }
      }

      item { Spacer(Modifier.height(12.dp)) }
    }
  }
}

@Composable
private fun LiveStatPill(
  label: String,
  value: String,
  color: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = color.copy(alpha = 0.12f),
    border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        color = color,
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary,
      )
    }
  }
}

@Composable
private fun <T : Enum<T>> SettingChipGroup(
  title: String,
  items: List<T>,
  selectedItem: T,
  labelFor: (T) -> String,
  tagPrefix: String,
  onSelect: (T) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelLarge,
      color = TextSecondary,
    )
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      items.forEach { item ->
        val selected = item == selectedItem
        FilterChip(
          selected = selected,
          onClick = { onSelect(item) },
          label = { Text(labelFor(item)) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BlazeOrangeContainer,
            selectedLabelColor = BlazeOrange,
            containerColor = ObsidianSurfaceVariant,
            labelColor = TextPrimary,
          ),
          modifier = Modifier.testTag("${tagPrefix}_${item.name.lowercase()}"),
        )
      }
    }
  }
}
