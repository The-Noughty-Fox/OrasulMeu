package com.thenoughtfox.orasulmeu.ui.create_post.create

import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.create_post.Event
import com.thenoughtfox.orasulmeu.ui.create_post.media.RoundButton
import com.thenoughtfox.orasulmeu.ui.create_post.media.UserImage
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import com.thenoughtfox.orasulmeu.ui.theme.bodyBoldModifier
import com.thenoughtfox.orasulmeu.ui.theme.bodyModifier
import com.thenoughtfox.orasulmeu.ui.theme.outlinedTextFieldModifier
import com.thenoughtfox.orasulmeu.ui.theme.pageModifier
import com.thenoughtfox.orasulmeu.utils.view.Toolbar
import kotlinx.coroutines.launch

@Composable
fun CreatePostPage(viewModel: CreatePostViewModel = viewModel()) {

    val uiState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val outState = rememberScrollState()

    OrasulMeuTheme {
        Column(
            modifier = Modifier
                .pageModifier()
                .padding(horizontal = 16.dp)
                .verticalScroll(outState)
        ) {
            Toolbar(
                title = stringResource(id = R.string.create_post_toolbar_title),
                onBackClickListener = {
                    coroutineScope.launch {
                        viewModel.event.send(Event.BackToMediaPage)
                    }
                }
            )

            Text(
                text = stringResource(id = R.string.create_post_field_title),
                style = bodyBoldModifier(),
                modifier = Modifier.padding(top = 36.dp)
            )

            OutlinedTextField(
                value = uiState.title, onValueChange = { text ->
                    coroutineScope.launch {
                        viewModel.event.send(Event.SetTitle(text))
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(8.dp),
                textStyle = bodyModifier(),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .outlinedTextFieldModifier()
                    .defaultMinSize(minHeight = 46.dp)
            )

            Text(
                text = stringResource(id = R.string.create_post_field_desc),
                style = bodyBoldModifier(),
                modifier = Modifier.padding(top = 36.dp)
            )

            OutlinedTextField(
                value = uiState.description, onValueChange = { text ->
                    coroutineScope.launch {
                        viewModel.event.send(Event.SetDescription(text))
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(8.dp),
                textStyle = bodyModifier(),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .outlinedTextFieldModifier()
                    .defaultMinSize(minHeight = 186.dp)
            )

            if (uiState.images.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.create_post_field_media),
                    style = bodyBoldModifier(),
                    modifier = Modifier.padding(top = 36.dp)
                )

                MediaList(images = uiState.images, outState)
            }

            Spacer(modifier = Modifier.weight(1f))

            val isTitleEmpty = uiState.title.isEmpty()
            RoundButton(modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.create_post_button_submit),
                backgroundColor = colorResource(id = if (isTitleEmpty) R.color.white else R.color.dark_blue),
                textColor = colorResource(id = if (isTitleEmpty) R.color.black else R.color.white),
                isLoading = uiState.isLoading,
                onClick = {
                    if (!isTitleEmpty) {
                        coroutineScope.launch {
                            viewModel.event.send(Event.Submit)
                        }
                    }
                })
        }
    }
}

@Composable
private fun MediaList(images: List<Uri>, outState: ScrollState) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .heightIn(max = 1000.dp)
            .nestedScroll(connection = object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    if (outState.canScrollForward && available.y < 0) {
                        val consumed = outState.dispatchRawDelta(-available.y)
                        return Offset(x = 0f, y = -consumed)
                    }
                    return Offset.Zero
                }
            })
    ) {
        items(images) { uri ->
            Box(
                modifier = Modifier.aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                UserImage(uri)
            }
        }

    }
}

@Preview
@Composable
private fun PreviewCreatePostPage() {
    CreatePostPage()
}