package com.thenoughtfox.orasulmeu.ui.post.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.post.PostContract
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReactionButton(
    reaction: PostContract.Reaction,
    isLoading: Boolean,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onRevokeReaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emojiTextStyle = TextStyle(fontSize = 24.sp)
    val emojiTextModifier = Modifier
        .padding(8.dp)
        .clip(RoundedCornerShape(16.dp))
    val reactionCountStyle = TextStyle(fontSize = 14.sp, color = colorResource(R.color.black))

    Row(
        modifier = modifier
            .background(
                color = colorResource(R.color.slate_100),
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = !isLoading && (reaction.selectedReaction != PostContract.Reactions.NOTHING),
                onClick = onRevokeReaction
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 24.dp)
                    .size(24.dp),
                color = colorResource(id = R.color.purple_700),
                strokeWidth = 2.dp
            )
        } else {
            when (reaction.selectedReaction) {
                PostContract.Reactions.LIKE -> {
                    Text(text = Emojis.LIKE, modifier = emojiTextModifier, style = emojiTextStyle)
                    Text(
                        text = reaction.count.toString(),
                        modifier = Modifier.padding(8.dp),
                        style = reactionCountStyle
                    )
                }

                PostContract.Reactions.DISLIKE -> {
                    Text(
                        text = Emojis.DISLIKE,
                        modifier = emojiTextModifier,
                        style = emojiTextStyle
                    )
                    Text(
                        text = reaction.count.toString(),
                        modifier = Modifier.padding(8.dp),
                        style = reactionCountStyle
                    )
                }

                PostContract.Reactions.NOTHING -> {
                    Text(
                        text = Emojis.DISLIKE,
                        style = emojiTextStyle,
                        modifier = emojiTextModifier.clickable { onDislike() }
                    )
                    Text(
                        text = Emojis.LIKE,
                        style = emojiTextStyle,
                        modifier = emojiTextModifier.clickable { onLike() })
                }
            }
        }
    }
}

private object Emojis {
    const val LIKE = "😍"
    const val DISLIKE = "\uD83E\uDD22"
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun Preview() = OrasulMeuTheme {
    var currentReaction: PostContract.Reaction by remember {
        mutableStateOf(
            PostContract.Reaction(
                selectedReaction = PostContract.Reactions.NOTHING,
                count = 24
            )
        )
    }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    fun delay2sec() = scope.launch {
        isLoading = true
        delay(2_000)
        isLoading = false
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .background(color = colorResource(id = R.color.white))
    ) {
        ReactionButton(
            modifier = Modifier.align(Alignment.Center),
            reaction = currentReaction,
            isLoading = isLoading,
            onLike = {
                delay2sec()
                currentReaction =
                    currentReaction.copy(selectedReaction = PostContract.Reactions.LIKE)
            },
            onDislike = {
                delay2sec()
                currentReaction =
                    currentReaction.copy(selectedReaction = PostContract.Reactions.DISLIKE)
            },
            onRevokeReaction = {
                delay2sec()
                currentReaction =
                    currentReaction.copy(selectedReaction = PostContract.Reactions.NOTHING)
            }
        )
    }

}