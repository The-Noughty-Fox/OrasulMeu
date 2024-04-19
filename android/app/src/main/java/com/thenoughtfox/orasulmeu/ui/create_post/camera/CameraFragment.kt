package com.thenoughtfox.orasulmeu.ui.create_post.camera

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.databinding.FragmentCameraBinding
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.create_post.Event
import com.thenoughtfox.orasulmeu.ui.create_post.camera.utils.CameraInitializer
import com.thenoughtfox.orasulmeu.ui.create_post.camera.utils.PerformImageCaptureUseCase
import com.thenoughtfox.orasulmeu.utils.applyBottomInsetMargin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private val binding: FragmentCameraBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: CreatePostViewModel by activityViewModels()

    @Inject
    lateinit var cameraInitializer: CameraInitializer

    @Inject
    lateinit var performImageCaptureUseCase: PerformImageCaptureUseCase

    // Registers a photo picker activity launcher in multi-select mode.
    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            if (uris.isEmpty()) return@registerForActivityResult
            lifecycleScope.launch {
                viewModel.event.send(Event.PickImages(uris))
                viewModel.event.send(Event.BackToMediaPage)
            }
        }

    private val cameraPermissionRequester : PermissionsRequester by lazy {
        constructPermissionsRequest(
            permissions = arrayOf(Manifest.permission.CAMERA),
            requiresPermission = { startCamera() },
            onNeverAskAgain = { askUserToOpenSettings() },
            onPermissionDenied = { askUserToOpenSettings() }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
        cameraPermissionRequester.launch()
    }

    private fun bindView() = binding.apply {
        buttonLayout.applyBottomInsetMargin()
        imageViewGallery.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        imageViewNext.setOnClickListener {
            lifecycleScope.launch {
                viewModel.event.send(Event.BackToMediaPage)
            }
        }

        imageViewShutter.setOnClickListener {
            lifecycleScope.launch {
                performImageCaptureUseCase()?.let { successUri ->
                    viewModel.event.send(Event.PickImages(listOf(successUri)))
                    viewModel.event.send(Event.BackToMediaPage)
                }
            }
        }
    }

    private fun startCamera() {
        cameraInitializer.initCamera(
            previewView = binding.cameraPreview,
            lifecycleOwner = viewLifecycleOwner
        )
    }

    private fun askUserToOpenSettings() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.camera_permission_denied_alert_title)
            setMessage(R.string.camera_permission_denied_aler_message)
            setPositiveButton(R.string.camera_permission_denied_go_to_settings_button_text) { _, _ -> startAppSettings() }
            setNegativeButton(R.string.camera_permission_denied_alert_cancel_button) { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun startAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        requireContext().startActivity(intent)
    }
}