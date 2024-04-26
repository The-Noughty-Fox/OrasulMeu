package com.thenoughtfox.orasulmeu.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.maps.model.LatLng
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.databinding.FragmentMapBinding
import com.thenoughtfox.orasulmeu.service.LocationClient
import com.thenoughtfox.orasulmeu.ui.create_post.map.view.MapboxMapView.Place
import com.thenoughtfox.orasulmeu.ui.create_post.map.view.PinUtils
import com.thenoughtfox.orasulmeu.ui.map.MapContract.Action
import com.thenoughtfox.orasulmeu.ui.map.MapContract.Event
import com.thenoughtfox.orasulmeu.utils.applyBottomInsetMargin
import com.thenoughtfox.orasulmeu.utils.showSettingsDialog
import com.thenoughtfox.orasulmeu.utils.showToast
import com.thenoughtfox.orasulmeu.utils.toPoint
import kotlinx.coroutines.launch
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructLocationPermissionRequest

class MapFragment : Fragment() {

    private val binding: FragmentMapBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: MapViewModel by viewModels()
    private val locationClient: LocationClient by lazy {
        LocationClient(requireContext()) { location ->
            lifecycleScope.launch {
                viewModel.event.send(Event.NavigateToPlayer(location.toPoint()))

                val places = mutableListOf<Place>().apply {
                    val bitmap1 =
                        PinUtils.maskDrawableToAnother(
                            context = requireContext(),
                            sourceResId = R.drawable.logo,
                            maskResId = R.drawable.ic_pin_annotation
                        )

                    val bitmap2 =
                        PinUtils.maskDrawableToAnother(
                            context = requireContext(),
                            sourceResId = R.drawable.ic_company_logo,
                            maskResId = R.drawable.ic_pin_annotation
                        )


                    val bitmap3 =
                        PinUtils.maskDrawableToAnother(
                            context = requireContext(),
                            sourceResId = R.drawable.image_placeholder,
                            maskResId = R.drawable.ic_pin_annotation
                        )


                    add(Place(point = LatLng(47.024378, 28.832066).toPoint(), bitmap = bitmap1))
                    add(Place(point = LatLng(47.025879, 28.835292).toPoint(), bitmap = bitmap2))
                    add(Place(point = LatLng(47.023647, 28.833826).toPoint(), bitmap = bitmap3))
                }

                binding.mapView.addPlaces(places)
            }
        }
    }

    private val locationRequester: PermissionsRequester by lazy {
        constructLocationPermissionRequest(
            LocationPermission.FINE,
            LocationPermission.COARSE,
            onShowRationale = {},
            onPermissionDenied = {
                context?.showToast("Please provide location permissions")
            },
            onNeverAskAgain = { activity?.let { showSettingsDialog(it) } },
            requiresPermission = {
                lifecycleScope.launch {
                    locationClient.getLastLocation()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservables()
        bindView()
        setupMap()
    }

    private fun bindView() = binding.apply {
        containerBadge.applyBottomInsetMargin()
        imageViewFindMe.setOnClickListener {
            locationRequester.launch()
        }
    }

    private fun setupMap() = binding.apply {
        mapView.apply {
            onLoadMap {
                locationRequester.launch()
            }
        }
    }

    private fun subscribeObservables() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.action.collect { action ->
                    when (action) {
                        is Action.MoveToLocation -> {
                            binding.mapView.redirectToLocation(action.point)
                        }

                        is Action.ShowToast -> context?.showToast(action.msg)
                    }
                }
            }
        }
    }

}