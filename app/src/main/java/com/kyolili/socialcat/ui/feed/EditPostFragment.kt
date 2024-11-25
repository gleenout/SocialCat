package com.kyolili.socialcat.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.FragmentEditPostBinding

class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var postId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = arguments?.getString("postId") ?: ""
        val currentDescription = arguments?.getString("description").orEmpty()

        binding.editDescription.setText(currentDescription)

        binding.savePostButton.setOnClickListener { updatePost() }
    }

    private fun updatePost() {
        val updatedDescription = binding.editDescription.text.toString()

        firestore.collection("posts").document(postId)
            .update("description", updatedDescription)
            .addOnSuccessListener {
                Toast.makeText(context, "Post atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed() // Voltar para o fragmento anterior
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao atualizar post", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
