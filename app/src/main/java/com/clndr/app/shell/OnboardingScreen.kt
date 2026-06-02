package com.clndr.app.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clndr.core.designsystem.components.ClndrDatePickerDialog
import com.clndr.core.designsystem.theme.ClndrText
import com.clndr.core.designsystem.theme.clndr
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)

/**
 * First-run gate: clndr renders nothing until it knows when you arrived. Mirrors the web
 * onboarding — a short manifesto and a single date-of-birth field.
 */
@Composable
fun OnboardingScreen(
    onDone: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = MaterialTheme.clndr
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showPicker by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .background(palette.screen)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 30.dp, vertical = 30.dp),
    ) {
        Spacer(Modifier.weight(1f))

        Text(
            "C L N D R",
            style = ClndrText.eyebrow.copy(letterSpacing = 6.sp),
            color = palette.txtLow,
        )
        Text(
            "Your whole life,\non a single grid.",
            style = MaterialTheme.typography.headlineLarge,
            color = palette.txtHi,
            modifier = Modifier.padding(top = 18.dp, bottom = 12.dp),
        )
        Text(
            "clndr renders up to 110 years as quiet squares — the ones you've lived are " +
                "marked, the rest wait. To begin, tell it when you arrived.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.txtMid,
            modifier = Modifier.padding(bottom = 34.dp),
        )

        Text(
            "Date of birth",
            style = MaterialTheme.typography.labelMedium,
            color = palette.txtMid,
            modifier = Modifier.padding(bottom = 7.dp),
        )
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
                selectedDate?.format(DATE_FMT) ?: "Tap to choose",
                style = MaterialTheme.typography.bodyLarge,
                color = if (selectedDate != null) palette.txtHi else palette.txtLow,
            )
        }

        PrimaryButton(
            text = "See my life",
            enabled = selectedDate != null,
            onClick = { selectedDate?.let(onDone) },
            modifier = Modifier.padding(top = 10.dp),
        )

        Spacer(Modifier.weight(1f))
        Text(
            "Stored only on this device.",
            style = MaterialTheme.typography.labelSmall,
            color = palette.txtFaint,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (showPicker) {
        ClndrDatePickerDialog(
            initialDate = selectedDate ?: LocalDate.of(1995, 6, 15),
            onDismiss = { showPicker = false },
            onConfirm = { picked -> selectedDate = picked },
            yearRange = 1900..LocalDate.now().year,
        )
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = MaterialTheme.clndr
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) palette.nodeLived else palette.surface2)
            .let { if (enabled) it.clickable(onClick = onClick) else it }
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) palette.screen else palette.txtLow,
        )
    }
}
