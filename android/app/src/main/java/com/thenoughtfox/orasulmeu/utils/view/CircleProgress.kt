package com.thenoughtfox.orasulmeu.utils.view

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.thenoughtfox.orasulmeu.R

@Composable
fun CircleProgress(
    modifier: Modifier = Modifier,
    color: Color = colorResource(id = R.color.black)
) {
    CircularProgressIndicator(
        color = color,
        modifier = modifier,
        strokeWidth = 3.dp
    )
}