package com.thenoughtfox.orasulmeu.ui.create_post.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            lifecycleScope.launch {
                viewModel.event.send(Event.NavigateToPlayer(location))
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
                    binding.layoutPin.isVisible = true
                    locationClient.getLastLocation()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.buttonNext.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                RoundButton(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(id = R.string.map_button_next),
                    backgroundColor = colorResource(id = R.color.button_next_color),
                    textColor = colorResource(id = R.color.black),
                    onClick = {
                        lifecycleScope.launch {
                            viewModel.event.send(Event.TappedNext)
                        }
                    })
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
        setupMap()
        setupRecyclerView()
        subscribeObservables()
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

    private fun setupMap() = binding.apply {
        mapView.apply {
            onLoadMap {
                locationRequester.launch()
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