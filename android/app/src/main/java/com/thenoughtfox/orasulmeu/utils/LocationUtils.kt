package com.thenoughtfox.orasulmeu.utils

import com.google.android.gms.maps.model.LatLng
import com.mapbox.geojson.Point

fun getPoint(latLng: LatLng): Point = Point.fromLngLat(latLng.longitude, latLng.latitude)