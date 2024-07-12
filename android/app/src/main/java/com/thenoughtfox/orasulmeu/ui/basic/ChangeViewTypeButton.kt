package com.thenoughtfox.orasulmeu.ui.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.screens.home.SelectedViewType
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun ChangeViewTypeButton(
    modifier: Modifier = Modifier, viewType: SelectedViewType, onClick: () -> Unit
) {
    Box(modifier = modifier
        .clip(CircleShape)
        .clickable { onClick() }
        .background(color = OrasulMeuTheme.colors.primary)
        .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    when (viewType) {
                        SelectedViewType.Map -> R.drawable.ic_list
                        SelectedViewType.List -> R.drawable.ic_menu_map
                    }
                ),
                contentDescription = null,
                tint = OrasulMeuTheme.colors.backgroundWhite,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = when (viewType) {
                    SelectedViewType.Map -> stringResource(R.string.home_view_type_list)
                    SelectedViewType.List -> stringResource(R.string.home_view_type_map)
                }, style = TextStyle(
                    color = OrasulMeuTheme.colors.backgroundWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400)
                )
            )
        }
    }
}

@Preview
@Composable
private fun Preview() = OrasulMeuTheme {
    var viewType: SelectedViewType by remember { mutableStateOf(SelectedViewType.Map) }
    ChangeViewTypeButton(viewType = viewType, onClick = {
        viewType =
            if (viewType == SelectedViewType.Map) SelectedViewType.List else SelectedViewType.Map
    })
}