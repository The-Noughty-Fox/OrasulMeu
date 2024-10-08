package com.thenoughtfox.orasulmeu.ui.screens.create_post.media

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.NavEvent
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.State
import com.thenoughtfox.orasulmeu.ui.screens.create_post.Image
import com.thenoughtfox.orasulmeu.ui.screens.profile.components.ClickableIcon
import com.thenoughtfox.orasulmeu.ui.screens.profile.components.TopBar
import com.thenoughtfox.orasulmeu.ui.theme.bodyBoldModifier
import com.thenoughtfox.orasulmeu.ui.theme.pageModifier
import com.thenoughtfox.orasulmeu.ui.theme.subTitleModifier
import com.thenoughtfox.orasulmeu.utils.view.Alert
import com.thenoughtfox.orasulmeu.utils.view.CircleProgress

@Composable
fun CreatePostMediaPage(
    uiState: State,
    onSendEvent: (Event) -> Unit,
    onGalleryClick: () -> Unit = {},
    sendNavEvent: (NavEvent) -> Unit = {}
) {

    if (uiState.removedImage != null) {
        Alert(
            onDismissRequest = {
                onSendEvent(Event.DismissAlert)
            },
            onConfirmation = {
                onSendEvent(Event.RemoveImage(uiState.removedImage))
            },
            dialogTitle = stringResource(id = R.string.create_post_remove_image_alert_title),
            dialogText = stringResource(id = R.string.create_post_remove_image_alert_desc),
            confirmText = stringResource(id = R.string.create_post_remove_image_alert_confirm),
            dismissText = stringResource(id = R.string.create_post_remove_image_alert_dismiss)
        )
    }

    Column(
        modifier = Modifier
            .pageModifier()
            .verticalScroll(rememberScrollState())
    ) {
        TopBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            titleText = stringResource(id = R.string.create_post_toolbar_title),
            leftItem = {
                ClickableIcon(
                    painter = painterResource(R.drawable.ic_chevron_left),
                    color = colorResource(R.color.icons_dark_grey),
                    onClick = { sendNavEvent(NavEvent.GoBack) }
                )
            }
        )

        val image = uiState.image?.parsedImage ?: R.drawable.image_placeholder
        AsyncImage(
            model = image,
            placeholder = painterResource(id = R.drawable.image_placeholder),
            contentDescription = "Image",
            modifier = Modifier
                .padding(top = 36.dp)
                .height(390.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )

        if (uiState.images.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
            ) {
                uiState.images.forEach { image ->
                    UserImage(
                        modifier = Modifier
                            .size(106.dp)
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        image = image,
                        color = if (uiState.image == image) {
                            colorResource(id = R.color.yellow)
                        } else {
                            Color.White
                        },
                        onClick = {
                            onSendEvent(Event.SelectImage(it))
                        },
                        onRemove = {
                            onSendEvent(Event.ShowAlert(it))
                        })
                }
            }
        }

        UploadButtons(
            onClickMedia = onGalleryClick,
            onClickCamera = { sendNavEvent(NavEvent.Camera) }
        )

        Spacer(modifier = Modifier.weight(1f))

        val isValid = uiState.images.isNotEmpty()
        RoundButton(modifier = Modifier
            .padding(horizontal = 16.dp)
            .align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.create_post_button_next),
            backgroundColor = colorResource(id = if (isValid) R.color.dark_blue else R.color.white),
            textColor = colorResource(id = if (isValid) R.color.white else R.color.black),
            isLoading = uiState.isLoading,
            onClick = {
                if (isValid) {
                    sendNavEvent(NavEvent.CreatePost)
                }
            })
    }
}

@Composable
private fun UploadButtons(onClickMedia: () -> Unit, onClickCamera: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, colorResource(id = R.color.border_color_grey)),
        color = Color.White,
        modifier = Modifier
            .padding(16.dp)
            .height(74.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .clickable { onClickMedia() }) {
                UploadButton(
                    image = painterResource(id = R.drawable.ic_media),
                    text = stringResource(id = R.string.create_post_button_upload_media),
                )
            }

            Spacer(
                modifier = Modifier
                    .background(colorResource(id = R.color.border_color_grey))
                    .width(1.dp)
                    .fillMaxHeight()
            )

            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .clickable { onClickCamera() }) {
                UploadButton(
                    image = painterResource(id = R.drawable.ic_camera),
                    text = stringResource(id = R.string.create_post_button_upload_open_camera),
                )
            }
        }
    }
}

@Composable
private fun UploadButton(image: Painter, text: String) {
    Column(
        modifier = Modifier
            .width(176.dp)
            .height(44.dp)
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = image,
            contentDescription = "Image",
        )

        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = text,
            style = subTitleModifier().copy(
                fontSize = 12.sp,
                color = colorResource(id = R.color.grey)
            )
        )
    }
}

@Composable
fun UserImage(
    image: Image,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    onClick: ((Image) -> Unit)? = null,
    onRemove: ((Image) -> Unit)? = null
) {
    Box {
        AsyncImage(
            model = image.parsedImage,
            contentDescription = "User images",
            modifier = modifier
                .clip(shape = RoundedCornerShape(8.dp))
                .clickable { onClick?.invoke(image) }
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(8.dp),
                    color = color
                )
                .background(color = Color.White)
        )

        if (image.shouldBeRemoved) {
            onRemove?.let { callback ->
                Image(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(start = 32.dp)
                        .clickable { callback(image) },
                    painter = painterResource(id = R.drawable.ic_remove),
                    contentDescription = "RemoveIcon",
                )
            }
        }
    }
}

@Composable
fun RoundButton(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = colorResource(id = R.color.white),
    textColor: Color = colorResource(id = R.color.black),
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(48.dp),
        color = backgroundColor,
        modifier = modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(48.dp))
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircleProgress(
                    modifier = Modifier.size(30.dp),
                    color = textColor
                )
            } else {
                Text(
                    text = text,
                    style = bodyBoldModifier().copy(color = textColor)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCreatePostMediaPage() {
    CreatePostMediaPage(State(), onSendEvent = {})
}