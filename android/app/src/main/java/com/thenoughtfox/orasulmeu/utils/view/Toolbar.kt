package com.thenoughtfox.orasulmeu.utils.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
    ConstraintLayout(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
    ) {
        val (backView, titleView, menuView) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.ic_chevron_left),
            contentDescription = "Left chevron",
            modifier = Modifier
                .constrainAs(backView) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
                .clickable { onBackClickListener?.invoke() }
                .size(24.dp)
        )

        Text(
            text = title,
            style = bodyModifier().copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier
                .constrainAs(titleView) {
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .width(230.dp)
                .padding(horizontal = 12.5.dp)
        )

        if (isMenu) {
            Image(
                painter = painterResource(id = iconMenu),
                contentDescription = "Menu icon",
                modifier = Modifier
                    .constrainAs(menuView) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
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