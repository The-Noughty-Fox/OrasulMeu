package com.thenoughtfox.orasulmeu.ui.post

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
import androidx.compose.material.Divider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.post.elements.MediaView
import com.thenoughtfox.orasulmeu.ui.post.elements.ReactionButton
import com.thenoughtfox.orasulmeu.ui.post.utils.PostPreviewPlaceholders
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun PostView(
    modifier: Modifier = Modifier,
    state: PostContract.State,
    type: PostScreenType = PostScreenType.LIST,
    sendEvent: (PostContract.Event) -> Unit = {}
) {

    var shouldShowReportAlert by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .wrapContentSize()
            .background(color = colorResource(R.color.white))
    ) {
        val pagerState = rememberPagerState(0, pageCount = { state.media.count() })

        HorizontalPager(modifier = Modifier.fillMaxWidth(), state = pagerState, pageContent = {
            MediaView(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), mediaItem = state.media[it]
            )
        })

        // reaction, pager indicator & three dots
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val (reaction, pagerIndication, threeDots) = createRefs()
            ReactionButton(reaction = state.reaction,
                onLike = { sendEvent(PostContract.Event.Like) },
                onDislike = { sendEvent(PostContract.Event.Dislike) },
                onRevokeReaction = { sendEvent(PostContract.Event.RevokeReaction) },
                modifier = Modifier.constrainAs(reaction) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top, 8.dp)
                    bottom.linkTo(parent.bottom, 8.dp)
                }
            )

            PagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.constrainAs(pagerIndication) {
                    centerTo(parent)
                })

            ThreeDotsIcon(
                type = type,
                onReportClick = { shouldShowReportAlert = true },
                onEditClick = { sendEvent(PostContract.Event.Edit) },
                onDeleteClick = { sendEvent(PostContract.Event.Delete) },
                modifier = Modifier
                    .constrainAs(threeDots) {
                        end.linkTo(parent.end)
                        centerVerticallyTo(parent)
                    }
                    .size(24.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        Text(
            text = state.address,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colorResource(R.color.primary)
            )
        )

        // author + time ago || date
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.author, style = TextStyle(
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )

            Text(
                text = state.time,
                style = TextStyle(fontSize = 14.sp, color = colorResource(R.color.grey))
            )
        }

        CombinedTitleWithBody(title = state.title, body = state.textContent)

        // TODO comments
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                painter = painterResource(R.drawable.ic_expand),
//                contentDescription = stringResource(R.string.show_comments_icon_desc),
//                tint = colorResource(R.color.comment_area),
//                modifier = Modifier.size(24.dp)
//            )
//
//            Text(
//                text = stringResource(R.string.open_comments_text),
//                style = TextStyle(fontSize = 14.sp, color = colorResource(R.color.comment_area))
//            )
//        }

        if (shouldShowReportAlert) {
            AlertDialog(
                onDismissRequest = { shouldShowReportAlert = false },
                confirmButton = {
                    Button(onClick = {
                        sendEvent(PostContract.Event.ConfirmReport)
                        shouldShowReportAlert = false
                    }) {
                        Text(text = "Raport")
                    }
                },
                dismissButton = {
                    Button(onClick = { shouldShowReportAlert = false }) { Text(text = "Anulare") }
                },
                title = { Text(text = "Raportați postarea") },
                text = { Text(text = "Sigur vrei să raportezi postarea?") }
            )
        }
    }
}

@Composable
private fun PagerIndicator(pagerState: PagerState, modifier: Modifier = Modifier) = Row(
    modifier.wrapContentHeight(), horizontalArrangement = Arrangement.Center
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
private fun ThreeDotsIcon(
    type: PostScreenType,
    onReportClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    //TODO: Impl report
    if (type == PostScreenType.LIST) return
    var isDropDownExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.clickable { isDropDownExpanded = true }) {
        Icon(
            painter = painterResource(R.drawable.ic_three_dots_horizontal),
            contentDescription = stringResource(R.string.three_dots_icon_desc),
            tint = colorResource(R.color.black),
            modifier = Modifier.size(24.dp)

        )

        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
        ) {
            DropdownMenu(
                modifier = Modifier.background(
                    color = colorResource(
                        if (type == PostScreenType.LIST) {
                            R.color.white
                        } else {
                            R.color.slate_100
                        }
                    )
                ),
                expanded = isDropDownExpanded,
                onDismissRequest = { isDropDownExpanded = false }) {
                when (type) {
                    PostScreenType.LIST -> {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.report_post_button_text),
                                    style = TextStyle(
                                        color = colorResource(R.color.error),
                                        fontSize = 17.sp
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

                    PostScreenType.PROFILE -> {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.menu_edit_post),
                                    style = TextStyle(
                                        color = colorResource(R.color.slate_950),
                                        fontSize = 17.sp
                                    )
                                )
                            }, trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_edit),
                                    tint = colorResource(R.color.slate_950),
                                    contentDescription = "edit"
                                )
                            }, onClick = {
                                isDropDownExpanded = false
                                onEditClick()
                            })

                        Divider()

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.menu_delete_post),
                                    style = TextStyle(
                                        color = colorResource(R.color.error),
                                        fontSize = 17.sp
                                    )
                                )
                            }, trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete),
                                    tint = colorResource(R.color.error),
                                    contentDescription = "delete"
                                )
                            }, onClick = {
                                isDropDownExpanded = false
                                onDeleteClick()
                            })
                    }
                }
            }
        }
    }
}

@Composable
private fun CombinedTitleWithBody(title: String, body: String) {
    val maxLength = 85
    var isBodyTooLong = false
    var shouldShowFullComment by remember { mutableStateOf(false) }

    val text = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(title)
        }

        isBodyTooLong = body.count() > maxLength
        val displayText = if (isBodyTooLong && !shouldShowFullComment) {
            body.take(maxLength) + "..."
        } else {
            body
        }

        append(" $displayText")
        if (isBodyTooLong && !shouldShowFullComment) {
            withStyle(style = SpanStyle(color = colorResource(R.color.comment_area))) {
                append(stringResource(id = R.string.post_more))
            }
        }
    }

    Text(
        text = text,
        style = TextStyle(fontSize = 14.sp),
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .clickable {
                if (isBodyTooLong) {
                    shouldShowFullComment = !shouldShowFullComment
                }
            },
    )
}

enum class PostScreenType {
    LIST,
    PROFILE
}

@Preview(showBackground = true, backgroundColor = 0xffffff, showSystemUi = true)
@Composable
private fun Preview() = OrasulMeuTheme {
    PostView(state = PostPreviewPlaceholders.postState, sendEvent = {})
}