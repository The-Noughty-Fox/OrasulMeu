package com.thenoughtfox.orasulmeu.ui.post_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Knurenko Bogdan 14.06.2024
 */
@AndroidEntryPoint
class PostListFragment : Fragment() {

    private val vm: PostListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {

            LaunchedEffect(Unit) {
                vm.sendAction(PostListContract.Action.Refresh)
            }

            PostListScreen(state = vm.state.collectAsState().value, sendAction = vm::sendAction)
        }
    }
}