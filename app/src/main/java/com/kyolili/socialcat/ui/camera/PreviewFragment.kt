package com.kyolili.socialcat.ui.camera

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kyolili.socialcat.databinding.FragmentPreviewBinding

class PreviewFragment : Fragment() {

    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

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

        // Exibir a imagem capturada
        binding.capturedImageView.setImageURI(Uri.parse(imagePath))

        // Ação do botão de postar
        binding.postButton.setOnClickListener {
            // Lógica para postar a imagem (por exemplo, salvar no servidor)
            // Simulação de postagem
            requireActivity().onBackPressed() // Voltar ao feed após a postagem
        }

        // Ação do botão de cancelar
        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressed() // Voltar ao feed sem postar
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
