package com.thenoughtfox.orasulmeu.ui.create_post.map.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar

class MapboxMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MapView(context, attrs) {

    private var onLoadMap: (() -> Unit)? = null
    private var onMapClicked: (() -> Unit)? = null
    private var onCameraTrackingDismissed: (() -> Unit)? = null

    companion object {
        private const val ZOOM_LEVEL = 15.0
        private const val MOVE_CAMERA_DURATION = 3_000L
    }

    fun onLoadMap(onLoadMap: () -> Unit) {
        this.onLoadMap = onLoadMap
    }

    fun onMapClicked(onMapClicked: () -> Unit) {
        this.onMapClicked = onMapClicked
    }

    fun onCameraTrackingDismissed(onCameraTrackingDismissed: () -> Unit) {
        this.onCameraTrackingDismissed = onCameraTrackingDismissed
    }

    init {
        compass.enabled = false
        scalebar.enabled = false

        mapboxMap.loadStyle(Style.LIGHT) {
            initLocationComponent()
            setupGesturesListener()
            onLoadMap?.invoke()

            style {
            }
        }
    }

    private fun initLocationComponent() = with(location) {
        updateSettings {
            enabled = true
            pulsingEnabled = true
        }
    }

    private fun setupGesturesListener() {
        gestures.addOnMoveListener(onMoveListener)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {
            onCameraTrackingDismissed?.invoke()
        }
    }

    fun redirectToLocation(
        point: Point,
        zoomLevel: Double? = null,
        isSmoothing: Boolean = false
    ) {
        val cameraOptions = CameraOptions
            .Builder()
            .center(point)
            .zoom(zoomLevel ?: ZOOM_LEVEL)
            .build()

        if (isSmoothing) {
            mapboxMap.flyTo(cameraOptions,
                mapAnimationOptions {
                    duration(MOVE_CAMERA_DURATION)
                })
        } else {
            mapboxMap.setCamera(cameraOptions)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gestures.removeOnMoveListener(onMoveListener)
    }
}