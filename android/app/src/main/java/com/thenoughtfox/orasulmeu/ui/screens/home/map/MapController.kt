package com.thenoughtfox.orasulmeu.ui.screens.home.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
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
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeViewModel
import com.thenoughtfox.orasulmeu.utils.showToast
import com.thenoughtfox.orasulmeu.utils.toPoint
import kotlinx.coroutines.launch
import org.openapitools.client.models.PostDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapController(homeViewModel: HomeViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state = homeViewModel.state.collectAsStateWithLifecycle().value

    val mapViewModel: MapViewModel = hiltViewModel()

    val sendEvent: (MapContract.Event) -> Unit = {
        scope.launch { mapViewModel.event.send(it) }
    }

    val locationClient: LocationClient = remember {
        LocationClient(context) { location ->
            sendEvent(MapContract.Event.NavigateToUser(location.toPoint()))
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


    val mapView = remember {
        MapboxMapView(context)
    }

    var postToShow: PostDto? by remember {
        mutableStateOf(null)
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            mapViewModel.action.collect { action ->
                when (action) {
                    is MapContract.Action.MoveToLocation -> {
                        mapView.redirectToLocation(action.point)
                    }

                    is MapContract.Action.ShowToast -> context.showToast(action.msg)
                }
            }
        }
    }

    LaunchedEffect(state) {
        if (mapView.isAttachedToWindow) {
            mapView.clearPlaces()
            mapView.addPosts(state.postsToShow)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
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
                            PostContract.Action.ConfirmReport -> scope.launch {
                                homeViewModel.sendEvent(HomeContract.Event.SendReport(it.id))
                            }

                            PostContract.Action.Dislike -> scope.launch {
                                homeViewModel.sendEvent(HomeContract.Event.DislikePost(it.id))
                            }

                            PostContract.Action.Like -> scope.launch {
                                homeViewModel.sendEvent(HomeContract.Event.LikePost(it.id))
                            }

                            PostContract.Action.RevokeReaction -> scope.launch {
                                homeViewModel.sendEvent(HomeContract.Event.RevokeReaction(it.id))
                            }
                        }
                    }
                }
            }
        }
    }
}