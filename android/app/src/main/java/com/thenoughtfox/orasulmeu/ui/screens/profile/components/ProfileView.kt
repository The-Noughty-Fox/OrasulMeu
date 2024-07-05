package com.thenoughtfox.orasulmeu.ui.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun ProfileView(
    name: String,
    avatarImageUrl: String?,
    postCount: Int,
    reactionsCount: Int,
    isEditionModeEnabled: Boolean,
    onEditPress: () -> Unit,
    onChangeImagePress: () -> Unit,
    onNameTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        CoilImage(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(120.dp)
                .zIndex(1f)
                .clip(CircleShape)
                .background(color = Color.Black),
            imageModel = { avatarImageUrl },
            previewPlaceholder = R.drawable.photo_placeholder,
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop, alignment = Alignment.Center
            ),
            imageLoader = {
                // for video thumbnail
                ImageLoader.Builder(LocalContext.current)
                    .components { add(VideoFrameDecoder.Factory()) }.build()
            },
            loading = {
                CircularProgressIndicator(
                    modifier = Modifier.padding(24.dp),
                    color = colorResource(R.color.primary),
                    strokeWidth = 4.dp
                )
            },
            failure = {
                Icon(
                    modifier = Modifier.padding(16.dp),
                    painter = painterResource(R.drawable.image_loading_failed_pic),
                    contentDescription = "Media loading failure",
                    tint = colorResource(R.color.grey)
                )
            }
        )

        if (isEditionModeEnabled) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .zIndex(2f)
                    .background(color = Color(0xFF003566))
                    .clickable { onChangeImagePress() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_profile_edit_photo),
                    contentDescription = null,
                    tint = colorResource(R.color.white),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Column(
            modifier = Modifier
                .zIndex(0f)
                .padding(top = 36.dp)
                .heightIn(min = 120.dp)
                .background(color = Color(0xFFDCE2E9), shape = RoundedCornerShape(18.dp))
                .fillMaxWidth(),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isEditionModeEnabled) {
                var nameEditable: String by remember { mutableStateOf(name) }
                val onNameChange: (String) -> Unit = {
                    onNameTextChange(it)
                    nameEditable = it
                }

                BasicTextField(
                    value = nameEditable,
                    onValueChange = { onNameChange(it) },
                    modifier = Modifier
                        .padding(top = 120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = OrasulMeuTheme.colors.backgroundWhite.copy(alpha = .4f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        color = colorResource(R.color.black),
                        fontWeight = FontWeight(700)
                    )
                )
            } else {
                Text(
                    text = name,
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = colorResource(R.color.black),
                        fontWeight = FontWeight(700)
                    ),
                    modifier = Modifier.padding(top = 100.dp)
                )
            }

            // click to edit
            if (!isEditionModeEnabled) {
                Box(modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onEditPress() }
                    .padding(4.dp)) {
                    Text(
                        text = "Atinge pentru a edita", style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(400),
                            color = colorResource(R.color.grey)
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(
                        top = 36.dp, bottom = 24.dp, start = 16.dp, end = 16.dp
                    )
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProfileInfoItem(title = "Reclamații", value = postCount.toString())
                VerticalDivider(thickness = 1.dp, color = colorResource(R.color.black))
                ProfileInfoItem(title = "Reactii", value = reactionsCount.toString())
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(title: String, value: String) =
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value, style = TextStyle(
                fontSize = 24.sp, fontWeight = FontWeight(700), color = colorResource(R.color.black)
            )
        )
        Text(
            text = title, style = TextStyle(
                fontSize = 16.sp, fontWeight = FontWeight(400), color = colorResource(R.color.black)
            )
        )
    }

@Preview(showBackground = true)
@Composable
private fun Preview() = OrasulMeuTheme {
    var isEditionModeEnabled by remember {
        mutableStateOf(false)
    }
    ProfileView(
        name = "John Doe",
        avatarImageUrl = "test",
        postCount = 42,
        reactionsCount = 112,
        isEditionModeEnabled = isEditionModeEnabled,
        onEditPress = { isEditionModeEnabled = true },
        onChangeImagePress = {},
        onNameTextChange = {}
    )
}