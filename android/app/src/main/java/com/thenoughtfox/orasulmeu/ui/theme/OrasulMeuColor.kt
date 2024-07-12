package com.thenoughtfox.orasulmeu.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.thenoughtfox.orasulmeu.R

class OrasulMeuColor {
    val primary: Color
        @Composable
        @ReadOnlyComposable
        get() = colorResource(R.color.indigo_600)

    val onBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = colorResource(R.color.black)

    val buttonNextBackground: Color
        get() = Color(0xFFC7D2FE)

    val backgroundWhite: Color
        get() = Color.White
}