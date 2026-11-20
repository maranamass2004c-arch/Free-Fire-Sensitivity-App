package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.SavedSensitivityPreset
import com.example.ui.SensiUiState
import com.example.ui.UiCopy
import com.example.ui.components.brandColor
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HeadshotGreen
import com.example.ui.theme.MoltenGold
import com.example.ui.theme.ObsidianOutline
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SavedScreen(
  state: SensiUiState,
  copy: UiCopy,
  onLoadPreset: (SavedSensitivityPreset) -> Unit,
  onCopyPreset: (SavedSensitivityPreset) -> Unit,
  onDeletePreset: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    if (state.savedPresets.isEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Icon(
          imageVector = Icons.Filled.Bookmarks,
          contentDescription = null,
          tint = BlazeOrange.copy(alpha = 0.7f),
          modifier = Modifier.size(56.dp),
        )
        Spacer(Modifier.height(14.dp))
        Text(
          text = copy.emptySavedTitle,
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimary,
          textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
          text = copy.emptySavedDesc,
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary,
          textAlign = TextAlign.Center,
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 720.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        item {
          Text(
            text = copy.savedTitle,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
          )
        }

        items(state.savedPresets, key = { it.id }) { preset ->
          val accent = brandColor(preset.brand)
          Surface(
            shape = MaterialTheme.shapes.large,
            color = ObsidianSurface,
            border = BorderStroke(1.dp, ObsidianOutline),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = preset.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                  )
                  Text(
                    text = "${preset.deviceName} • ${preset.refreshHz}Hz • ${preset.touchLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = accent,
                  )
                }

                Row {
                  IconButton(
                    onClick = { onCopyPreset(preset) },
                    modifier = Modifier.testTag("copy_preset_${preset.id}"),
                  ) {
                    Icon(
                      imageVector = Icons.Filled.ContentCopy,
                      contentDescription = copy.copyAllBtn,
                      tint = ElectricCyan,
                    )
                  }
                  IconButton(
                    onClick = { onDeletePreset(preset.id) },
                    modifier = Modifier.testTag("delete_preset_${preset.id}"),
                  ) {
                    Icon(
                      imageVector = Icons.Filled.DeleteOutline,
                      contentDescription = "Delete",
                      tint = CrimsonAlert,
                    )
                  }
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                SavedStatChip("Gen ${preset.general}", BlazeOrange, Modifier.weight(1f))
                SavedStatChip("Red ${preset.redDot}", ElectricCyan, Modifier.weight(1f))
                SavedStatChip("2x ${preset.scope2x}", MoltenGold, Modifier.weight(1f))
                SavedStatChip("4x ${preset.scope4x}", HeadshotGreen, Modifier.weight(1f))
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = "Sniper ${preset.sniperScope} • FreeLook ${preset.freeLook} • Fire ${preset.fireButtonSize}%",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextMuted,
                )
                Button(
                  onClick = { onLoadPreset(preset) },
                  colors = ButtonDefaults.buttonColors(containerColor = BlazeOrange),
                  contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                  shape = MaterialTheme.shapes.small,
                  modifier = Modifier.testTag("load_preset_${preset.id}"),
                ) {
                  Text(copy.loadPresetBtn)
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SavedStatChip(
  text: String,
  color: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = ObsidianSurfaceVariant,
  ) {
    Box(
      modifier = Modifier.padding(vertical = 6.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
      )
    }
  }
}
