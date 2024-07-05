package com.thenoughtfox.orasulmeu.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thenoughtfox.orasulmeu.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

fun bodyModifier() = TextStyle(
    fontSize = 16.sp,
    color = Color.Black,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

fun bodyBoldModifier() = bodyModifier().copy(fontWeight = FontWeight.Bold)

fun Modifier.outlinedTextFieldModifier(): Modifier = composed {
    this then Modifier
        .fillMaxWidth()
        .background(color = colorResource(id = R.color.white))
        .border(
            width = 1.dp,
            color = colorResource(id = R.color.border_color_grey),
            shape = RoundedCornerShape(8.dp)
        )
}

fun subTitleModifier() = TextStyle(
    fontSize = 14.sp,
    color = Color.Black,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

fun headlineModified() = TextStyle(
    fontSize = 28.sp,
    color = Color.Black,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

fun Modifier.pageModifier(): Modifier = composed {
    this then Modifier
        .background(color = colorResource(id = R.color.background_color))
        .fillMaxSize()
        .statusBarsPadding()
}
