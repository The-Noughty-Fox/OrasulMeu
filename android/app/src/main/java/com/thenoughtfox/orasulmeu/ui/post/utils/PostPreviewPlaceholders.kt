package com.thenoughtfox.orasulmeu.ui.post.utils

import com.thenoughtfox.orasulmeu.ui.post.PostContract
import org.openapitools.client.models.MediaSupabaseDto
import org.openapitools.client.models.PointDto
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.PostReactionsDto
import org.openapitools.client.models.UserDto
import java.time.OffsetDateTime

/**
 * @author Knurenko Bogdan 26.04.2024
 */
object PostPreviewPlaceholders {
    val postDto = PostDto(
        id = 0,
        title = "Test post dto",
        content = "Loren ipsum",
        author = UserDto(
            email = "test@gmail.com",
            username = "John Doe",
            id = 4
        ),
        comments = 4,
        location = PointDto(0.0, 0.0),
        locationAddress = "31 August str",
        reactions = PostReactionsDto(
            dislike = 35,
            like = 401,
            userReaction = null
        ),
        media = listOf(
            MediaSupabaseDto(
                id = 1,
                type = MediaSupabaseDto.Type.image,
                url = "https://i.pinimg.com/originals/a9/21/5a/a9215adf9680895e8c609ea27421f4b0.png",
                fileName = "filename",
                bucketPath = "",
            ),
            MediaSupabaseDto(
                id = 2,
                type = MediaSupabaseDto.Type.image,
                url = "https://i.pinimg.com/originals/a9/21/5a/a9215adf9680895e8c609ea27421f4b0.png",
                fileName = "filename",
                bucketPath = ""
            ),
            MediaSupabaseDto(
                id = 3,
                type = MediaSupabaseDto.Type.image,
                url = "https://i.pinimg.com/originals/a9/21/5a/a9215adf9680895e8c609ea27421f4b0.png",
                fileName = "filename",
                bucketPath = ""
            )
        ),
        createdAt = OffsetDateTime.now()
    )

    val dummyPosts = List(12) {
        val longitude = 28.8574 + it * 0.001
        val latitude = 47.0042 + it * 0.001


        postDto.copy(location = PointDto(latitude = latitude, longitude = longitude))
    }

    val postState = PostContract.State().copy(
        author = "John Doe",
        time = "6 min ago",
        address = "Ulitsa Pushkina dom Kolotushkina",
        title = "Hello Luke",
        textContent = "Have you heard the story about lord Darth Plegas the Wise blah blah blah blah blah blah blah blah blah blah blah blah",
        media = listOf(
            MediaSupabaseDto(
                id = 0,
                type = MediaSupabaseDto.Type.image,
                url = "test",
                fileName = "test.jpg",
                bucketPath = ""
            ),
            MediaSupabaseDto(
                id = 0,
                type = MediaSupabaseDto.Type.image,
                url = "test",
                fileName = "test.jpg",
                bucketPath = ""
            ),
            MediaSupabaseDto(
                id = 0,
                type = MediaSupabaseDto.Type.image,
                url = "test",
                fileName = "test.jpg",
                bucketPath = ""
            )
        ),
        reaction = PostContract.Reaction(
            selectedReaction = PostContract.Reactions.LIKE, likes = 214, dislikes = 63
        )
    )
}