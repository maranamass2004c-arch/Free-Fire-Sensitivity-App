package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.DeviceCatalog
import com.example.model.AppLanguage
import com.example.model.DeviceBrand
import com.example.model.DeviceProfile
import com.example.ui.UiCopy
import com.example.ui.theme.AppleSilver
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.BlazeOrangeContainer
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HeadshotGreen
import com.example.ui.theme.IqooAmber
import com.example.ui.theme.MoltenGold
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.ObsidianOutline
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.OnePlusRed
import com.example.ui.theme.RealmeYellow
import com.example.ui.theme.SamsungBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VivoCyan
import com.example.ui.theme.XiaomiOrange
import kotlin.math.hypot
import kotlin.math.roundToInt

fun brandColor(brand: DeviceBrand): Color = when (brand) {
  DeviceBrand.SAMSUNG -> SamsungBlue
  DeviceBrand.VIVO -> VivoCyan
  DeviceBrand.IPHONE -> AppleSilver
  DeviceBrand.IQOO -> IqooAmber
  DeviceBrand.ONEPLUS -> OnePlusRed
  DeviceBrand.XIAOMI_POCO -> XiaomiOrange
  DeviceBrand.REALME -> RealmeYellow
}

@Composable
fun HeadshotReticleRing(
  score: Int,
  label: String,
  modifier: Modifier = Modifier,
) {
  val animatedSweep by animateFloatAsState(
    targetValue = (score.coerceIn(0, 100) / 100f) * 270f,
    animationSpec = tween(durationMillis = 600),
    label = "reticleSweep",
  )
  val animatedScore by animateIntAsState(
    targetValue = score,
    animationSpec = spring(),
    label = "reticleScore",
  )

  Box(
    modifier = modifier.size(104.dp),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.matchParentSize()) {
      val strokeWidth = 8.dp.toPx()
      val pad = strokeWidth / 2f
      val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

      // Track arc
      drawArc(
        color = ObsidianOutline,
        startAngle = 135f,
        sweepAngle = 270f,
        useCenter = false,
        topLeft = Offset(pad, pad),
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
      )

      // Progress arc
      drawArc(
        brush = Brush.sweepGradient(
          colors = listOf(BlazeOrange, MoltenGold, HeadshotGreen),
        ),
        startAngle = 135f,
        sweepAngle = animatedSweep,
        useCenter = false,
        topLeft = Offset(pad, pad),
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
      )

      // Subtle crosshair ticks inside
      val center = Offset(size.width / 2f, size.height / 2f)
      val tickInner = size.minDimension * 0.28f
      val tickOuter = size.minDimension * 0.34f
      listOf(
        Offset(center.x, center.y - tickOuter) to Offset(center.x, center.y - tickInner),
        Offset(center.x - tickOuter, center.y) to Offset(center.x - tickInner, center.y),
        Offset(center.x + tickInner, center.y) to Offset(center.x + tickOuter, center.y),
      ).forEach { (start, end) ->
        drawLine(
          color = ElectricCyan.copy(alpha = 0.45f),
          start = start,
          end = end,
          strokeWidth = 1.5.dp.toPx(),
          cap = StrokeCap.Round,
        )
      }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "$animatedScore%",
        style = MaterialTheme.typography.headlineMedium,
        color = HeadshotGreen,
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
fun SensitivityMetricCard(
  label: String,
  value: Int,
  baseValue: Int,
  accentColor: Color,
  modifier: Modifier = Modifier,
) {
  val animatedVal by animateIntAsState(
    targetValue = value,
    animationSpec = spring(),
    label = "sensiVal",
  )
  val animatedProgress by animateFloatAsState(
    targetValue = (value.coerceIn(0, 200) / 200f),
    animationSpec = tween(durationMillis = 450),
    label = "sensiBar",
  )
  val delta = value - baseValue

  Surface(
    modifier = modifier,
    shape = MaterialTheme.shapes.medium,
    color = ObsidianSurfaceVariant,
    border = BorderStroke(1.dp, ObsidianOutline),
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = label,
          style = MaterialTheme.typography.titleSmall,
          color = TextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f),
        )
        if (delta != 0) {
          val sign = if (delta > 0) "+$delta" else "$delta"
          val badgeColor = if (delta > 0) ElectricCyan else MoltenGold
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = badgeColor.copy(alpha = 0.14f),
          ) {
            Text(
              text = sign,
              style = MaterialTheme.typography.labelSmall,
              color = badgeColor,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
          }
        }
      }

      Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = animatedVal.toString(),
          style = MaterialTheme.typography.displayLarge,
          color = TextPrimary,
        )
        Text(
          text = "/ 200",
          style = MaterialTheme.typography.labelMedium,
          color = TextMuted,
          modifier = Modifier.padding(bottom = 5.dp),
        )
      }

      // 0..200 visual bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(7.dp)
          .clip(CircleShape)
          .background(ObsidianBgTrack),
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(animatedProgress)
            .height(7.dp)
            .clip(CircleShape)
            .background(
              Brush.horizontalGradient(
                listOf(accentColor.copy(alpha = 0.7f), accentColor),
              ),
            ),
        )
      }
    }
  }
}

