package com.thenoughtfox.orasulmeu.ui.screens.create_post.map

import android.Manifest
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.hilt.navigation.compose.hiltViewModel
import com.mapbox.search.ReverseGeoOptions
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.databinding.FragmentMapSearchBinding
import com.thenoughtfox.orasulmeu.navigation.LocalNavigator
import com.thenoughtfox.orasulmeu.service.LocationClient
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.screens.create_post.media.RoundButton
import com.thenoughtfox.orasulmeu.utils.applyBottomInsetMargin
import com.thenoughtfox.orasulmeu.utils.applyTopStatusInsetMargin
import com.thenoughtfox.orasulmeu.utils.hideKeyboard
import com.thenoughtfox.orasulmeu.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Knurenko Bogdan 07.06.2024
 */

@Composable
fun MapSearchController(createPostViewModel: CreatePostViewModel) {
    val navigator = LocalNavigator.current
    val viewModel: MapSearchViewModel =
        hiltViewModel<MapSearchViewModel, MapSearchViewModel.Factory> {
            it.create(navigator)
        }

    val adapter: SearchSuggestionAdapter = remember { SearchSuggestionAdapter() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current


    val locationClient: LocationClient = remember {
        LocationClient(context) { location ->
            scope.launch {
                viewModel.event.send(Event.NavigateToPlayer(location))
            }
        }
    }

    var isPinVisible: Boolean by remember { mutableStateOf(false) }

    val locationRequester = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.all { it.value }) {
            isPinVisible = true
            locationClient.getLastLocation()
        } else {
            context.showToast("Please provide location permissions")
        }
    }

    val mapSearchState = viewModel.state.collectAsState().value
    val action = viewModel.action.collectAsState(Action.Initial).value

    var moveToLocation: Action.MoveToLocation? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(action) {

        when (action) {
            is Action.MoveToLocation -> {
                moveToLocation = action
            }

            is Action.ShowToast -> context.showToast(action.msg)
            Action.Initial -> Unit
        }
    }


    AndroidViewBinding(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .background(color = colorResource(R.color.background_color)),
        factory = { inflater, parent, attach ->
            FragmentMapSearchBinding.inflate(inflater, parent, attach).apply {
                this.buttonNext.apply {
                    setContent {
                        RoundButton(modifier = Modifier.padding(horizontal = 24.dp),
                            text = stringResource(id = R.string.map_button_next),
                            backgroundColor = colorResource(id = R.color.button_next_color),
                            textColor = colorResource(id = R.color.black),
                            onClick = {
                                scope.launch {
                                    viewModel.event.send(Event.TappedNext)
                                }
                            })
                    }
                }

                containerSearch.applyTopStatusInsetMargin()
                buttonNext.applyBottomInsetMargin()
                adapter.setOnItemClicked {
                    editTextSearch.apply {
                        text.clear()
                        clearFocus()
                    }

                    scope.launch { viewModel.event.send(Event.OnSearchSuggestionClicked(it)) }
                }
                recyclerViewLocation.adapter = adapter


                editTextSearch.doOnTextChanged { text, _, _, _ ->
                    scope.launch {
                        viewModel.event.send(Event.DoOnTextLocationChanged(text.toString()))
                    }
                }

                imageViewClose.setOnClickListener {
                    editTextSearch.apply {
                        text.clear()
                        clearFocus()
                        hideKeyboard()
                    }
                }

                mapView.apply {
                    onLoadMap {
                        locationRequester.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            )
                        )

                    }

                    onCameraTrackingDismissed {
                        scope.launch {
                            viewModel.event.send(
                                Event.OnCameraTrackingDismissed(
                                    ReverseGeoOptions(center = mapboxMap.cameraState.center)
                                )
                            )
                        }
                    }
                }

                // state part
                adapter.submitList(mapSearchState.suggestions)
                recyclerViewLocation.isVisible = mapSearchState.isSuggestionListShow
                layoutPin.isVisible = !mapSearchState.isSuggestionListShow
                textViewStreetName.text = mapSearchState.address
                containerPinStreetName.visibility =
                    if (mapSearchState.address.isNotEmpty()) View.VISIBLE else View.INVISIBLE

                scope.launch(Dispatchers.IO) {
                    createPostViewModel.event.send(
                        CreatePostContract.Event.SetAddress(
                            mapSearchState.address
                        )
                    )
                }
            }
        }) {
        moveToLocation?.let {
            mapView.redirectToLocation(it.point)
            root.hideKeyboard()
            moveToLocation = null
        }
    }
}