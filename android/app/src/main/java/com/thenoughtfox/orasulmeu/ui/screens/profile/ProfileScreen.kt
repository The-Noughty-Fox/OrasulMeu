package com.thenoughtfox.orasulmeu.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.post.PostView
import com.thenoughtfox.orasulmeu.ui.post.utils.PostPreviewPlaceholders
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.NavEvent
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.State
import com.thenoughtfox.orasulmeu.ui.screens.profile.components.ClickableIcon
import com.thenoughtfox.orasulmeu.ui.screens.profile.components.ProfileView
import com.thenoughtfox.orasulmeu.ui.screens.profile.components.TopBar
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun ProfileScreen(
    state: State,
    onSendEvent: (Event) -> Unit = {},
    sendNavEvent: (NavEvent) -> Unit = {},
    pickImage: () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.background_color))
            .statusBarsPadding(),
        topBar = {
            TopBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                rightItem = {
                    if (state.isEditing) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable { onSendEvent(Event.SaveChanges) }
                        ) {
                            Text(text = stringResource(R.string.save))
                        }
                    } else {
                        ClickableIcon(
                            painter = painterResource(R.drawable.ic_settings),
                            color = colorResource(R.color.icons_dark_grey),
                            onClick = { sendNavEvent(NavEvent.GoToSettings) }
                        )
                    }
                },
                titleText = stringResource(R.string.my_profile_screen_title),
                leftItem = {
                    if (state.isEditing) {
                        ClickableIcon(
                            painter = painterResource(R.drawable.ic_chevron_left),
                            color = colorResource(R.color.icons_dark_grey),
                            onClick = {
                                onSendEvent(Event.DiscardChanges)
                            }
                        )
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(R.color.background_color))
                    .padding(padding)
            ) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ProfileView(
                            name = state.name,
                            avatarImageUrl = state.imageUrl,
                            postCount = state.postsCount,
                            reactionsCount = state.reactionsCount,
                            isEditionModeEnabled = state.isEditing,
                            onEditPress = { onSendEvent(Event.EditProfile) },
                            onNameTextChange = { onSendEvent(Event.ChangeName(it)) },
                            onChangeImagePress = { pickImage() }
                        )
                    }
                }

                item {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                        text = "Postările mele",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(600),
                            color = colorResource(R.color.black),
                        )
                    )
                }

                items(state.ownedPost) {
                    PostView(state = it) { }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() = OrasulMeuTheme {
    val state = State(
        name = "John Doe",
        ownedPost = List(3) { PostPreviewPlaceholders.postState }
    )
    ProfileScreen(state = state, onSendEvent = {}, pickImage = {})
}