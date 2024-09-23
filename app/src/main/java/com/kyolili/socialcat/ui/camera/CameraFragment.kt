package com.kyolili.socialcat.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.FragmentCameraBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashEnabled = false
    private var camera: Camera? = null
    private lateinit var imageCapture: ImageCapture

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkAndRequestPermissions()

        // Alternar entre câmeras (frontal e traseira)
        binding.btnSwitchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA

            cameraProviderFuture.get().unbindAll()
            bindCameraUseCases(cameraProviderFuture.get())
        }

        // Ativar/desativar flash
        binding.btnFlash.setOnClickListener {
            flashEnabled = !flashEnabled
            camera?.cameraControl?.enableTorch(flashEnabled)
        }

        // Capturar foto
        binding.btnCapture.setOnClickListener {
            takePhoto()
        }

        // Fechar a câmera
        binding.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun checkAndRequestPermissions() {
        // Verifica se a permissão da câmera foi concedida
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Se a permissão já foi concedida, iniciar a câmera
            startCamera()
        } else {
            // Solicitar permissão da câmera
            requestCameraPermission()
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindCameraUseCases(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()

        try {
            // Desvincula todos os casos de uso antes de vincular novamente
            cameraProvider.unbindAll()

            // Vincular a visualização e captura de imagem ao ciclo de vida
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture
            )

        } catch (exc: Exception) {
            Log.e("CameraX", "Use case binding failed", exc)
        }
    }

    private fun takePhoto() {
        // Criar arquivo para armazenar a imagem capturada
        val photoFile = File(
            requireActivity().externalMediaDirs.first(),
            "${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraX", "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(requireContext(), "Falha ao capturar a foto.", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = photoFile.absolutePath // Obter o caminho da imagem como String
                    Log.d("CameraX", "Photo capture succeeded: $savedUri")

                    // Passar o caminho da foto capturada para o PreviewFragment via Bundle
                    val bundle = Bundle().apply {
                        putString("imageUri", savedUri)
                    }
                    findNavController().navigate(R.id.previewFragment, bundle)
                }
            })
    }



    private fun requestCameraPermission() {
        // Solicitar permissão da câmera
        requestPermissions(arrayOf(Manifest.permission.CAMERA), 101)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Se a permissão foi concedida, iniciar a câmera
                startCamera()
            } else {
                // Se a permissão foi negada, exibir mensagem e voltar para o feed
                Toast.makeText(requireContext(), "Permissão da câmera negada.", Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(), "Habilite nas configurações.", Toast.LENGTH_LONG).show()

                // Navegar de volta para o feed (ou qualquer outra tela que desejar)
                findNavController().navigate(R.id.navigation_home)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }
}
