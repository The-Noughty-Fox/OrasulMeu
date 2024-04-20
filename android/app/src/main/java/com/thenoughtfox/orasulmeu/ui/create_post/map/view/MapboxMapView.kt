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
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.databinding.FragmentAnnotationItemBinding

class MapboxMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MapView(context, attrs) {

    private var onLoadMap: (() -> Unit)? = null
    private var onCameraTrackingDismissed: (() -> Unit)? = null
    private var style: String = Style.LIGHT

    companion object {
        private const val ZOOM_LEVEL = 15.0
        private const val MOVE_CAMERA_DURATION = 3_000L
    }

    fun onLoadMap(onLoadMap: () -> Unit) {
        this.onLoadMap = onLoadMap
    }

    fun onCameraTrackingDismissed(onCameraTrackingDismissed: () -> Unit) {
        this.onCameraTrackingDismissed = onCameraTrackingDismissed
    }

    init {
        compass.enabled = false
        scalebar.enabled = false

        mapboxMap.loadStyle(style) {
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

    fun showAnnotations(centerPoint: Point?) {
        viewAnnotationManager.removeAllViewAnnotations()
        if (centerPoint == null) {
            return
        }

        // Define the view annotation
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            // Specify the layout resource id
            resId = R.layout.fragment_annotation_item,
            // Set any view annotation options
            options = viewAnnotationOptions {
                geometry(centerPoint)
                allowOverlapWithPuck(true)
                annotationAnchor {
                    anchor(ViewAnnotationAnchor.BOTTOM)
                }
            }
        )

        FragmentAnnotationItemBinding.bind(viewAnnotation).apply {
            imageView.setImageResource(R.drawable.ic_company_logo)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gestures.removeOnMoveListener(onMoveListener)
    }
}