package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val TacticalEsportsColorScheme = darkColorScheme(
  primary = BlazeOrange,
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = BlazeOrangeContainer,
  onPrimaryContainer = BlazeOrangeGlow,
  secondary = ElectricCyan,
  onSecondary = Color(0xFF002026),
  secondaryContainer = ElectricCyanContainer,
  onSecondaryContainer = ElectricCyan,
  tertiary = MoltenGold,
  onTertiary = Color(0xFF261A00),
  tertiaryContainer = MoltenGoldContainer,
  onTertiaryContainer = MoltenGold,
  background = ObsidianBg,
  onBackground = TextPrimary,
  surface = ObsidianSurface,
  onSurface = TextPrimary,
  surfaceVariant = ObsidianSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  surfaceContainerLow = ObsidianSurface,
  surfaceContainer = ObsidianSurfaceVariant,
  surfaceContainerHigh = ObsidianCardElevated,
  outline = ObsidianOutline,
  outlineVariant = Color(0xFF222B3B),
  error = CrimsonAlert,
  onError = Color.White,
  errorContainer = CrimsonAlertContainer,
  onErrorContainer = CrimsonAlert,
)

val TacticalShapes = Shapes(
  small = RoundedCornerShape(10.dp),
  medium = RoundedCornerShape(16.dp),
  large = RoundedCornerShape(22.dp),
  extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun FFSensiTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = TacticalEsportsColorScheme,
    typography = AppTypography,
    shapes = TacticalShapes,
    content = content,
  )
}
