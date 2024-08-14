package com.thenoughtfox.orasulmeu.ui.screens.create_post.map.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import coil.ImageLoader
import coil.request.ImageRequest
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationSourceOptions
import com.mapbox.maps.plugin.annotation.ClusterOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.thenoughtfox.orasulmeu.ui.post.utils.PostDtoToStateMapper.toJsonElement
import com.thenoughtfox.orasulmeu.ui.post.utils.PostDtoToStateMapper.toPostDto
import com.thenoughtfox.orasulmeu.utils.generateSmallIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.openapitools.client.models.PostDto

class MapboxMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MapView(context, attrs) {

    private var onLoadMap: (() -> Unit)? = null
    private var onCameraTrackingDismissed: (() -> Unit)? = null
    private var style: String = Style.LIGHT
    private var pointAnnotationManager: PointAnnotationManager? = null

    private val parentJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + parentJob)

    private var onPostClick: ((PostDto) -> Unit)? = null

    companion object {
        private const val ZOOM_LEVEL = 15.0
        private const val MOVE_CAMERA_DURATION = 3_000L
        private const val LAYER_ID_PIN = "LayerIdPin"
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
            pointAnnotationManager = annotations.createPointAnnotationManager(
                AnnotationConfig(
                    layerId = LAYER_ID_PIN,
                    annotationSourceOptions = AnnotationSourceOptions(
                        clusterOptions = ClusterOptions(
                            circleRadius = 15.0,
                            textColor = Color.BLACK,
                            clusterRadius = 25,
                            colorLevels = listOf(Pair(0, Color.WHITE))
                        )
                    )
                )
            )

            pointAnnotationManager?.addClickListener(OnPointAnnotationClickListener { annotation ->
                val post = annotation.getData()?.toPostDto()
                    ?: return@OnPointAnnotationClickListener false

                onPostClick?.invoke(post)
                true
            })
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

    fun addPosts(posts: List<PostDto>) = scope.launch {
        val pointAnnotationOptions = posts.map { post ->
            val point = Point.fromLngLat(post.location.longitude, post.location.latitude)
            val imageUrl = post.media.firstOrNull()?.url
            val image: Drawable? = if (!imageUrl.isNullOrEmpty()) {
                val request = ImageRequest.Builder(context)
                    .data(post.media.firstOrNull()?.url ?: "")
                    .allowHardware(false)
                    .build()

                ImageLoader(context).execute(request).drawable
            } else {
                null
            }

            if (image == null) {
                null
            } else {
                val bitmap = PinUtils.maskDrawableToAnother(context, image)
                PointAnnotationOptions()
                    .withData(post.toJsonElement())
                    .withPoint(point)
                    .withIconImage(bitmap.generateSmallIcon(context, 36, 48))
            }
        }

        pointAnnotationManager?.create(pointAnnotationOptions.filterNotNull())
    }

    fun onPostClick(onPostClick: (PostDto) -> Unit) {
        this.onPostClick = onPostClick
    }

    fun clearPlaces() {
        pointAnnotationManager?.deleteAll()
    }

    override fun onDestroy() {
        gestures.removeOnMoveListener(onMoveListener)
        parentJob.cancel()
    }

    private fun Int.dpToPixels(): Int {
        val scale = context.resources.displayMetrics.density
        val pixels = (this * scale + 0.5f)
        return pixels.toInt()
    }
}