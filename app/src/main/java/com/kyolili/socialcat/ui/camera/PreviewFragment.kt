package com.kyolili.socialcat.ui.camera

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.kyolili.socialcat.databinding.FragmentPreviewBinding

class PreviewFragment : Fragment() {

    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Receber o caminho da imagem capturada
        val imagePath = arguments?.getString("imageUri") ?: return

        // Usar Glide para carregar a imagem
        Glide.with(this)
            .load(Uri.parse(imagePath))
            .into(binding.capturedImageView)

        // Ação do botão de postar
        binding.postButton.setOnClickListener {
            // Capturar a descrição digitada
            val description = binding.descriptionEditText.text.toString()

            // Criar um objeto de dados para enviar ao Firestore
            val postData = hashMapOf(
                "description" to description,
                "imageUri" to imagePath,
                "timestamp" to System.currentTimeMillis()
            )

            // Enviar os dados para o Firestore
            firestore.collection("posts")
                .add(postData)
                .addOnSuccessListener {
                    // Postagem bem-sucedida
                    requireActivity().onBackPressedDispatcher.onBackPressed() // Voltar ao feed após a postagem
                }
                .addOnFailureListener { e ->
                    // Tratar erro
                    e.printStackTrace()
                }
        }

        // Ação do botão de cancelar
        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // Voltar ao feed sem postar
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}