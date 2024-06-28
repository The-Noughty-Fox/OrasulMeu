package com.thenoughtfox.orasulmeu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalColors = staticCompositionLocalOf { OrasulMeuColor() }

object OrasulMeuTheme {
    val colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current
}

@Composable
fun OrasulMeuTheme(
    colors: OrasulMeuColor = OrasulMeuTheme.colors,
    content: @Composable () -> Unit
) = CompositionLocalProvider(LocalColors provides colors) {
    MaterialTheme {
        content()
    }
}