package com.thenoughtfox.orasulmeu.ui.screens.home.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.mapbox.geojson.Point
import com.thenoughtfox.orasulmeu.service.LocationClient
import com.thenoughtfox.orasulmeu.ui.screens.create_post.map.view.MapboxMapView
import com.thenoughtfox.orasulmeu.utils.showToast
import com.thenoughtfox.orasulmeu.utils.toPoint
import kotlinx.coroutines.launch

@Composable
fun MapController() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val vm: MapViewModel = hiltViewModel()

    val sendEvent: (MapContract.Event) -> Unit = {
        scope.launch {
            vm.event.send(it)
        }
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

    var locationToGo: Point? by remember { mutableStateOf(null) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.action.collect { action ->
                when (action) {
                    is MapContract.Action.MoveToLocation -> {
                        locationToGo = action.point
                    }

                    is MapContract.Action.ShowToast -> context.showToast(action.msg)
                }
            }
        }
    }

    AndroidView(
        factory = {
            MapboxMapView(it).apply {

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
    ) { map ->
        locationToGo?.let {
            map.redirectToLocation(it)
            locationToGo = null
        }
    }
}