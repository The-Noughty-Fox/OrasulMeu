package com.thenoughtfox.orasulmeu.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalInspectionMode

/**
 * Used to understand if the composable function was called from preview (TRUE) or runtime (FALSE)
 */
val isPreview: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalInspectionMode.current