package com.thenoughtfox.orasulmeu.ui.screens.create_post

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.NavEvent
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.State
import com.thenoughtfox.orasulmeu.ui.screens.create_post.media.RoundButton
import com.thenoughtfox.orasulmeu.ui.screens.create_post.media.UserImage
import com.thenoughtfox.orasulmeu.ui.screens.profile.components.ClickableIcon
import com.thenoughtfox.orasulmeu.ui.screens.profile.components.TopBar
import com.thenoughtfox.orasulmeu.ui.theme.bodyBoldModifier
import com.thenoughtfox.orasulmeu.ui.theme.bodyModifier
import com.thenoughtfox.orasulmeu.ui.theme.outlinedTextFieldModifier
import com.thenoughtfox.orasulmeu.ui.theme.pageModifier
import com.thenoughtfox.orasulmeu.ui.theme.subTitleModifier

@Composable
fun CreatePostPage(
    uiState: State,
    onSendEvent: (Event) -> Unit = {},
    sendNavEvent: (NavEvent) -> Unit = {}
) {

    val outState = rememberScrollState()

    Column(
        modifier = Modifier
            .pageModifier()
            .padding(horizontal = 16.dp)
            .verticalScroll(outState)
    ) {
        TopBar(
            titleText = stringResource(id = R.string.create_post_toolbar_title),
            leftItem = {
                ClickableIcon(
                    painter = painterResource(R.drawable.ic_chevron_left),
                    color = colorResource(R.color.icons_dark_grey),
                    onClick = { sendNavEvent(NavEvent.GoBack) }
                )
            }
        )

        //Title
        Text(
            text = stringResource(id = R.string.create_post_field_title),
            style = bodyBoldModifier(),
            modifier = Modifier.padding(top = 36.dp)
        )

        OutlinedTextField(
            value = uiState.title, onValueChange = { text ->
                onSendEvent(Event.SetTitle(text))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = bodyModifier(),
            modifier = Modifier
                .padding(top = 8.dp)
                .outlinedTextFieldModifier()
                .defaultMinSize(minHeight = 46.dp)
        )

        //Description
        Text(
            text = stringResource(id = R.string.create_post_field_desc),
            style = bodyBoldModifier(),
            modifier = Modifier.padding(top = 36.dp)
        )

        OutlinedTextField(
            value = uiState.description, onValueChange = { text ->
                onSendEvent(Event.SetDescription(text))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = bodyModifier(),
            modifier = Modifier
                .padding(top = 8.dp)
                .outlinedTextFieldModifier()
                .defaultMinSize(minHeight = 186.dp)
        )


        //Address
        Text(
            text = stringResource(id = R.string.create_post_field_address),
            style = bodyBoldModifier(),
            modifier = Modifier.padding(top = 36.dp)
        )

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.white))
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.border_color_grey),
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(shape = RoundedCornerShape(8.dp))
                .defaultMinSize(minHeight = 46.dp)
                .clickable {
                    sendNavEvent(NavEvent.GoToMapSearch)
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = "LocationPin",
                modifier = Modifier.padding(start = 12.dp)
            )

            Text(
                text = uiState.address,
                style = subTitleModifier(),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (uiState.images.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.create_post_field_media),
                style = bodyBoldModifier(),
                modifier = Modifier.padding(top = 36.dp)
            )

            MediaList(images = uiState.images, outState, onItemClick = {
                sendNavEvent(NavEvent.GoToMedia)
            })
        }

        Spacer(modifier = Modifier.weight(1f))

        val isTitleEmpty = uiState.title.isEmpty()
        RoundButton(modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.create_post_button_submit),
            backgroundColor = colorResource(id = if (isTitleEmpty) R.color.white else R.color.dark_blue),
            textColor = colorResource(id = if (isTitleEmpty) R.color.black else R.color.white),
            isLoading = uiState.isLoading,
            onClick = {
                if (!isTitleEmpty) {
                    onSendEvent(Event.Submit)
                }
            })
    }
}

@Composable
private fun MediaList(images: List<Uri>, outState: ScrollState, onItemClick: () -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .heightIn(max = 1000.dp)
            .nestedScroll(connection = object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    if (outState.canScrollForward && available.y < 0) {
                        val consumed = outState.dispatchRawDelta(-available.y)
                        return Offset(x = 0f, y = -consumed)
                    }
                    return Offset.Zero
                }
            })
    ) {
        items(images) { uri ->
            Box(
                modifier = Modifier.aspectRatio(1f),
            ) {
                UserImage(
                    image = uri,
                    modifier = Modifier
                        .size(106.dp)
                        .padding(vertical = 8.dp),
                    onClick = { onItemClick() }
                )
            }
        }

    }
}

@Preview
@Composable
private fun PreviewCreatePostPage() {
    CreatePostPage(State().copy(images = List(3) { Uri.EMPTY })) {}
}