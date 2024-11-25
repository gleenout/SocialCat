package com.kyolili.socialcat.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.FragmentCameraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val TAG = "CameraFragment"

    private lateinit var cameraController: LifecycleCameraController
    private var flashMode = ImageCapture.FLASH_MODE_OFF

    // Removida a propriedade imageCapture já que estamos usando o cameraController

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            setupCamera()
        } else {
            Toast.makeText(requireContext(), "Permissão de câmera necessária", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
            setupCamera()
        } else {
            Toast.makeText(requireContext(), "Permissões de armazenamento necessárias", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val bundle = Bundle().apply {
                    putString("imageUri", uri.toString())
                }
                findNavController().navigate(R.id.previewFragment, bundle)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        setupUI()
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                setupCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun setupCamera() {
        try {
            cameraController = LifecycleCameraController(requireContext()).apply {
                imageCaptureFlashMode = flashMode
                setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            }

            binding.cameraPreview.apply {
                controller = cameraController
                implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            cameraController.bindToLifecycle(viewLifecycleOwner)

        } catch (e: Exception) {
            Log.e(TAG, "Erro ao configurar câmera: ${e.message}", e)
            Toast.makeText(requireContext(), "Erro ao inicializar câmera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        with(binding) {
            btnCapture.setOnClickListener { takePhoto() }
            btnSwitchCamera.setOnClickListener { switchCamera() }
            btnFlash.setOnClickListener { toggleFlash() }
            btnClose.setOnClickListener { closeCamera() }
            btnGallery.setOnClickListener { openGallery() }
        }
    }

    private fun takePhoto() {
        val photoFile = createImageFile()

        cameraController.takePicture(
            ImageCapture.OutputFileOptions.Builder(photoFile).build(),
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = photoFile.absolutePath
                    Log.d(TAG, "Photo capture succeeded: $savedUri")

                    val bundle = Bundle().apply {
                        putString("imageUri", savedUri)
                    }
                    findNavController().navigate(R.id.previewFragment, bundle)
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(
                        requireContext(),
                        "Falha ao capturar a foto.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        // Primeiro tenta usar o diretório de mídia externo
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        // Se não conseguir, usa o diretório de cache interno
        val fileDir = mediaDir ?: requireContext().cacheDir

        return File(fileDir, "IMG_${timeStamp}.jpg").apply {
            parentFile?.mkdirs() // Garante que o diretório pai existe
            if (exists()) {
                delete()
            }
        }
    }


    private fun switchCamera() {
        try {
            cameraController.cameraSelector =
                if (cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao trocar câmera: ${e.message}", e)
            Toast.makeText(requireContext(), "Erro ao trocar câmera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFlash() {
        try {
            flashMode = when (flashMode) {
                ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                else -> ImageCapture.FLASH_MODE_OFF
            }

            updateFlashIcon()
            cameraController.imageCaptureFlashMode = flashMode
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao alterar flash: ${e.message}", e)
            Toast.makeText(requireContext(), "Erro ao alterar flash", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFlashIcon() {
        val flashIcon = when (flashMode) {
            ImageCapture.FLASH_MODE_ON -> R.drawable.ic_flash_on
            ImageCapture.FLASH_MODE_AUTO -> R.drawable.ic_flash_auto
            else -> R.drawable.ic_flash_off
        }
        binding.btnFlash.setImageResource(flashIcon)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun closeCamera() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}