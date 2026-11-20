package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AimPrecision
import com.example.model.AppLanguage
import com.example.model.CalibrationConfig
import com.example.model.DeviceBrand
import com.example.model.FingerGrip
import com.example.model.FpsSetting
import com.example.model.GraphicsSetting
import com.example.model.PlayStyle
import com.example.model.RefreshRateOption
import com.example.model.SavedSensitivityPreset
import com.example.model.TouchResponseTier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ff_sensi_prefs")

data class StoredAppState(
  val selectedDeviceId: String?,
  val config: CalibrationConfig,
  val language: AppLanguage,
  val savedPresets: List<SavedSensitivityPreset>,
)

class PreferencesRepository(private val context: Context) {

  private object Keys {
    val SELECTED_DEVICE_ID = stringPreferencesKey("selected_device_id")
    val TOUCH_RESPONSE = stringPreferencesKey("touch_response")
    val REFRESH_RATE = stringPreferencesKey("refresh_rate")
    val GRAPHICS = stringPreferencesKey("graphics")
    val FPS = stringPreferencesKey("fps")
    val AIM_PRECISION = stringPreferencesKey("aim_precision")
    val PLAY_STYLE = stringPreferencesKey("play_style")
    val FINGER_GRIP = stringPreferencesKey("finger_grip")
    val SWIPE_VELOCITY = floatPreferencesKey("swipe_velocity")
    val LANGUAGE = stringPreferencesKey("language")
    val SAVED_PRESETS_JSON = stringPreferencesKey("saved_presets_json")
  }

  val stateFlow: Flow<StoredAppState> = context.dataStore.data.map { prefs ->
    val touch = prefs[Keys.TOUCH_RESPONSE]?.let { name ->
      TouchResponseTier.entries.firstOrNull { it.name == name }
    } ?: TouchResponseTier.BALANCED_240

    val hz = prefs[Keys.REFRESH_RATE]?.let { name ->
      RefreshRateOption.entries.firstOrNull { it.name == name }
    } ?: RefreshRateOption.HZ_120

    val graphics = prefs[Keys.GRAPHICS]?.let { name ->
      GraphicsSetting.entries.firstOrNull { it.name == name }
    } ?: GraphicsSetting.SMOOTH

    val fps = prefs[Keys.FPS]?.let { name ->
      FpsSetting.entries.firstOrNull { it.name == name }
    } ?: FpsSetting.HIGH

    val aim = prefs[Keys.AIM_PRECISION]?.let { name ->
      AimPrecision.entries.firstOrNull { it.name == name }
    } ?: AimPrecision.DEFAULT

    val style = prefs[Keys.PLAY_STYLE]?.let { name ->
      PlayStyle.entries.firstOrNull { it.name == name }
    } ?: PlayStyle.ONE_TAP

    val grip = prefs[Keys.FINGER_GRIP]?.let { name ->
      FingerGrip.entries.firstOrNull { it.name == name }
    } ?: FingerGrip.TWO_FINGER

    val velocity = prefs[Keys.SWIPE_VELOCITY]

    val lang = prefs[Keys.LANGUAGE]?.let { code ->
      AppLanguage.entries.firstOrNull { it.code == code }
    } ?: AppLanguage.TA

    val presets = decodePresets(prefs[Keys.SAVED_PRESETS_JSON])

    StoredAppState(
      selectedDeviceId = prefs[Keys.SELECTED_DEVICE_ID],
      config = CalibrationConfig(
        touchResponse = touch,
        refreshRate = hz,
        graphics = graphics,
        fps = fps,
        aimPrecision = aim,
        playStyle = style,
        fingerGrip = grip,
        measuredSwipeVelocityPxPerSec = velocity,
      ),
      language = lang,
      savedPresets = presets,
    )
  }

  suspend fun saveSelectedDeviceId(deviceId: String?) {
    context.dataStore.edit { prefs ->
      if (deviceId == null) {
        prefs.remove(Keys.SELECTED_DEVICE_ID)
      } else {
        prefs[Keys.SELECTED_DEVICE_ID] = deviceId
      }
    }
  }

