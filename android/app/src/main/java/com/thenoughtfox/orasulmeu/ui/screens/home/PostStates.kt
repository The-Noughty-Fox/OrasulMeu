package com.thenoughtfox.orasulmeu.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun PostLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(128.dp)
                .align(Alignment.Center),
            color = colorResource(R.color.primary),
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun PostError(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.search_posts_no_results_message),
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                fontWeight = FontWeight(400),
                fontSize = 20.sp,
                color = OrasulMeuTheme.colors.onBackground
            )
        )
    }
}