package com.thenoughtfox.orasulmeu.ui.create_post.camera

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.thenoughtfox.orasulmeu.databinding.FragmentCameraBinding
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostContract.Event
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.utils.applyBottomInsetMargin
import com.thenoughtfox.orasulmeu.utils.getVibrator
import com.thenoughtfox.orasulmeu.utils.vibrate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private val binding: FragmentCameraBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: CreatePostViewModel by activityViewModels()

    // Registers a photo picker activity launcher in multi-select mode.
    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            if (uris.isEmpty()) return@registerForActivityResult
            lifecycleScope.launch {
                viewModel.event.send(Event.PickImages(uris))
                viewModel.event.send(Event.BackToMediaPage)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCameraView()
        bindView()
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
            cameraView.takePicture()
        }
    }

    private fun setupCameraView() = binding.cameraView.apply {
        setLifecycleOwner(viewLifecycleOwner)
        addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                context?.getVibrator()?.vibrate()
                val externalFileDir =
                    activity?.getExternalFilesDir(Environment.DIRECTORY_DCIM).toString()
                val fileName = externalFileDir + "/" + System.currentTimeMillis() + ".jpeg"

                result.toFile(File(fileName)) {
                    lifecycleScope.launch {
                        viewModel.event.send(Event.PickImages(listOf(Uri.fromFile(it))))
                        viewModel.event.send(Event.BackToMediaPage)
                    }
                }
            }
        })
    }
}