  suspend fun saveCalibrationConfig(config: CalibrationConfig) {
    context.dataStore.edit { prefs ->
      prefs[Keys.TOUCH_RESPONSE] = config.touchResponse.name
      prefs[Keys.REFRESH_RATE] = config.refreshRate.name
      prefs[Keys.GRAPHICS] = config.graphics.name
      prefs[Keys.FPS] = config.fps.name
      prefs[Keys.AIM_PRECISION] = config.aimPrecision.name
      prefs[Keys.PLAY_STYLE] = config.playStyle.name
      prefs[Keys.FINGER_GRIP] = config.fingerGrip.name
      if (config.measuredSwipeVelocityPxPerSec != null) {
        prefs[Keys.SWIPE_VELOCITY] = config.measuredSwipeVelocityPxPerSec
      } else {
        prefs.remove(Keys.SWIPE_VELOCITY)
      }
    }
  }

  suspend fun saveLanguage(language: AppLanguage) {
    context.dataStore.edit { prefs ->
      prefs[Keys.LANGUAGE] = language.code
    }
  }

  suspend fun addPreset(preset: SavedSensitivityPreset) {
    context.dataStore.edit { prefs ->
      val current = decodePresets(prefs[Keys.SAVED_PRESETS_JSON]).toMutableList()
      current.add(0, preset)
      prefs[Keys.SAVED_PRESETS_JSON] = encodePresets(current.take(25))
    }
  }

  suspend fun deletePreset(presetId: String) {
    context.dataStore.edit { prefs ->
      val current = decodePresets(prefs[Keys.SAVED_PRESETS_JSON]).filterNot { it.id == presetId }
      prefs[Keys.SAVED_PRESETS_JSON] = encodePresets(current)
    }
  }

  private fun encodePresets(list: List<SavedSensitivityPreset>): String {
    val array = JSONArray()
    list.forEach { item ->
      val obj = JSONObject().apply {
        put("id", item.id)
        put("title", item.title)
        put("deviceId", item.deviceId)
        put("deviceName", item.deviceName)
        put("brand", item.brand.name)
        put("general", item.general)
        put("redDot", item.redDot)
        put("scope2x", item.scope2x)
        put("scope4x", item.scope4x)
        put("sniperScope", item.sniperScope)
        put("freeLook", item.freeLook)
        put("fireButtonSize", item.fireButtonSize)
        put("refreshHz", item.refreshHz)
        put("touchLabel", item.touchLabel)
        put("createdAtMillis", item.createdAtMillis)
      }
      array.put(obj)
    }
    return array.toString()
  }

  private fun decodePresets(raw: String?): List<SavedSensitivityPreset> {
    if (raw.isNullOrBlank()) return emptyList()
    return try {
      val array = JSONArray(raw)
      buildList {
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          val brandName = obj.optString("brand", DeviceBrand.SAMSUNG.name)
          val brand = DeviceBrand.entries.firstOrNull { it.name == brandName } ?: DeviceBrand.SAMSUNG
          add(
            SavedSensitivityPreset(
              id = obj.getString("id"),
              title = obj.getString("title"),
              deviceId = obj.getString("deviceId"),
              deviceName = obj.getString("deviceName"),
              brand = brand,
              general = obj.getInt("general"),
              redDot = obj.getInt("redDot"),
              scope2x = obj.getInt("scope2x"),
              scope4x = obj.getInt("scope4x"),
              sniperScope = obj.getInt("sniperScope"),
              freeLook = obj.getInt("freeLook"),
              fireButtonSize = obj.getInt("fireButtonSize"),
              refreshHz = obj.getInt("refreshHz"),
              touchLabel = obj.optString("touchLabel", "Balanced"),
              createdAtMillis = obj.optLong("createdAtMillis", 0L),
            ),
          )
        }
      }
    } catch (_: Exception) {
      emptyList()
    }
  }
}