private val ObsidianBgTrack = Color(0xFF0D1118)

@Composable
fun TouchDragCalibrationPad(
  copy: UiCopy,
  currentVelocity: Float?,
  onCalibrationComplete: (Float) -> Unit,
  modifier: Modifier = Modifier,
) {
  val trailPoints = remember { mutableStateListOf<Offset>() }
  val recordedVelocities = remember { mutableStateListOf<Float>() }
  var dragStartTime by remember { mutableStateOf(0L) }
  var dragStartOffset by remember { mutableStateOf(Offset.Zero) }
  var lastOffset by remember { mutableStateOf(Offset.Zero) }

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    color = ObsidianSurfaceVariant,
    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.35f)),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f),
        ) {
          Icon(
            imageVector = Icons.Filled.TouchApp,
            contentDescription = null,
            tint = ElectricCyan,
          )
          Text(
            text = copy.dragTestTitle,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
          )
        }
        TextButton(
          onClick = {
            recordedVelocities.clear()
            trailPoints.clear()
          },
          modifier = Modifier.testTag("reset_drag_test_button"),
        ) {
          Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Spacer(Modifier.width(4.dp))
          Text(copy.dragTestReset)
        }
      }

      Text(
        text = copy.dragTestDesc,
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
      )

      // Interactive Drag Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(170.dp)
          .clip(MaterialTheme.shapes.medium)
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF0A121C), Color(0xFF131A26)),
            ),
          )
          .border(1.dp, ObsidianOutline, MaterialTheme.shapes.medium)
          .testTag("drag_calibration_pad")
          .pointerInput(Unit) {
            detectDragGestures(
              onDragStart = { start ->
                trailPoints.clear()
                trailPoints.add(start)
                dragStartOffset = start
                lastOffset = start
                dragStartTime = System.currentTimeMillis()
              },
              onDrag = { change, _ ->
                change.consume()
                lastOffset = change.position
                trailPoints.add(change.position)
              },
              onDragEnd = {
                val elapsedMs = (System.currentTimeMillis() - dragStartTime).coerceAtLeast(35L)
                val distPx = hypot(
                  (lastOffset.x - dragStartOffset.x).toDouble(),
                  (lastOffset.y - dragStartOffset.y).toDouble(),
                ).toFloat()
                if (distPx > 30f && recordedVelocities.size < 3) {
                  val velocity = (distPx / elapsedMs) * 1000f
                  recordedVelocities.add(velocity)
                  if (recordedVelocities.size == 3) {
                    val avg = recordedVelocities.average().toFloat()
                    onCalibrationComplete(avg)
                  }
                }
              },
            )
          },
        contentAlignment = Alignment.Center,
      ) {
        Canvas(modifier = Modifier.matchParentSize()) {
          // Guide lines
          val midX = size.width / 2f
          drawLine(
            color = ObsidianOutline.copy(alpha = 0.5f),
            start = Offset(midX, size.height * 0.85f),
            end = Offset(midX, size.height * 0.15f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
          )
          // Target head ring at top
          drawCircle(
            color = BlazeOrange.copy(alpha = 0.45f),
            radius = 16.dp.toPx(),
            center = Offset(midX, size.height * 0.2f),
            style = Stroke(width = 2.dp.toPx()),
          )
          // Swipe trail
          if (trailPoints.size > 1) {
            val path = Path().apply {
              moveTo(trailPoints.first().x, trailPoints.first().y)
              for (i in 1 until trailPoints.size) {
                lineTo(trailPoints[i].x, trailPoints[i].y)
              }
            }
            drawPath(
              path = path,
              brush = Brush.verticalGradient(listOf(ElectricCyan, BlazeOrange)),
              style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round),
            )
          }
        }

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          val badgeText = when {
            recordedVelocities.size >= 3 -> String.format(
              copy.dragTestDone,
              recordedVelocities.average().roundToInt(),
            )
            currentVelocity != null && recordedVelocities.isEmpty() -> String.format(
              copy.dragTestDone,
              currentVelocity.roundToInt(),
            )
            else -> String.format(copy.dragTestProgress, recordedVelocities.size)
          }
          Surface(
            shape = CircleShape,
            color = ObsidianSurface.copy(alpha = 0.85f),
            border = BorderStroke(1.dp, ObsidianOutline),
          ) {
            Text(
              text = badgeText,
              style = MaterialTheme.typography.labelMedium,
              color = if (recordedVelocities.size >= 3) HeadshotGreen else ElectricCyan,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicePickerBottomSheet(
  copy: UiCopy,
  language: AppLanguage,
  activeDeviceId: String,
  selectedBrandFilter: DeviceBrand?,
  searchQuery: String,
  onBrandFilterChange: (DeviceBrand?) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onSelectDevice: (DeviceProfile) -> Unit,
  onDismiss: () -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val filteredDevices = remember(selectedBrandFilter, searchQuery) {
    DeviceCatalog.allDevices.filter { device ->
      val brandMatch = selectedBrandFilter == null || device.brand == selectedBrandFilter
      val q = searchQuery.trim()
      val queryMatch = q.isEmpty() ||
        device.marketingName.contains(q, ignoreCase = true) ||
        device.brand.displayName.contains(q, ignoreCase = true) ||
        device.modelCodes.any { it.contains(q, ignoreCase = true) }
      brandMatch && queryMatch
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = ObsidianSurface,
    contentColor = TextPrimary,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = copy.devicesTitle,
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimary,
        )
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = copy.cancel,
            tint = TextSecondary,
          )
        }
      }

      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("sheet_device_search"),
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
          selected = selectedBrandFilter == null,
          onClick = { onBrandFilterChange(null) },
          label = { Text(copy.allBrands) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BlazeOrangeContainer,
            selectedLabelColor = BlazeOrange,
          ),
        )
        DeviceBrand.entries.forEach { brand ->
          FilterChip(
            selected = selectedBrandFilter == brand,
            onClick = { onBrandFilterChange(brand) },
            label = {
              Text(if (language == AppLanguage.TA) brand.displayNameTa else brand.displayName)
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = brandColor(brand).copy(alpha = 0.2f),
              selectedLabelColor = brandColor(brand),
            ),
          )
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .height(380.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
      ) {
        items(filteredDevices, key = { it.id }) { device ->
          val isSelected = device.id == activeDeviceId
          val accent = brandColor(device.brand)
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(MaterialTheme.shapes.medium)
              .clickable { onSelectDevice(device) }
              .testTag("pick_device_${device.id}"),
            shape = MaterialTheme.shapes.medium,
            color = if (isSelected) ObsidianCardElevated else ObsidianSurfaceVariant,
            border = BorderStroke(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) BlazeOrange else ObsidianOutline,
            ),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                    text = "${device.nativeMaxHz}Hz • ${device.touchSamplingHz}Hz Touch • Gen ${device.baseGeneral}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                  )
                }
              }
              if (isSelected) {
                Icon(
                  imageVector = Icons.Filled.Check,
                  contentDescription = copy.activePhoneTag,
                  tint = BlazeOrange,
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
fun SavePresetDialog(
  copy: UiCopy,
  defaultName: String,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  var name by remember(defaultName) { mutableStateOf(defaultName) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = ObsidianSurface,
    titleContentColor = TextPrimary,
    textContentColor = TextSecondary,
    title = {
      Text(copy.saveDialogTitle, style = MaterialTheme.typography.titleLarge)
    },
    text = {
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("preset_name_input"),
        placeholder = { Text(copy.saveDialogHint) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BlazeOrange,
          unfocusedBorderColor = ObsidianOutline,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
        ),
      )
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(name) },
        colors = ButtonDefaults.buttonColors(containerColor = BlazeOrange),
        modifier = Modifier.testTag("confirm_save_preset"),
      ) {
        Text(copy.saveConfirm, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text(copy.cancel)
      }
    },
  )
}
