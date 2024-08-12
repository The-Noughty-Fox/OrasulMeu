package com.thenoughtfox.orasulmeu.ui.post.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import org.openapitools.client.models.Media

@Composable
fun MediaView(mediaItem: Media, modifier: Modifier = Modifier) {
    CoilImage(
        modifier = modifier,
        imageModel = { mediaItem.url },
        previewPlaceholder = R.drawable.photo_placeholder,
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        ),
        imageLoader = {
            // for video thumbnail
            ImageLoader.Builder(LocalContext.current)
                .components { add(VideoFrameDecoder.Factory()) }
                .build()
        },
        loading = {
            CircularProgressIndicator(
                modifier = modifier.padding(175.dp),
                color = colorResource(R.color.primary),
                strokeWidth = 4.dp
            )
        },
        failure = {
            Icon(
                modifier = modifier.padding(16.dp),
                painter = painterResource(R.drawable.image_loading_failed_pic),
                contentDescription = "Media loading failure",
                tint = colorResource(R.color.grey)
            )
        }
    )
}

@Preview
@Composable
private fun Preview() = OrasulMeuTheme {
    val media = Media(
        id = 0,
        fileName = "test",
        type = Media.Type.image,
        url = "https://placehold.co/600x400/png"
    )

    Box(
        modifier = Modifier
            .background(color = colorResource(R.color.white))
    ) {
        MediaView(media, modifier = Modifier.size(128.dp))
    }
}