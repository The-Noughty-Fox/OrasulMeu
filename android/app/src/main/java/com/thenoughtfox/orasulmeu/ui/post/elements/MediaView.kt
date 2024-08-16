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
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import kotlinx.coroutines.Dispatchers
import org.openapitools.client.models.MediaSupabaseDto

@Composable
fun MediaView(mediaItem: MediaSupabaseDto, modifier: Modifier = Modifier) {
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(mediaItem.url)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(mediaItem.url)
        .diskCacheKey(mediaItem.url)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .build()

    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = "image",
        modifier = modifier,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        loading = {
            CircularProgressIndicator(
                modifier = modifier.padding(175.dp),
                color = colorResource(R.color.primary),
                strokeWidth = 4.dp
            )
        },
        error = {
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
    val media = MediaSupabaseDto(
        id = 0,
        fileName = "test",
        type = MediaSupabaseDto.Type.image,
        url = "https://placehold.co/600x400/png",
        bucketPath = ""
    )

    Box(
        modifier = Modifier
            .background(color = colorResource(R.color.white))
    ) {
        MediaView(media, modifier = Modifier.size(128.dp))
    }
}