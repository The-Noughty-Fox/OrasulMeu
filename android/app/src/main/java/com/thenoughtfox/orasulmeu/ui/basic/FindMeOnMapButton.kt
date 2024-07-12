package com.thenoughtfox.orasulmeu.ui.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun FindMeOnMapButton(modifier: Modifier = Modifier, onPress: () -> Unit) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color = OrasulMeuTheme.colors.primary)
            .clickable { onPress() }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_locator),
            contentDescription = "find me button",
            modifier = Modifier.align(Alignment.Center),
            tint = OrasulMeuTheme.colors.backgroundWhite
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun Preview() = OrasulMeuTheme {
    FindMeOnMapButton {

    }
}