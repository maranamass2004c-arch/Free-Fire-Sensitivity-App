package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.DetectionStatus
import com.example.model.DeviceBrand
import com.example.ui.SensiUiState
import com.example.ui.UiCopy
import com.example.ui.components.HeadshotReticleRing
import com.example.ui.components.SensitivityMetricCard
import com.example.ui.components.brandColor
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.BlazeOrangeContainer
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HeadshotGreen
import com.example.ui.theme.MoltenGold
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.ObsidianOutline
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LabScreen(
  state: SensiUiState,
  copy: UiCopy,
  onOpenDevicePicker: (DeviceBrand?) -> Unit,
  onResetToAutoDetect: () -> Unit,
  onNavigateToCalibrate: () -> Unit,
  onCopyAll: () -> Unit,
  onOpenSaveDialog: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val calc = state.calculated
  val device = state.activeDevice
  val brandTint = brandColor(device.brand)

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
      // 1. Device Detection Hero Card
      item {
        Surface(
          shape = MaterialTheme.shapes.large,
          color = ObsidianSurface,
          border = BorderStroke(1.dp, brandTint.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Box(modifier = Modifier.fillMaxWidth()) {
            Image(
              painter = painterResource(id = R.drawable.img_hero_esports),
              contentDescription = null,
              contentScale = ContentScale.Crop,
              alpha = 0.34f,
              modifier = Modifier.matchParentSize(),
            )
            Box(
              modifier = Modifier
                .matchParentSize()
                .background(
                  Brush.verticalGradient(
                    listOf(
                      ObsidianBg.copy(alpha = 0.55f),
                      ObsidianSurface.copy(alpha = 0.94f),
                    ),
                  ),
                ),
            )

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
              // Status Pill
              val (statusText, statusColor, statusIcon) = when {
                state.isUsingAutoDetectedDevice -> Triple(
                  copy.autoDetectedBadge,
                  HeadshotGreen,
                  Icons.Filled.CheckCircle,
                )
                state.needsManualModelConfirmation &&
                  state.detectedInfo.status == DetectionStatus.BRAND_MATCH_NEEDS_MODEL -> Triple(
                  copy.brandMatchedBadge,
                  MoltenGold,
                  Icons.Filled.WarningAmber,
                )
                state.needsManualModelConfirmation -> Triple(
                  copy.manualPickNeededBadge,
                  MoltenGold,
                  Icons.Filled.WarningAmber,
                )
                else -> Triple(
                  copy.manualOverrideBadge,
                  ElectricCyan,
                  Icons.Filled.SwapHoriz,
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Surface(
                  shape = CircleShape,
                  color = statusColor.copy(alpha = 0.14f),
                  border = BorderStroke(1.dp, statusColor.copy(alpha = 0.45f)),
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                  ) {
                    Icon(
                      imageVector = statusIcon,
                      contentDescription = null,
                      tint = statusColor,
                      modifier = Modifier.size(14.dp),
                    )
                    Text(
                      text = statusText,
                      style = MaterialTheme.typography.labelSmall,
                      color = statusColor,
                    )
                  }
                }

                if (!state.isUsingAutoDetectedDevice &&
                  state.detectedInfo.status == DetectionStatus.EXACT_MATCH
                ) {
                  TextButton(
                    onClick = onResetToAutoDetect,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("reset_auto_detect_button"),
                  ) {
                    Icon(
                      imageVector = Icons.Filled.Refresh,
                      contentDescription = null,
                      modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                      text = copy.useAutoDetectBtn,
                      style = MaterialTheme.typography.labelSmall,
                    )
                  }
                }
              }

              // Main Device Info + Reticle Ring
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Column(
                  modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                  ) {
                    Icon(
                      imageVector = if (device.isIos) Icons.Filled.PhoneIphone else Icons.Filled.PhoneAndroid,
                      contentDescription = null,
                      tint = brandTint,
                      modifier = Modifier.size(20.dp),
                    )
                    Text(
                      text = if (state.language == AppLanguage.TA) {
                        device.brand.displayNameTa
                      } else {
                        device.brand.displayName
                      },
                      style = MaterialTheme.typography.labelMedium,
                      color = brandTint,
                    )
                  }

                  Text(
                    text = device.marketingName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                  )

                  Text(
                    text = "${device.screenInches}\" • ${device.nativeMaxHz}Hz • ${device.touchSamplingHz}Hz Touch • ${device.chipsetTier}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                  )

                  Text(
                    text = "${copy.hardwareLabel}: ${state.detectedInfo.manufacturer} ${state.detectedInfo.modelRaw} (${state.detectedInfo.detectedRefreshHz}Hz)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }

                HeadshotReticleRing(
                  score = calc.consistencyScore,
                  label = copy.headshotConsistency,
                )
              }

              if (state.needsManualModelConfirmation) {
                Surface(
                  shape = MaterialTheme.shapes.medium,
                  color = MoltenGold.copy(alpha = 0.12f),
                  border = BorderStroke(1.dp, MoltenGold.copy(alpha = 0.45f)),
                  modifier = Modifier.fillMaxWidth(),
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                  ) {
                    Icon(
                      imageVector = Icons.Filled.Info,
                      contentDescription = null,
                      tint = MoltenGold,
                      modifier = Modifier.size(18.dp),
                    )
                    Text(
                      text = copy.modelNotFoundBanner,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextPrimary,
                      modifier = Modifier.weight(1f),
                    )
                  }
                }
              }

              // Action row inside hero
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Button(
                  onClick = { onOpenDevicePicker(null) },
                  colors = ButtonDefaults.buttonColors(containerColor = BlazeOrange),
                  shape = MaterialTheme.shapes.medium,
                  modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("open_device_picker_button"),
                ) {
                  Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                  )
                  Spacer(Modifier.width(6.dp))
                  Text(
                    text = if (state.needsManualModelConfirmation) copy.pickModelBtn else copy.changePhoneBtn,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }

                OutlinedButton(
                  onClick = onNavigateToCalibrate,
                  shape = MaterialTheme.shapes.medium,
                  border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                  modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("tune_settings_button"),
                ) {
                  Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                  )
                  Spacer(Modifier.width(6.dp))
                  Text(
                    text = copy.tuneSettingsBtn,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }
              }
            }
          }
        }
      }

      // 2. Quick Brand Switch Strip (Samsung, Vivo, iPhone, iQOO, OnePlus, Redmi/POCO)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = copy.quickBrandPickTitle,
            style = MaterialTheme.typography.titleSmall,
            color = TextSecondary,
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            DeviceBrand.entries.forEach { brand ->
              val isActiveBrand = device.brand == brand
              val accent = brandColor(brand)
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isActiveBrand) accent.copy(alpha = 0.2f) else ObsidianSurfaceVariant,
                border = BorderStroke(
                  width = 1.dp,
                  color = if (isActiveBrand) accent else ObsidianOutline,
                ),
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .clickable { onOpenDevicePicker(brand) }
                  .testTag("quick_brand_${brand.name.lowercase()}"),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(accent),
                  )
                  Text(
                    text = if (state.language == AppLanguage.TA) brand.displayNameTa else brand.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isActiveBrand) accent else TextPrimary,
                  )
                }
              }
            }
          }
        }
      }

      // 3. Sensitivity Header + Active Factors Pill Summary
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = copy.sensitivityHeader,
              style = MaterialTheme.typography.titleLarge,
              color = TextPrimary,
            )
            Text(
              text = "${state.config.refreshRate.label} • ${
                if (state.language == AppLanguage.TA) state.config.touchResponse.labelTa
                else state.config.touchResponse.labelEn
              } • ${
                if (state.language == AppLanguage.TA) state.config.playStyle.labelTa
                else state.config.playStyle.labelEn
              }",
              style = MaterialTheme.typography.bodySmall,
              color = ElectricCyan,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = BlazeOrangeContainer,
          ) {
            Text(
              text = copy.scaleHint,
              style = MaterialTheme.typography.labelSmall,
              color = BlazeOrange,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
          }
        }
      }

      // 4. 6-Stat Headshot Sensitivity 2x3 Grid
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            SensitivityMetricCard(
              label = copy.generalLabel,
              value = calc.general,
              baseValue = device.baseGeneral,
              accentColor = BlazeOrange,
              modifier = Modifier.weight(1f),
            )
            SensitivityMetricCard(
              label = copy.redDotLabel,
              value = calc.redDot,
              baseValue = device.baseRedDot,
              accentColor = ElectricCyan,
              modifier = Modifier.weight(1f),
            )
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            SensitivityMetricCard(
              label = copy.scope2xLabel,
              value = calc.scope2x,
              baseValue = device.base2xScope,
              accentColor = MoltenGold,
              modifier = Modifier.weight(1f),
            )
            SensitivityMetricCard(
              label = copy.scope4xLabel,
              value = calc.scope4x,
              baseValue = device.base4xScope,
              accentColor = HeadshotGreen,
              modifier = Modifier.weight(1f),
            )
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            SensitivityMetricCard(
              label = copy.sniperLabel,
              value = calc.sniperScope,
              baseValue = device.baseSniperScope,
              accentColor = Color(0xFFA78BFA),
              modifier = Modifier.weight(1f),
            )
            SensitivityMetricCard(
              label = copy.freeLookLabel,
              value = calc.freeLook,
              baseValue = device.baseFreeLook,
              accentColor = Color(0xFF38BDF8),
              modifier = Modifier.weight(1f),
            )
          }
        }
      }

      // 5. Copy & Save Primary Action Row
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Button(
            onClick = onCopyAll,
            colors = ButtonDefaults.buttonColors(containerColor = BlazeOrange),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
              .weight(1.4f)
              .height(50.dp)
              .testTag("copy_all_button"),
          ) {
            Icon(
              imageVector = Icons.Filled.ContentCopy,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(copy.copyAllBtn, fontWeight = FontWeight.Bold)
          }
          OutlinedButton(
            onClick = onOpenSaveDialog,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MoltenGold.copy(alpha = 0.7f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MoltenGold),
            modifier = Modifier
              .weight(1f)
              .height(50.dp)
              .testTag("save_profile_button"),
          ) {
            Icon(
              imageVector = Icons.Filled.BookmarkAdd,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(copy.saveProfileBtn, fontWeight = FontWeight.SemiBold)
          }
        }
      }

      // 6. Pro Bonus: Fire Button Size, DPI / iOS Tracking & Drag Tip
      item {
        Surface(
          shape = MaterialTheme.shapes.large,
          color = ObsidianSurfaceVariant,
          border = BorderStroke(1.dp, ObsidianOutline),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Text(
              text = copy.proBonusHeader,
              style = MaterialTheme.typography.titleMedium,
              color = MoltenGold,
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Surface(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                color = ObsidianCardElevated,
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = copy.fireButtonLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                  )
                  Spacer(Modifier.height(4.dp))
                  Text(
                    text = "${calc.fireButtonSize}%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = BlazeOrange,
                  )
                }
              }

              Surface(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                color = ObsidianCardElevated,
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = if (device.isIos) copy.iosPointerLabel else copy.dpiLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                  )
                  Spacer(Modifier.height(4.dp))
                  Text(
                    text = if (device.isIos) "75%" else "${calc.recommendedDpi}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ElectricCyan,
                  )
                }
              }
            }

            Surface(
              shape = MaterialTheme.shapes.medium,
              color = ObsidianSurface,
              border = BorderStroke(1.dp, HeadshotGreen.copy(alpha = 0.25f)),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
              ) {
                Text(
                  text = copy.dragTipHeader,
                  style = MaterialTheme.typography.labelLarge,
                  color = HeadshotGreen,
                )
                Text(
                  text = if (state.language == AppLanguage.TA) calc.dragTipTa else calc.dragTipEn,
                  style = MaterialTheme.typography.bodyMedium,
                  color = TextPrimary,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                  text = if (state.language == AppLanguage.TA) device.headshotNoteTa else device.headshotNoteEn,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary,
                )
              }
            }
          }
        }
      }

      // 7. Multi-Factor Calibration Breakdown (Shows Model + Touch + Hz + Game Settings)
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
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.GraphicEq,
                contentDescription = null,
                tint = ElectricCyan,
              )
              Text(
                text = copy.factorBreakdownHeader,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
              )
            }

            calc.factors.forEach { factor ->
              Surface(
                shape = MaterialTheme.shapes.small,
                color = ObsidianSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Column(
                    modifier = Modifier
                      .weight(1f)
                      .padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                  ) {
                    Text(
                      text = if (state.language == AppLanguage.TA) factor.titleTa else factor.titleEn,
                      style = MaterialTheme.typography.titleSmall,
                      color = TextPrimary,
                    )
                    Text(
                      text = if (state.language == AppLanguage.TA) factor.summaryTa else factor.summaryEn,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextSecondary,
                    )
                  }

                  val deltaStr = when {
                    factor.generalDelta > 0 -> "+${factor.generalDelta} Gen"
                    factor.generalDelta < 0 -> "${factor.generalDelta} Gen"
                    else -> "Base"
                  }
                  val deltaColor = when {
                    factor.generalDelta > 0 -> ElectricCyan
                    factor.generalDelta < 0 -> MoltenGold
                    else -> HeadshotGreen
                  }
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = deltaColor.copy(alpha = 0.14f),
                  ) {
                    Text(
                      text = deltaStr,
                      style = MaterialTheme.typography.labelSmall,
                      color = deltaColor,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                  }
                }
              }
            }
          }
        }
      }

      item { Spacer(Modifier.height(12.dp)) }
    }
  }
}
