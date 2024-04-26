package com.thenoughtfox.orasulmeu.ui.profile.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun ProfileToolbar(onBackPressed: () -> Unit, onSettingsPressed: () -> Unit) = Row(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(
        painter = painterResource(R.drawable.ic_chevron_left),
        contentDescription = "return back icon",
        tint = colorResource(R.color.icons_dark_grey)
    )
    Text(
        text = "Profilul meu", modifier = Modifier.weight(1f), style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight(700),
            textAlign = TextAlign.Center,
            color = colorResource(R.color.black)
        )
    )
    Icon(
        painter = painterResource(R.drawable.ic_settings),
        contentDescription = "settings icon",
        tint = colorResource(R.color.icons_dark_grey)
    )
}

@Preview
@Composable
private fun Preview() = OrasulMeuTheme {
    ProfileToolbar({}, {})
}