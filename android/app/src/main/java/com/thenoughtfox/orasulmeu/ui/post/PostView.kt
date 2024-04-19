@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.thenoughtfox.orasulmeu.ui.post

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.post.elements.ReactionButton
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import org.openapitools.client.models.Media

@Composable
fun PostView(state: PostContract.State, onSendEvent: (PostContract.Event) -> Unit) {
    Column(modifier = Modifier.wrapContentSize()) {
        val pagerState = rememberPagerState(0, pageCount = { state.media.count() })

        HorizontalPager(modifier = Modifier.fillMaxWidth(), state = pagerState, pageContent = {
            // todo replace it with media
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .aspectRatio(1f)
                    .background(
                        color = colorResource(R.color.black), shape = RoundedCornerShape(16.dp)
                    )
            )
        })

        // reaction, pager indicator & three dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReactionButton(reaction = state.reaction,
                isLoading = state.isReactionLoading,
                onLike = { onSendEvent(PostContract.Event.Like) },
                onDislike = { onSendEvent(PostContract.Event.Dislike) },
                onRevokeReaction = { onSendEvent(PostContract.Event.RevokeReaction) })

            PagerIndicator(pagerState = pagerState)

            ThreeDotsIcon(
                onReportClick = { onSendEvent(PostContract.Event.Report) },
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        // author + time ago || date
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.author,
                style = TextStyle(color = colorResource(R.color.primary), fontSize = 16.sp)
            )

            Text(
                text = state.time,
                style = TextStyle(fontSize = 14.sp, color = colorResource(R.color.grey))
            )
        }

        Text(
            text = state.address,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            style = TextStyle(
                fontSize = 14.sp, color = colorResource(R.color.black)
            )
        )

        CombinedTitleWithBody(title = state.title, body = state.textContent)

        // comments
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_expand),
                contentDescription = stringResource(R.string.show_comments_icon_desc),
                tint = colorResource(R.color.comment_area),
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = stringResource(R.string.open_comments_text),
                style = TextStyle(fontSize = 14.sp, color = colorResource(R.color.comment_area))
            )
        }
    }
}

@Composable
private fun PagerIndicator(pagerState: PagerState) = Row(
    Modifier.wrapContentHeight(), horizontalArrangement = Arrangement.Center
) {
    repeat(pagerState.pageCount) { iteration ->
        val isCurrent = pagerState.currentPage == iteration
        val color = if (isCurrent) colorResource(R.color.primary) else colorResource(R.color.grey)
        Box(
            modifier = Modifier
                .padding(2.dp)
                .clip(CircleShape)
                .background(color)
                .size(if (isCurrent) 8.dp else 6.dp)
        )
    }
}

@Composable
private fun ThreeDotsIcon(onReportClick: () -> Unit, modifier: Modifier = Modifier) {
    var isDropDownExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.clickable { isDropDownExpanded = true }) {
        Icon(
            painter = painterResource(R.drawable.ic_three_dots_horizontal),
            contentDescription = stringResource(R.string.three_dots_icon_desc),
            tint = colorResource(R.color.black),
            modifier = Modifier.size(24.dp)

        )

        DropdownMenu(expanded = isDropDownExpanded,
            onDismissRequest = { isDropDownExpanded = false }) {
            DropdownMenuItem(text = {
                Text(
                    text = stringResource(R.string.report_post_button_text), style = TextStyle(
                        color = colorResource(R.color.error), fontSize = 17.sp
                    )
                )
            }, trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_report),
                    tint = colorResource(R.color.error),
                    contentDescription = stringResource(R.string.report_post_icon_desc)
                )
            }, onClick = {
                isDropDownExpanded = false
                onReportClick()
            })
        }
    }
}

@Composable
private fun CombinedTitleWithBody(title: String, body: String) {
    val text = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(title)
        }
        append(" $body")
    }
    Text(
        text = text,
        style = TextStyle(fontSize = 14.sp), modifier = Modifier.padding(horizontal = 16.dp),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun Preview() = OrasulMeuTheme {
    val postState = PostContract.State().copy(
        author = "John Doe",
        time = "6 min ago",
        address = "Ulitsa Pushkina dom Kolotushkina",
        title = "Hello Luke",
        textContent = "Have you heard the story about lord Darth Plegas the Wise blah blah blah blah blah blah blah blah blah blah blah blah",
        media = listOf(
            Media(id = 0, type = Media.Type.image, url = "gay", fileName = "gay.jpg"),
            Media(id = 0, type = Media.Type.image, url = "gay", fileName = "gay.jpg"),
            Media(id = 0, type = Media.Type.image, url = "gay", fileName = "gay.jpg")
        ),
        reaction = PostContract.Reaction(
            selectedReaction = PostContract.Reactions.LIKE, count = 214
        )
    )
    PostView(state = postState, onSendEvent = {})
}