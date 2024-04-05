package com.thenoughtfox.orasulmeu.utils.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.bodyModifier

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String,
    isMenu: Boolean = false,
    iconMenu: Int = R.drawable.ic_menu,
    onBackClickListener: (() -> Unit)? = null,
    onMenuClickListener: (() -> Unit)? = null
) {
    Row(
        modifier
            .height(48.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.clickable { onBackClickListener?.invoke() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Left chevron",
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = stringResource(id = R.string.toolbar_back),
                style = bodyModifier().copy(textAlign = TextAlign.Center),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = title,
            style = bodyModifier().copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier
                .width(230.dp)
                .padding(horizontal = 12.5.dp)
        )

        if (isMenu) {
            Spacer(Modifier.weight(1f))

            Image(
                painter = painterResource(id = iconMenu),
                contentDescription = "Menu icon",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onMenuClickListener?.invoke() },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewToolbar() {
    Toolbar(title = stringResource(id = R.string.toolbar_back))
}