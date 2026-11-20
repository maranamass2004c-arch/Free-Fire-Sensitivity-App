package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.DeviceCatalog
import com.example.model.AppLanguage
import com.example.model.DeviceBrand
import com.example.model.DeviceProfile
import com.example.ui.SensiUiState
import com.example.ui.UiCopy
import com.example.ui.components.brandColor
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.BlazeOrangeContainer
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MoltenGold
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.ObsidianOutline
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DevicesScreen(
  state: SensiUiState,
  copy: UiCopy,
  onBrandFilterChange: (DeviceBrand?) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onSelectDevice: (DeviceProfile) -> Unit,
  modifier: Modifier = Modifier,
) {
  val isTa = state.language == AppLanguage.TA
  val filteredDevices = remember(state.selectedBrandFilter, state.searchQuery) {
    DeviceCatalog.allDevices.filter { device ->
      val brandMatch = state.selectedBrandFilter == null || device.brand == state.selectedBrandFilter
      val q = state.searchQuery.trim()
      val queryMatch = q.isEmpty() ||
        device.marketingName.contains(q, ignoreCase = true) ||
        device.brand.displayName.contains(q, ignoreCase = true) ||
        device.modelCodes.any { it.contains(q, ignoreCase = true) }
      brandMatch && queryMatch
    }
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 720.dp)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Spacer(Modifier.height(4.dp))
      Text(
        text = copy.devicesTitle,
        style = MaterialTheme.typography.titleLarge,
        color = TextPrimary,
      )

      OutlinedTextField(
        value = state.searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("directory_search_input"),
        placeholder = { Text(copy.searchHint, color = TextMuted) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BlazeOrange,
          unfocusedBorderColor = ObsidianOutline,
          focusedContainerColor = ObsidianSurfaceVariant,
          unfocusedContainerColor = ObsidianSurfaceVariant,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
        ),
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        FilterChip(
          selected = state.selectedBrandFilter == null,
          onClick = { onBrandFilterChange(null) },
          label = { Text(copy.allBrands) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BlazeOrangeContainer,
            selectedLabelColor = BlazeOrange,
          ),
          modifier = Modifier.testTag("filter_brand_all"),
        )
        DeviceBrand.entries.forEach { brand ->
          val accent = brandColor(brand)
          FilterChip(
            selected = state.selectedBrandFilter == brand,
            onClick = { onBrandFilterChange(brand) },
            label = { Text(if (isTa) brand.displayNameTa else brand.displayName) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = accent.copy(alpha = 0.2f),
              selectedLabelColor = accent,
            ),
            modifier = Modifier.testTag("filter_brand_${brand.name.lowercase()}"),
          )
        }
      }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
      ) {
        items(filteredDevices, key = { it.id }) { device ->
          val isActive = device.id == state.activeDevice.id
          val accent = brandColor(device.brand)

          Surface(
            shape = MaterialTheme.shapes.large,
            color = if (isActive) ObsidianCardElevated else ObsidianSurface,
            border = BorderStroke(
              width = if (isActive) 1.5.dp else 1.dp,
              color = if (isActive) BlazeOrange else ObsidianOutline,
            ),
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
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  modifier = Modifier.weight(1f),
                ) {
                  Surface(
                    shape = CircleShape,
                    color = accent.copy(alpha = 0.16f),
                    modifier = Modifier.size(40.dp),
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Icon(
                        imageVector = if (device.isIos) Icons.Filled.PhoneIphone else Icons.Filled.PhoneAndroid,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp),
                      )
                    }
                  }

                  Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                      text = device.marketingName,
                      style = MaterialTheme.typography.titleMedium,
                      color = TextPrimary,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                      text = "${device.screenInches}\" • ${device.nativeMaxHz}Hz • ${device.touchSamplingHz}Hz Touch • ${device.chipsetTier}",
                      style = MaterialTheme.typography.bodySmall,
                      color = TextSecondary,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                  }
                }

                if (isActive) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BlazeOrangeContainer,
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                      Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = BlazeOrange,
                        modifier = Modifier.size(14.dp),
                      )
                      Text(
                        text = copy.activePhoneTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = BlazeOrange,
                      )
                    }
                  }
                } else {
                  Button(
                    onClick = { onSelectDevice(device) },
                    colors = ButtonDefaults.buttonColors(containerColor = BlazeOrange),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.testTag("select_catalog_${device.id}"),
                  ) {
                    Text(copy.applyPhoneBtn, style = MaterialTheme.typography.labelLarge)
                  }
                }
              }

              // Preview of this model's distinct baseline values
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                MiniSpecChip("Gen ${device.baseGeneral}", BlazeOrange, Modifier.weight(1f))
                MiniSpecChip("Red ${device.baseRedDot}", ElectricCyan, Modifier.weight(1f))
                MiniSpecChip("2x ${device.base2xScope}", MoltenGold, Modifier.weight(1f))
                MiniSpecChip(
                  if (device.isIos) "Fire ${device.baseFireButton}%" else "DPI ${device.recommendedDpi}",
                  accent,
                  Modifier.weight(1f),
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MiniSpecChip(
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
      modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        maxLines = 1,
      )
    }
  }
}
