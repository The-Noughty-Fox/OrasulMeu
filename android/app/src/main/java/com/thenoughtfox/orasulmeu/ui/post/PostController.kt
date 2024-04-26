package com.thenoughtfox.orasulmeu.ui.post

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.thenoughtfox.orasulmeu.ui.post.utils.PostPreviewPlaceholders
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import com.thenoughtfox.orasulmeu.utils.isPreview
import org.openapitools.client.models.PostDto

@Composable
fun PostController(postItem: PostDto) {
    val postViewModel: PostViewModel? =
        if (!isPreview) injectedViewModel(postItem) else null

    val postViewState: PostContract.State =
        postViewModel?.state?.collectAsState()?.value ?: PostPreviewPlaceholders.postState

    postViewModel?.action?.collectAsState()?.value?.let {
        val context = LocalContext.current
        when (it) {
            PostContract.Action.RequestReportConfirmation -> {
                AlertDialog(
                    onDismissRequest = {},
                    confirmButton = {
                        Button(onClick = { postViewModel.emitEvent(PostContract.Event.ConfirmReport) }) {
                            Text(text = "Raport")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { }) { Text(text = "Anulare") }
                    },
                    title = { Text(text = "Raportați postarea") },
                    text = { Text(text = "Sigur vrei să raportezi postarea?") }
                )
            }

            PostContract.Action.ShowReportSendingFailed -> {
                Toast.makeText(context, "Oops, report sending error", Toast.LENGTH_SHORT).show()
            }

            PostContract.Action.ShowReportSubmitted -> {
                Toast.makeText(context, "Report successfully submitted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    PostView(
        state = postViewState,
        onSendEvent = { postViewModel?.emitEvent(it) }
    )
}

@Composable
private fun injectedViewModel(dto: PostDto): PostViewModel =
    hiltViewModel<PostViewModel, PostViewModel.PostViewModelFactory> { it.create(dto) }

@Preview(showBackground = true, backgroundColor = 0xff000000)
@Composable
private fun Preview() = OrasulMeuTheme {
    PostController(postItem = PostPreviewPlaceholders.postDto)
}

