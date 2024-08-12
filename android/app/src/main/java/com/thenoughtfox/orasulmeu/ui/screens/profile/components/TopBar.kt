package com.thenoughtfox.orasulmeu.ui.screens.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    titleText: String,
    leftItem: @Composable () -> Unit = {},
    rightItem: @Composable () -> Unit = {},
) = ConstraintLayout(
    modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
) {
    val (leftButton, title, rightButton) = createRefs()

    Box(modifier = Modifier.constrainAs(leftButton) {
        centerVerticallyTo(parent)
        start.linkTo(parent.start)
    }) { leftItem() }

    Text(
        text = titleText,
        modifier = Modifier.constrainAs(title) {
            centerTo(parent)
        },
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight(700),
            textAlign = TextAlign.Center,
            color = colorResource(R.color.black)
        )
    )

    Box(modifier = Modifier.constrainAs(rightButton) {
        centerVerticallyTo(parent)
        end.linkTo(parent.end)
    }) { rightItem() }
}

@Composable
fun ClickableIcon(
    painter: Painter, onClick: () -> Unit, color: Color, modifier: Modifier = Modifier
) {
    Icon(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        painter = painter,
        contentDescription = null,
        tint = color
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() = OrasulMeuTheme {
    TopBar(titleText = "Test", leftItem = {
        ClickableIcon(
            painter = painterResource(R.drawable.ic_chevron_left),
            color = colorResource(R.color.icons_dark_grey),
            onClick = { }
        )
    })
}