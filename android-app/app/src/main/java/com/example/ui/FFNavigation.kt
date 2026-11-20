package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.SensitivityCalculator
import com.example.model.AppLanguage
import com.example.ui.components.DevicePickerBottomSheet
import com.example.ui.components.SavePresetDialog
import com.example.ui.screens.CalibrateScreen
import com.example.ui.screens.DevicesScreen
import com.example.ui.screens.LabScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.BlazeOrangeContainer
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianOutline
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

const val ROUTE_LAB = "lab"
const val ROUTE_CALIBRATE = "calibrate"
const val ROUTE_DEVICES = "devices"
const val ROUTE_SAVED = "saved"

private data class NavTab(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
)

@Composable
fun FFAppShell(
  viewModel: SensitivityViewModel = viewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val copy = AppStrings.get(state.language)
  val navController = rememberNavController()
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = backStackEntry?.destination?.route ?: ROUTE_LAB

  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()
  val clipboardManager = LocalClipboardManager.current

  val tabs = listOf(
    NavTab(ROUTE_LAB, copy.navLab, Icons.Filled.CenterFocusStrong, Icons.Outlined.CenterFocusStrong, "nav_lab"),
    NavTab(ROUTE_CALIBRATE, copy.navCalibrate, Icons.Filled.Tune, Icons.Outlined.Tune, "nav_calibrate"),
    NavTab(ROUTE_DEVICES, copy.navDevices, Icons.Filled.Devices, Icons.Outlined.Devices, "nav_devices"),
    NavTab(ROUTE_SAVED, copy.navSaved, Icons.Filled.Bookmarks, Icons.Outlined.Bookmarks, "nav_saved"),
  )

  fun navigateTo(route: String) {
    navController.navigate(route) {
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
      }
      launchSingleTop = true
      restoreState = true
    }
  }

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianBg),
  ) {
    val useNavRail = maxWidth >= 600.dp

    Scaffold(
      containerColor = ObsidianBg,
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
      topBar = {
        Surface(
          color = ObsidianSurface,
          border = BorderStroke(1.dp, ObsidianOutline.copy(alpha = 0.55f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .windowInsetsPadding(WindowInsets.statusBars)
              .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Surface(
                shape = CircleShape,
                color = BlazeOrangeContainer,
                border = BorderStroke(1.dp, BlazeOrange.copy(alpha = 0.6f)),
                modifier = Modifier.size(36.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Filled.CenterFocusStrong,
                    contentDescription = null,
                    tint = BlazeOrange,
                    modifier = Modifier.size(20.dp),
                  )
                }
              }
              Column {
                Text(
                  text = copy.appTitle,
                  style = MaterialTheme.typography.titleLarge,
                  color = TextPrimary,
                )
                Text(
                  text = copy.subtitleTag,
                  style = MaterialTheme.typography.labelSmall,
                  color = ElectricCyan,
                )
              }
            }

            // Instant Tamil / English Toggle
            Surface(
              onClick = viewModel::toggleLanguage,
              shape = CircleShape,
              color = ObsidianSurfaceVariant,
              border = BorderStroke(1.dp, ObsidianOutline),
              modifier = Modifier.testTag("language_toggle_button"),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.Translate,
                  contentDescription = "Switch Language",
                  tint = ElectricCyan,
                  modifier = Modifier.size(16.dp),
                )
                Text(
                  text = if (state.language == AppLanguage.TA) "தமிழ் • EN" else "EN • தமிழ்",
                  style = MaterialTheme.typography.labelMedium,
                  color = TextPrimary,
                  fontWeight = FontWeight.SemiBold,
                )
              }
            }
          }
        }
      },
      bottomBar = {
        if (!useNavRail) {
          NavigationBar(
            containerColor = ObsidianSurface,
            contentColor = TextPrimary,
            windowInsets = WindowInsets.navigationBars,
            tonalElevation = 0.dp,
          ) {
            tabs.forEach { tab ->
              val selected = currentRoute == tab.route
              NavigationBarItem(
                selected = selected,
                onClick = { navigateTo(tab.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.label,
                  )
                },
                label = {
                  Text(
                    text = tab.label,
                    maxLines = 1,
                  )
                },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = BlazeOrange,
                  selectedTextColor = BlazeOrange,
                  indicatorColor = BlazeOrangeContainer,
                  unselectedIconColor = TextSecondary,
                  unselectedTextColor = TextSecondary,
                ),
                modifier = Modifier.testTag(tab.testTag),
              )
            }
          }
        }
      },
    ) { innerPadding ->
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
      ) {
        if (useNavRail) {
          NavigationRail(
            containerColor = ObsidianSurface,
            contentColor = TextPrimary,
            modifier = Modifier
              .fillMaxHeight()
              .windowInsetsPadding(WindowInsets.navigationBars),
          ) {
            Spacer(Modifier.size(12.dp))
            tabs.forEach { tab ->
              val selected = currentRoute == tab.route
              NavigationRailItem(
                selected = selected,
                onClick = { navigateTo(tab.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.label,
                  )
                },
                label = { Text(tab.label, maxLines = 1) },
                colors = NavigationRailItemDefaults.colors(
                  selectedIconColor = BlazeOrange,
                  selectedTextColor = BlazeOrange,
                  indicatorColor = BlazeOrangeContainer,
                  unselectedIconColor = TextSecondary,
                  unselectedTextColor = TextSecondary,
                ),
                modifier = Modifier.testTag(tab.testTag),
              )
            }
          }
          Box(
            modifier = Modifier
              .width(1.dp)
              .fillMaxHeight()
              .background(ObsidianOutline.copy(alpha = 0.4f)),
          )
        }

        NavHost(
          navController = navController,
          startDestination = ROUTE_LAB,
          modifier = Modifier.weight(1f),
        ) {
          composable(ROUTE_LAB) {
            LabScreen(
              state = state,
              copy = copy,
              onOpenDevicePicker = { brand -> viewModel.openDevicePicker(brand) },
              onResetToAutoDetect = viewModel::resetToAutoDetectedDevice,
              onNavigateToCalibrate = { navigateTo(ROUTE_CALIBRATE) },
              onCopyAll = {
                val formatted = SensitivityCalculator.formatClipboardSummary(
                  state.calculated,
                  state.config,
                )
                clipboardManager.setText(AnnotatedString(formatted))
                coroutineScope.launch {
                  snackbarHostState.showSnackbar(copy.copiedToast)
                }
              },
              onOpenSaveDialog = viewModel::openSaveDialog,
            )
          }

          composable(ROUTE_CALIBRATE) {
            CalibrateScreen(
              state = state,
              copy = copy,
              onTouchResponseChange = viewModel::updateTouchResponse,
              onDragVelocityMeasured = viewModel::applyMeasuredDragVelocity,
              onRefreshRateChange = viewModel::updateRefreshRate,
              onGraphicsChange = viewModel::updateGraphics,
              onFpsChange = viewModel::updateFps,
              onAimPrecisionChange = viewModel::updateAimPrecision,
              onPlayStyleChange = viewModel::updatePlayStyle,
              onFingerGripChange = viewModel::updateFingerGrip,
            )
          }

          composable(ROUTE_DEVICES) {
            DevicesScreen(
              state = state,
              copy = copy,
              onBrandFilterChange = viewModel::setBrandFilter,
              onSearchQueryChange = viewModel::setSearchQuery,
              onSelectDevice = { device ->
                viewModel.selectDevice(device)
                navigateTo(ROUTE_LAB)
              },
            )
          }

          composable(ROUTE_SAVED) {
            SavedScreen(
              state = state,
              copy = copy,
              onLoadPreset = { preset ->
                viewModel.loadPreset(preset)
                navigateTo(ROUTE_LAB)
              },
              onCopyPreset = { preset ->
                val summary = buildString {
                  appendLine("🔥 ${preset.title}")
                  appendLine("📱 ${preset.deviceName} (${preset.refreshHz}Hz)")
                  appendLine("• General: ${preset.general} | Red Dot: ${preset.redDot}")
                  appendLine("• 2x: ${preset.scope2x} | 4x: ${preset.scope4x} | Sniper: ${preset.sniperScope}")
                  appendLine("• Free Look: ${preset.freeLook} | Fire Button: ${preset.fireButtonSize}%")
                }
                clipboardManager.setText(AnnotatedString(summary))
                coroutineScope.launch {
                  snackbarHostState.showSnackbar(copy.copiedToast)
                }
              },
              onDeletePreset = viewModel::deletePreset,
            )
          }
        }
      }
    }

    if (state.showDevicePickerSheet) {
      DevicePickerBottomSheet(
        copy = copy,
        language = state.language,
        activeDeviceId = state.activeDevice.id,
        selectedBrandFilter = state.selectedBrandFilter,
        searchQuery = state.searchQuery,
        onBrandFilterChange = viewModel::setBrandFilter,
        onSearchQueryChange = viewModel::setSearchQuery,
        onSelectDevice = viewModel::selectDevice,
        onDismiss = viewModel::closeDevicePicker,
      )
    }

    if (state.showSaveDialog) {
      SavePresetDialog(
        copy = copy,
        defaultName = "${state.activeDevice.marketingName} (${state.config.refreshRate.label})",
        onConfirm = viewModel::saveCurrentPreset,
        onDismiss = viewModel::closeSaveDialog,
      )
    }
  }
}
