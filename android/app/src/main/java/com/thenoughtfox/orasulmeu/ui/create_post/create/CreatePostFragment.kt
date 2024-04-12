package com.thenoughtfox.orasulmeu.ui.create_post.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private val viewModel: CreatePostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            OrasulMeuTheme {
                val uiState by viewModel.state.collectAsState()
                CreatePostPage(uiState) { event ->
                    lifecycleScope.launch {
                        viewModel.event.send(event)
                    }
                }
            }
        }
    }
}