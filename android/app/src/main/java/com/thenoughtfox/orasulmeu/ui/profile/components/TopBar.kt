package com.thenoughtfox.orasulmeu.ui.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
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
    titleText: String,
    leftItem: @Composable () -> Unit = {},
    rightItem: @Composable () -> Unit = {},
) = ConstraintLayout(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
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
) = Box(modifier = modifier
    .size(32.dp)
    .clip(CircleShape)
    .clickable { onClick() }) {
    Icon(
        modifier = Modifier.align(Alignment.Center),
        painter = painter,
        contentDescription = null,
        tint = color
    )
}

@Preview
@Composable
private fun Preview() = OrasulMeuTheme {
    TopBar(titleText = "Test")
}