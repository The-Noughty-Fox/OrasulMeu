package com.thenoughtfox.orasulmeu.ui.screens.home.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.thenoughtfox.orasulmeu.service.LocationClient
import com.thenoughtfox.orasulmeu.ui.basic.FindMeOnMapButton
import com.thenoughtfox.orasulmeu.ui.screens.create_post.map.view.MapboxMapView
import com.thenoughtfox.orasulmeu.utils.showToast
import com.thenoughtfox.orasulmeu.utils.toPoint
import kotlinx.coroutines.launch

@Composable
fun MapController() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val viewModel: MapViewModel = hiltViewModel()

    val sendEvent: (MapContract.Event) -> Unit = {
        scope.launch { viewModel.event.send(it) }
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

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.action.collect { action ->
                when (action) {
                    is MapContract.Action.MoveToLocation -> {
                        mapView.redirectToLocation(action.point)
                    }

                    is MapContract.Action.ShowToast -> context.showToast(action.msg)
                }
            }
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
    }
}