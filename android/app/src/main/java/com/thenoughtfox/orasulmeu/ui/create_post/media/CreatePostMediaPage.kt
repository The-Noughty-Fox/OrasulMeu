package com.thenoughtfox.orasulmeu.ui.create_post.media

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.create_post.Event
import com.thenoughtfox.orasulmeu.ui.theme.bodyBoldModifier
import com.thenoughtfox.orasulmeu.ui.theme.pageModifier
import com.thenoughtfox.orasulmeu.ui.theme.subTitleModifier
import com.thenoughtfox.orasulmeu.utils.view.CircleProgress
import com.thenoughtfox.orasulmeu.utils.view.Toolbar
import kotlinx.coroutines.launch

@Composable
fun CreatePostMediaPage(viewModel: CreatePostViewModel = viewModel()) {

    val uiState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .pageModifier()
            .verticalScroll(rememberScrollState())
    ) {
        Toolbar(
            title = stringResource(id = R.string.create_post_toolbar_title),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        val image = if (uiState.image == null) {
            R.drawable.image_placeholder
        } else {
            uiState.image
        }

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
                    .padding(16.dp)
                    .horizontalScroll(rememberScrollState()),
            ) {
                uiState.images.forEach { image ->
                    val color = if (uiState.image == image) Color.Blue else Color.White
                    UserImage(image = image, color = color) {
                        coroutineScope.launch {
                            viewModel.event.send(Event.SelectImage(image))
                        }
                    }
                }
            }
        }

        UploadButtons(
            onClickMedia = {
                coroutineScope.launch {
                    viewModel.event.send(Event.OnClickMedia)
                }
            }, onClickCamera = {
                coroutineScope.launch {
                    viewModel.event.send(Event.OnClickCamera)
                }
            })

        Spacer(modifier = Modifier.weight(1f))

        RoundButton(modifier = Modifier
            .padding(horizontal = 16.dp)
            .align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.create_post_button_next),
            onClick = {
                coroutineScope.launch {
                    viewModel.event.send(Event.GoToPostPage)
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
            .height(44.dp),
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
    onClick: ((Uri) -> Unit)? = null
) {
    Surface(
        color = color,
        modifier = modifier
            .size(106.dp)
            .padding(end = 10.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .clickable { onClick?.invoke(image) },
        shape = RoundedCornerShape(8.dp)
    ) {
        AsyncImage(model = image, contentDescription = "User images")
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
    CreatePostMediaPage()
}