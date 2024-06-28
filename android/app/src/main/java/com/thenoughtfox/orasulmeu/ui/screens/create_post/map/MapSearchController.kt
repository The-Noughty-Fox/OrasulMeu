package com.thenoughtfox.orasulmeu.ui.screens.create_post.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.mapbox.search.ReverseGeoOptions
import com.mapbox.search.result.SearchAddress
import com.mapbox.search.result.SearchSuggestion
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.navigation.LocalCreatePostNavigator
import com.thenoughtfox.orasulmeu.service.LocationClient
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.screens.create_post.map.view.MapboxMapView
import com.thenoughtfox.orasulmeu.ui.screens.create_post.media.RoundButton
import com.thenoughtfox.orasulmeu.utils.showToast
import kotlinx.coroutines.launch

@Composable
fun MapSearchController(createPostViewModel: CreatePostViewModel) {
    val navigator = LocalCreatePostNavigator.current
    val viewModel: MapSearchViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

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

    val state by viewModel.state.collectAsState()
    val mapView = remember {
        MapboxMapView(context)
    }

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.action.collect { action ->
                when (action) {
                    is Action.MoveToLocation -> mapView.redirectToLocation(action.point)
                    is Action.ShowToast -> context.showToast(action.msg)
                }
            }
        }
    }

    LaunchedEffect(state.address) {
        createPostViewModel.event.send(
            CreatePostContract.Event.SetAddress(state.address)
        )
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
//            .safeDrawingPadding()
            .background(color = colorResource(R.color.background_color))
    ) {
        val (map, search, suggestion, pin, buttonNext) = createRefs()

        AndroidView(modifier = Modifier
            .fillMaxSize()
            .constrainAs(map) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                top.linkTo(parent.top)
            },
            factory = {
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
            }
        )

        SearchBarView(
            searchText = state.searchText,
            modifier = Modifier.constrainAs(search) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            }, onValueChanged = { text ->
                scope.launch {
                    viewModel.event.send(Event.DoOnTextLocationChanged(text))
                }
            }, onClear = {
                scope.launch {
                    focusManager.clearFocus()
                    viewModel.event.send(Event.ClearSearchText)
                }
            })


        if (state.isSuggestionListShow) {
            SuggestionListView(modifier = Modifier.constrainAs(suggestion) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(search.bottom)
                bottom.linkTo(buttonNext.top)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }, suggestions = state.suggestions) {
                scope.launch {
                    focusManager.clearFocus()
                    viewModel.event.send(
                        Event.OnSearchSuggestionClicked(it)
                    )
                }
            }
        }

        if (!state.isSuggestionListShow) {
            PinView(modifier = Modifier.constrainAs(pin) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }, state.address)
        }

        RoundButton(modifier = Modifier
            .constrainAs(buttonNext) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .padding(horizontal = 24.dp),
            text = stringResource(id = R.string.map_button_next),
            backgroundColor = colorResource(id = R.color.button_next_color),
            textColor = colorResource(id = R.color.black),
            onClick = {
                scope.launch {
                    navigator.navigateUp()
                }
            })
    }
}

@Composable
private fun PinView(modifier: Modifier, address: String) {
    Column(modifier = modifier) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(text = address, modifier = Modifier.padding(horizontal = 24.dp))
        }

        Image(
            painter = painterResource(id = R.drawable.ic_map_pin),
            contentDescription = "pin",
            modifier = Modifier
                .size(32.dp, 52.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun SuggestionListView(
    modifier: Modifier,
    suggestions: List<SearchSuggestion>,
    onItemClick: (SearchSuggestion) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        items(suggestions) { suggestion ->
            Column(modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                .clickable {
                    onItemClick(suggestion)
                }) {
                val shortAddress =
                    suggestion.address?.formattedAddress(SearchAddress.FormatStyle.Short)
                val address =
                    if (shortAddress.isNullOrEmpty()) suggestion.name else shortAddress
                Text(text = address, fontSize = 17.sp, color = Color.Black)
                Text(
                    text = suggestion.fullAddress.toString(),
                    fontSize = 15.sp,
                    color = colorResource(id = R.color.grey_search)
                )
            }
        }
    }
}

@Composable
private fun SearchBarView(
    modifier: Modifier,
    searchText: String,
    onValueChanged: (String) -> Unit,
    onClear: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp)
    ) {
        val (searchImage, editText, closeImage) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "search",
            Modifier
                .size(24.dp)
                .constrainAs(searchImage) {
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                }
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { text ->
                onValueChanged(text)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
            maxLines = 1,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.search_address),
                    color = Color.Black,
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
            ),
            modifier = Modifier.constrainAs(editText) {
                start.linkTo(searchImage.start)
                end.linkTo(closeImage.end)
                bottom.linkTo(parent.bottom)
                top.linkTo(parent.top)
            }
        )

        Image(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = "close",
            modifier = Modifier
                .size(24.dp)
                .constrainAs(closeImage) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                }
                .clickable { onClear() }
        )

    }

}