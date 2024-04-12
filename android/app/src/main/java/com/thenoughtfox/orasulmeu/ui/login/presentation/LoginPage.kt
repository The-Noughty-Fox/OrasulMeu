package com.thenoughtfox.orasulmeu.ui.login.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.bodyModifier
import com.thenoughtfox.orasulmeu.ui.theme.headlineModified
import com.thenoughtfox.orasulmeu.ui.theme.pageModifier
import com.thenoughtfox.orasulmeu.ui.theme.subTitleModifier
import com.thenoughtfox.orasulmeu.utils.view.CircleProgress

@Composable
fun LoginPage(uiState: State, onSendEvent: (Event) -> Unit) {

    Column(modifier = Modifier.pageModifier()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                modifier = Modifier.size(80.dp),
                contentDescription = "Logo"
            )
            Text(
                style = headlineModified(),
                text = stringResource(id = R.string.sign_in_title),
                modifier = Modifier.padding(top = 24.dp)
            )

            Text(
                text = stringResource(id = R.string.sign_in_to_start),
                style = subTitleModifier(),
                modifier = Modifier.padding(top = 8.dp)
            )

            SignInButton(
                modifier = Modifier.padding(top = 41.dp),
                isLoading = uiState.isLoadingGoogle,
                type = SingInType.Google,
                onClick = {
                    onSendEvent(Event.Auth(it))
                }
            )

            SignInButton(
                modifier = Modifier.padding(top = 25.dp),
                isLoading = uiState.isLoadingFacebook,
                type = SingInType.Facebook,
                onClick = {
                    onSendEvent(Event.Auth(it))
                }
            )
        }

        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Skip",
                style = bodyModifier().copy(color = colorResource(id = R.color.grey)),
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(modifier = Modifier.padding(top = 26.dp, bottom = 16.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_company_logo),
                    contentDescription = "Company Logo",
                    modifier = Modifier.padding(end = 8.dp)
                )

                Text(
                    text = stringResource(id = R.string.built_by_the_noughty_fox),
                    style = subTitleModifier().copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun SignInButton(
    modifier: Modifier = Modifier,
    type: SingInType,
    isLoading: Boolean = false,
    onClick: (SingInType) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(48.dp),
        color = colorResource(id = type.backgroundColor),
        modifier = modifier
            .width(340.dp)
            .height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.clickable { onClick.invoke(type) }
        ) {
            if (!isLoading) {
                Image(
                    painter = painterResource(id = R.drawable.ic_dot),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(colorResource(id = type.imageColor)),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(id = type.text),
                    fontSize = 16.sp,
                    color = colorResource(id = type.textColor)
                )
            } else {
                CircleProgress(
                    modifier = Modifier.size(30.dp),
                    color = colorResource(id = type.textColor)
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginPagePreview() {
    LoginPage(State()) {}
}