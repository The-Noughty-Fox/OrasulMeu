package com.thenoughtfox.orasulmeu.ui.create_post.map

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mapbox.search.ReverseGeoOptions
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.databinding.FragmentMapSearchBinding
import com.thenoughtfox.orasulmeu.service.LocationClient
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostContract
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.create_post.media.RoundButton
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import com.thenoughtfox.orasulmeu.utils.applyBottomInsetMargin
import com.thenoughtfox.orasulmeu.utils.applyTopStatusInsetMargin
import com.thenoughtfox.orasulmeu.utils.hideKeyboard
import com.thenoughtfox.orasulmeu.utils.showSettingsDialog
import com.thenoughtfox.orasulmeu.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructLocationPermissionRequest

@AndroidEntryPoint
class MapSearchFragment : Fragment() {

    private val binding: FragmentMapSearchBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: MapSearchViewModel by viewModels()
    private val createPostViewModel: CreatePostViewModel by activityViewModels()
    private val adapter: SearchSuggestionAdapter by lazy { SearchSuggestionAdapter() }
    private val locationClient: LocationClient by lazy {
        LocationClient(requireContext()) { location ->
            if (view != null) {
                lifecycleScope.launch {
                    viewModel.event.send(Event.NavigateToPlayer(location))
                }
            }
        }
    }

    private var locationResult: ActivityResultLauncher<IntentSenderRequest>? = null
    private var locationRequester: PermissionsRequester? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        constructLocationPermissionRequest()
        initLocationResultLauncher()
        setupComposeButtonNext()
        bindView()
        setupMap()
        setupRecyclerView()
        subscribeObservables()
    }

    private fun constructLocationPermissionRequest() {
        locationRequester = constructLocationPermissionRequest(
            LocationPermission.FINE,
            LocationPermission.COARSE,
            onShowRationale = {},
            onPermissionDenied = {
                context?.showToast("Please provide location permissions")
            },
            onNeverAskAgain = { activity?.let { showSettingsDialog(it) } },
            requiresPermission = {
                lifecycleScope.launch {
                    if (view != null) {
                        binding.layoutPin.isVisible = true
                        locationClient.startLocationRequest(
                            onSuccess = { locationClient.getLastLocation() },
                            onFailure = { e ->
                                val intentSenderRequest =
                                    IntentSenderRequest.Builder(e.resolution).build()
                                locationResult?.launch(intentSenderRequest)
                            }
                        )
                    }
                }
            })
    }

    private fun initLocationResultLauncher() {
        locationResult =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (locationClient.isLocationPermissionGranted()) {
                        locationClient.getLastLocation()
                    } else {
                        locationRequester?.launch()
                    }
                } else {
                    context?.showToast("Please enable location")
                }
            }
    }

    private fun bindView() = binding.apply {
        containerSearch.applyTopStatusInsetMargin()
        buttonNext.applyBottomInsetMargin()
        adapter.setOnItemClicked {
            editTextSearch.apply {
                text.clear()
                clearFocus()
            }

            lifecycleScope.launch { viewModel.event.send(Event.OnSearchSuggestionClicked(it)) }
        }

        editTextSearch.doOnTextChanged { text, _, _, _ ->
            lifecycleScope.launch {
                viewModel.event.send(Event.DoOnTextLocationChanged(text.toString()))
            }
        }

        imageViewClose.setOnClickListener {
            editTextSearch.apply {
                text.clear()
                clearFocus()
                hideKeyboard()
            }
        }
    }

    private fun setupComposeButtonNext() = binding.buttonNext.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            RoundButton(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = stringResource(id = R.string.map_button_next),
                backgroundColor = OrasulMeuTheme.colors.buttonNextBackground,
                textColor = colorResource(id = R.color.black),
                onClick = {
                    lifecycleScope.launch {
                        viewModel.event.send(Event.TappedNext)
                    }
                })
        }
    }

    private fun setupMap() = binding.apply {
        mapView.apply {
            onLoadMap {
                locationClient.startLocationRequest(
                    onSuccess = { locationRequester?.launch() },
                    onFailure = { e ->
                        val intentSenderRequest = IntentSenderRequest.Builder(e.resolution).build()
                        locationResult?.launch(intentSenderRequest)
                    }
                )
            }

            onCameraTrackingDismissed {
                lifecycleScope.launch {
                    viewModel.event.send(
                        Event.OnCameraTrackingDismissed(
                            ReverseGeoOptions(center = mapboxMap.cameraState.center)
                        )
                    )
                }
            }
        }
    }

    private fun setupRecyclerView() = binding.apply {
        recyclerViewLocation.adapter = adapter
    }

    private fun subscribeObservables() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.state.collect { state ->
                    adapter.submitList(state.suggestions)
                    binding.recyclerViewLocation.isVisible = state.isSuggestionListShow
                    binding.layoutPin.isVisible = !state.isSuggestionListShow
                    binding.textViewStreetName.text = state.address
                    binding.containerPinStreetName.visibility =
                        if (state.address.isNotEmpty()) View.VISIBLE else View.INVISIBLE

                    sendStreetName(state.address)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.action.collect { action ->
                    when (action) {
                        is Action.MoveToLocation -> {
                            view?.hideKeyboard()
                            binding.mapView.redirectToLocation(action.point)
                        }

                        is Action.ShowToast -> context?.showToast(action.msg)
                    }
                }
            }
        }
    }

    private fun sendStreetName(address: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            createPostViewModel.event.send(CreatePostContract.Event.SetAddress(address))
        }
    }
}