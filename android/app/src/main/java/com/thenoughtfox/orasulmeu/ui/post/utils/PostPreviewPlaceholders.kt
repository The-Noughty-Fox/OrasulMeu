package com.thenoughtfox.orasulmeu.ui.post.utils

import com.thenoughtfox.orasulmeu.ui.post.PostContract
import org.openapitools.client.models.Media
import org.openapitools.client.models.PointDto
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.PostReactionsDto
import org.openapitools.client.models.UserDto

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
            firstName = "John",
            lastName = "Doe",
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
            Media(
                id = 1,
                type = Media.Type.image,
                url = "url",
                fileName = "filename"
            ),
            Media(
                id = 2,
                type = Media.Type.image,
                url = "url",
                fileName = "filename"
            ),
            Media(
                id = 3,
                type = Media.Type.image,
                url = "url",
                fileName = "filename"
            )
        )
    )

    val postState = PostContract.State().copy(
        author = "John Doe",
        time = "6 min ago",
        address = "Ulitsa Pushkina dom Kolotushkina",
        title = "Hello Luke",
        textContent = "Have you heard the story about lord Darth Plegas the Wise blah blah blah blah blah blah blah blah blah blah blah blah",
        media = listOf(
            Media(id = 0, type = Media.Type.image, url = "test", fileName = "test.jpg"),
            Media(id = 0, type = Media.Type.image, url = "test", fileName = "test.jpg"),
            Media(id = 0, type = Media.Type.image, url = "test", fileName = "test.jpg")
        ),
        reaction = PostContract.Reaction(
            selectedReaction = PostContract.Reactions.LIKE, likes = 214, dislikes = 63
        )
    )
}