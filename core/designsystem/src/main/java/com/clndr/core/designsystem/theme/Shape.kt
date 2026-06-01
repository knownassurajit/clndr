package com.clndr.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Corner radii ported from the design tokens:
 *   --radius-sm 12 · --radius-md 20 · --radius-lg 28.
 * `extraSmall`/`small` cover chips and inner controls; `medium` is the card radius.
 */
val ClndrShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(30.dp),
)
