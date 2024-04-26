package com.thenoughtfox.orasulmeu.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun ProfileView(
    name: String, avatarImageUrl: String?, postCount: Int, reactionsCount: Int
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        // todo replace with image
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(120.dp)
                .zIndex(1f)
                .background(color = Color.Black, CircleShape)
        )

        Column(
            modifier = Modifier
                .zIndex(0f)
                .padding(top = 36.dp)
                .heightIn(min = 120.dp)
                .background(color = Color(0xFFDCE2E9), shape = RoundedCornerShape(18.dp))
                .fillMaxWidth(),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = name,
                style = TextStyle(
                    fontSize = 24.sp,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight(700)
                )
            )
            Text(
                text = "Atinge pentru a edita", style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = colorResource(R.color.grey)
                )
            )

            Row(
                modifier = Modifier
                    .padding(
                        top = 36.dp,
                        bottom = 24.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProfileInfoItem(title = "Reclamații", value = postCount.toString())
                VerticalDivider(thickness = 1.dp, color = colorResource(R.color.black))
                ProfileInfoItem(title = "Reactii", value = reactionsCount.toString())
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(title: String, value: String) =
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value, style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight(700),
                color = colorResource(R.color.black)
            )
        )
        Text(
            text = title, style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                color = colorResource(R.color.black)
            )
        )
    }

@Preview(showBackground = true)
@Composable
private fun Preview() = OrasulMeuTheme {
    ProfileView(name = "John Doe", avatarImageUrl = "test", postCount = 42, reactionsCount = 112)
}