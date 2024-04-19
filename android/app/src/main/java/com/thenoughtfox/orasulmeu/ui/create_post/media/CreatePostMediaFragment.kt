package com.thenoughtfox.orasulmeu.ui.create_post.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostContract.Action
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostContract.Event
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import com.thenoughtfox.orasulmeu.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePostMediaFragment : Fragment() {

    private val viewModel: CreatePostViewModel by activityViewModels()
    private var job: Job? = null

    // Registers a photo picker activity launcher in multi-select mode.
    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            if (uris.isEmpty()) return@registerForActivityResult
            lifecycleScope.launch {
                viewModel.event.send(Event.PickImages(uris))
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            OrasulMeuTheme {
                val uiState by viewModel.state.collectAsState()
                CreatePostMediaPage(
                    uiState = uiState,
                    onSendEvent = { lifecycleScope.launch { viewModel.event.send(it) } }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservables()
    }

    private fun subscribeObservables() {
        job = lifecycleScope.launch {
            viewModel.action.collect { action ->
                when (action) {
                    is Action.ShowToast -> context?.showToast(action.msg)
                    Action.OpenPhotoPicker -> {
                        pickMultipleMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        job?.cancel()
        super.onDestroyView()
    }
}