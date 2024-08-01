package com.thenoughtfox.orasulmeu.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thenoughtfox.orasulmeu.ui.basic.ChangeViewTypeButton
import com.thenoughtfox.orasulmeu.ui.screens.home.map.MapController
import com.thenoughtfox.orasulmeu.ui.screens.home.post_list.PostListController

@Composable
fun HomeController() {
    val viewModel: HomeViewModel = hiltViewModel()
    var selectedViewType: SelectedViewType by rememberSaveable { mutableStateOf(SelectedViewType.Map) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedViewType) {
            SelectedViewType.Map -> MapController(homeViewModel = viewModel)
            SelectedViewType.List -> PostListController(viewModel = viewModel)
        }

        ChangeViewTypeButton(
            viewType = selectedViewType,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            onClick = {
                selectedViewType =
                    if (selectedViewType == SelectedViewType.Map) SelectedViewType.List else SelectedViewType.Map
            }
        )
    }
}

enum class SelectedViewType {
    Map, List
}