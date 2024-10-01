package com.thenoughtfox.orasulmeu.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.thenoughtfox.orasulmeu.R

@Composable
fun AnonymousLoginDialog(
    onDismissRequest: () -> Unit = {},
    onConfirmation: () -> Unit
) {
    Dialog(onDismissRequest = {
        onDismissRequest.invoke()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors().copy(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Image(
                    painter = painterResource(R.drawable.image_need_to_auth),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )

                Text(
                    text = stringResource(R.string.auth_screen_title),
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 24.dp)
                )

                Text(
                    text = stringResource(R.string.auth_screen_desc),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(400),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(16.dp),
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colorResource(R.color.tufts_blue),
                            shape = RoundedCornerShape(100.dp)
                        )
                        .clip(RoundedCornerShape(100.dp))
                        .clickable {
                            onConfirmation()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.auth_button),
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight(500),
                            textAlign = TextAlign.Center
                        ),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AnonymousLoginDialogPreview() {
    AnonymousLoginDialog({}, {})
}