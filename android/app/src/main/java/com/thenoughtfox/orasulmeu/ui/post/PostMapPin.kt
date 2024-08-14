package com.thenoughtfox.orasulmeu.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.coil.CoilImage
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import org.openapitools.client.models.MediaSupabaseDto
import org.openapitools.client.models.PointDto
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.PostReactionsDto
import org.openapitools.client.models.UserDto
import java.time.OffsetDateTime

@Composable
fun PostMapPin(postDto: PostDto, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .clip(ChatPinShape())
            .clickable { onClick() }
            .background(color = Color.Black)
            .width(36.dp)
            .height(48.dp)
    ) {
        CoilImage(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(2.dp)
                .size(32.dp)
                .clip(CircleShape),
            imageModel = { postDto.media.firstOrNull()?.url },
            previewPlaceholder = R.drawable.photo_placeholder,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun Preview() = OrasulMeuTheme {
    val author = UserDto(
        id = 0,
        email = "test@gmail.com",
        username = "John Doe",
        socialProfilePictureUrl = null
    )
    val dto = PostDto(
        author = author,
        comments = "",
        content = "Some dummy content",
        id = 0,
        locationAddress = "31th August str. 24",
        media = listOf(
            MediaSupabaseDto(
                id = 0,
                type = MediaSupabaseDto.Type.image,
                fileName = "",
                bucketPath = "",
                url = "https://i.pinimg.com/originals/a9/21/5a/a9215adf9680895e8c609ea27421f4b0.png"
            )
        ),
        reactions = PostReactionsDto(dislike = 2, like = 12, userReaction = null),
        title = "Some dummy title",
        createdAt = OffsetDateTime.now(),
        location = PointDto(0.0, 0.0)
    )
    PostMapPin(postDto = dto)
}

class ChatPinShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(Path().apply {
            val width = size.width
            val height = size.height

            val triangleHeight = height * .4f

            val startPoint = Point(x = width / 2, y = height)
            val leftPoint = Point(x = 0f, y = triangleHeight)
            val rightPoint = Point(x = width, y = triangleHeight)
            val topPoint = Point(x = width / 2, y = 0f)

            val leftBottomAnchor = Point(x = 0f, y = height * .75f)
            val rightBottomAnchor = Point(x = width, y = height * .75f)

            val leftTopAnchor = Point(x = width * .04f, y = height * .04f)
            val rightTopAnchor = Point(x = width * .96f, y = height * .04f)


            moveTo(startPoint.x, startPoint.y)

            quadraticTo(leftBottomAnchor.x, leftBottomAnchor.y, leftPoint.x, leftPoint.y)
            quadraticTo(leftTopAnchor.x, leftTopAnchor.y, topPoint.x, topPoint.y)
            quadraticTo(rightTopAnchor.x, rightTopAnchor.y, rightPoint.x, rightPoint.y)
            quadraticTo(rightBottomAnchor.x, rightBottomAnchor.y, startPoint.x, startPoint.y)
            close()
        })
    }
}

private data class Point(val x: Float, val y: Float)
