package com.thenoughtfox.orasulmeu.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.mapbox.geojson.Point

fun LatLng.toPoint(): Point = Point.fromLngLat(longitude, latitude)
fun Location.toPoint(): Point = Point.fromLngLat(longitude, latitude)