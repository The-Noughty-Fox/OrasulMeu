package com.thenoughtfox.orasulmeu.ui.screens.home.map

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.thenoughtfox.orasulmeu.service.LocationClient
import com.thenoughtfox.orasulmeu.ui.basic.FindMeOnMapButton
import com.thenoughtfox.orasulmeu.ui.post.PostContract
import com.thenoughtfox.orasulmeu.ui.post.PostView
import com.thenoughtfox.orasulmeu.ui.post.utils.PostDtoToStateMapper.toState
import com.thenoughtfox.orasulmeu.ui.screens.create_post.map.view.MapboxMapView
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.State
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeViewModel
import com.thenoughtfox.orasulmeu.utils.showToast
import com.thenoughtfox.orasulmeu.utils.toPoint
import kotlinx.coroutines.launch
import org.openapitools.client.models.PostDto

@Composable
fun MapController(viewModel: HomeViewModel) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val mapView = remember { MapboxMapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.action.collect { action ->
                when (action) {
                    is HomeContract.Action.MoveToLocation -> {
                        mapView.redirectToLocation(action.point)
                    }
                }
            }
        }
    }

    LaunchedEffect(state) {
        if (mapView.isAttachedToWindow) {
            mapView.clearPlaces()
            mapView.addPosts(state.popularPosts)
        }
    }

    MapView(mapView, context, state, sendEvent = {
        scope.launch {
            viewModel.sendEvent(it)
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapView(
    mapView: MapboxMapView,
    context: Context,
    state: State = State(),
    sendEvent: (HomeContract.Event) -> Unit = {}
) {
    var postToShow: PostDto? by remember { mutableStateOf(null) }

    val locationClient: LocationClient = remember {
        LocationClient(context) { location ->
            sendEvent(HomeContract.Event.NavigateToUser(location.toPoint()))
        }
    }

    val locationRequester = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.all { it.value }) {
            locationClient.getLastLocation()
        } else {
            context.showToast("Please provide location permissions")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                mapView.apply {
                    if (state.lastLocation != null) {
                        redirectToLocation(point = state.lastLocation)
                    }

                    onLoadMap {
                        if (state.lastLocation != null) return@onLoadMap
                        locationRequester.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            )
                        )
                    }

                    onPostClick { post ->
                        postToShow = post
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        FindMeOnMapButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = 72.dp)
        ) {
            locationRequester.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }

        if (postToShow != null) {
            ModalBottomSheet(
                windowInsets = BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Bottom),
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                onDismissRequest = { postToShow = null }) {
                postToShow?.toState()?.let {
                    PostView(state = it) { action ->
                        when (action) {
                            PostContract.Action.ConfirmReport ->
                                sendEvent(HomeContract.Event.SendReport(it.id))


                            PostContract.Action.Dislike ->
                                sendEvent(HomeContract.Event.DislikePost(it.id))


                            PostContract.Action.Like ->
                                sendEvent(HomeContract.Event.LikePost(it.id))


                            PostContract.Action.RevokeReaction ->
                                sendEvent(HomeContract.Event.RevokeReaction(it.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewMapView() {
    val context = LocalContext.current
    val mapView = remember {
        MapboxMapView(context)
    }

    MapView(mapView = mapView, context = context)
}