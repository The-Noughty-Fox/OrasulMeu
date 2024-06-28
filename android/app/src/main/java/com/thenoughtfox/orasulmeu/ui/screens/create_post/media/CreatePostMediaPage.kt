package com.thenoughtfox.orasulmeu.ui.screens.create_post.media

import android.net.Uri
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
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.State
import com.thenoughtfox.orasulmeu.ui.theme.bodyBoldModifier
import com.thenoughtfox.orasulmeu.ui.theme.pageModifier
import com.thenoughtfox.orasulmeu.ui.theme.subTitleModifier
import com.thenoughtfox.orasulmeu.utils.view.Alert
import com.thenoughtfox.orasulmeu.utils.view.CircleProgress
import com.thenoughtfox.orasulmeu.utils.view.Toolbar

@Composable
fun CreatePostMediaPage(
    uiState: State,
    onSendEvent: (Event) -> Unit,
    onCameraClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {

    if (uiState.removedUri != null) {
        Alert(
            onDismissRequest = {
                onSendEvent(Event.DismissAlert)
            },
            onConfirmation = {
                onSendEvent(Event.RemoveImage(uiState.removedUri))
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
        Toolbar(
            title = stringResource(id = R.string.create_post_toolbar_title),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        val image = uiState.image ?: R.drawable.image_placeholder

        AsyncImage(
            model = image, contentDescription = "Image",
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
                        onClick = { uri ->
                            onSendEvent(Event.SelectImage(uri))
                        },
                        onRemove = { uri ->
                            onSendEvent(Event.ShowAlert(uri))
                        })
                }
            }
        }

        UploadButtons(
            onClickMedia = onGalleryClick,
            onClickCamera = onCameraClick
        )

        Spacer(modifier = Modifier.weight(1f))

        RoundButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.create_post_button_next),
            onClick = onNextClick
        )
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
    image: Uri,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    onClick: ((Uri) -> Unit)? = null,
    onRemove: ((Uri) -> Unit)? = null
) {
    Box {
        AsyncImage(
            model = image,
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