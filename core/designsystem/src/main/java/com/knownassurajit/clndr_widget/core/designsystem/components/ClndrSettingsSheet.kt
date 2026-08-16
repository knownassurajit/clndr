package com.knownassurajit.clndr_widget.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.knownassurajit.clndr_widget.core.designsystem.theme.ThemeMode
import com.knownassurajit.clndr_widget.core.designsystem.theme.clndr
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ClndrSettingsState(
    val birthDate: LocalDate?,
    val themeMode: ThemeMode,
    val sunriseAutoEnabled: Boolean,
    val widgetsFollowAppTheme: Boolean = true,
)

enum class ClndrPinTarget { YearProgress, YearCalendar, Life, Goals }

private val DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)
private val THEME_OPTIONS = listOf("Light", "Dark", "System")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClndrSettingsSheet(
    state: ClndrSettingsState,
    onDismiss: () -> Unit,
    onSetBirthDate: (LocalDate) -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onToggleSunriseAuto: (Boolean) -> Unit,
    onToggleWidgetsFollowApp: (Boolean) -> Unit,
    onPinWidget: (ClndrPinTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = MaterialTheme.clndr
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.screen,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(bottom = 30.dp),
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineSmall, color = palette.txtHi)

            SubHead("Identity", Modifier.padding(top = 18.dp, bottom = 10.dp))
            FieldLabel("Date of birth")
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.line, RoundedCornerShape(12.dp))
                    .clickable { showPicker = true }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
            ) {
                Text(
                    state.birthDate?.format(DATE_FMT) ?: "Not set",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (state.birthDate != null) palette.txtHi else palette.txtLow,
                )
            }

            SubHead("Appearance", Modifier.padding(top = 18.dp, bottom = 10.dp))
            SegmentedToggle(
                options = THEME_OPTIONS,
                selectedIndex = themeIndex(state.themeMode),
                onSelect = { index -> onSetThemeMode(themeFromIndex(index)) },
            )
            SettingRow(
                title = "Twilight engine",
                sub = if (state.sunriseAutoEnabled) {
                    "On — overrides Light / Dark / System at local sunrise and sunset"
                } else {
                    "Switches theme at local sunrise and sunset"
                },
            ) {
                ThemedSwitch(checked = state.sunriseAutoEnabled, onChange = onToggleSunriseAuto)
            }

            SubHead("Home screen", Modifier.padding(top = 14.dp, bottom = 10.dp))
            SettingRow(
                title = "Widgets follow app theme",
                sub = if (state.widgetsFollowAppTheme) {
                    "Light, Dark, System, and Twilight apply to widgets"
                } else {
                    "Widgets follow the system light / dark setting"
                },
            ) {
                ThemedSwitch(checked = state.widgetsFollowAppTheme, onChange = onToggleWidgetsFollowApp)
            }
            GhostButton(text = "Add Year Progress widget", onClick = { onPinWidget(ClndrPinTarget.YearProgress) })
            GhostButton(text = "Add Year Calendar widget", onClick = { onPinWidget(ClndrPinTarget.YearCalendar) })
            GhostButton(text = "Add Life widget", onClick = { onPinWidget(ClndrPinTarget.Life) })
            GhostButton(text = "Add Goals widget", onClick = { onPinWidget(ClndrPinTarget.Goals) })
        }
    }

    if (showPicker) {
        ClndrDatePickerDialog(
            initialDate = state.birthDate,
            onDismiss = { showPicker = false },
            onConfirm = onSetBirthDate,
            yearRange = 1900..LocalDate.now().year,
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.clndr.txtMid,
        modifier = Modifier.padding(bottom = 7.dp)
    )
}

@Composable
private fun SettingRow(
    title: String,
    sub: String?,
    divider: Boolean = true,
    trailing: @Composable () -> Unit,
) {
    val palette = MaterialTheme.clndr
    Row(
        Modifier.fillMaxWidth().padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = palette.txtHi)
            if (sub != null) Text(sub, style = MaterialTheme.typography.bodySmall, color = palette.txtLow)
        }
        trailing()
    }
    if (divider) Box(Modifier.fillMaxWidth().height(1.dp).background(palette.line))
}

@Composable
private fun GhostButton(text: String, onClick: () -> Unit) {
    val palette = MaterialTheme.clndr
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, palette.lineStrong, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp)
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = palette.txtHi)
    }
}

@Composable
private fun ThemedSwitch(checked: Boolean, onChange: (Boolean) -> Unit) {
    val palette = MaterialTheme.clndr
    Switch(
        checked = checked,
        onCheckedChange = onChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = palette.screen,
            checkedTrackColor = palette.nodeLived,
            uncheckedThumbColor = palette.screen,
            uncheckedTrackColor = palette.nodeFuture,
            uncheckedBorderColor = palette.nodeFuture,
        ),
    )
}

private fun themeIndex(mode: ThemeMode): Int = when (mode) {
    ThemeMode.FORCE_LIGHT -> 0
    ThemeMode.FORCE_DARK -> 1
    ThemeMode.FOLLOW_SYSTEM, ThemeMode.SUNRISE_AUTO -> 2
}

private fun themeFromIndex(index: Int): ThemeMode = when (index) {
    0 -> ThemeMode.FORCE_LIGHT
    1 -> ThemeMode.FORCE_DARK
    else -> ThemeMode.FOLLOW_SYSTEM
}
