package com.kyolili.socialcat.ui.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.FragmentPostDetailBinding
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var postId: String
    private var isLiked = false
    private var likesCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // Recebe os dados do post usando argumentos
            postId = arguments?.getString("postId") ?: ""
            val username = arguments?.getString("username") ?: "Usuário"
            val description = arguments?.getString("description").orEmpty()
            val time = arguments?.getString("time").orEmpty()
            val imageUri = arguments?.getString("imageUri")
            likesCount = arguments?.getInt("likes") ?: 0

            // Log para debug
            Log.d("PostDetailFragment", "Dados recebidos: postId=$postId, username=$username, description=$description, time=$time, imageUri=$imageUri, likes=$likesCount")

            // Exibe os dados no layout
            binding.usernameTextView.text = username
            binding.postDescriptionTextView.text = description
            binding.postTimeTextView.text = time
            binding.likesCountTextView.text = likesCount.toString()

            // Carregar imagem com Glide e tratamento de erro
            imageUri?.let {
                Glide.with(requireContext())
                    .load(it)
                    .error(R.drawable.ic_person)
                    .placeholder(R.drawable.ic_person)
                    .into(binding.postImageView)
            } ?: run {
                // Caso não tenha imageUri, mostra imagem padrão
                binding.postImageView.setImageResource(R.drawable.ic_person)
            }

            // Verifica se o usuário já curtiu o post
            checkLikeStatus()

            // Configura a ação do botão de curtir
            binding.heartIcon.setOnClickListener {
                handleLikeAction()
            }

            binding.optionsMenuIcon.setOnClickListener { view ->
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.menuInflater.inflate(R.menu.post_options_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.edit_post -> {
                            navigateToEditPost(postId)
                            true
                        }
                        R.id.delete_post -> {
                            deletePost(postId)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }

        } catch (e: Exception) {
            Log.e("PostDetailFragment", "Erro ao carregar detalhes do post", e)
            Toast.makeText(context, "Erro ao carregar detalhes do post", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLikeStatus() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            Log.d("PostDetailFragment", "Usuário não logado")
            return
        }

        firestore.collection("posts").document(postId)
            .get()
            .addOnSuccessListener { document ->
                val likedBy = document.get("likedBy") as? List<String> ?: listOf()
                isLiked = likedBy.contains(currentUserId)
                likesCount = likedBy.size
                updateLikeUI()
            }
            .addOnFailureListener { e ->
                Log.e("PostDetailFragment", "Erro ao verificar status de like", e)
            }
    }

    private fun handleLikeAction() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(context, "Faça login para curtir", Toast.LENGTH_SHORT).show()
            return
        }

        val postRef = firestore.collection("posts").document(postId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)

            // Get current likes list
            val likedBy = snapshot.get("likedBy") as? MutableList<String> ?: mutableListOf()

            // Toggle like
            if (likedBy.contains(currentUserId)) {
                // Unlike
                likedBy.remove(currentUserId)
                transaction.update(postRef, "likedBy", likedBy)
                transaction.update(postRef, "likes", likedBy.size)
                isLiked = false
                likesCount = likedBy.size
            } else {
                // Like
                likedBy.add(currentUserId)
                transaction.update(postRef, "likedBy", likedBy)
                transaction.update(postRef, "likes", likedBy.size)
                isLiked = true
                likesCount = likedBy.size
            }
        }.addOnSuccessListener {
            updateLikeUI()
            Log.d("PostDetailFragment", "Like action successful")
        }.addOnFailureListener { e ->
            Log.e("PostDetailFragment", "Erro na ação de like", e)
            Toast.makeText(context, "Erro ao processar like", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLikeUI() {
        // Atualiza o ícone de coração
        val heartIconRes = if (isLiked) R.drawable.ic_heart else R.drawable.ic_heart_border
        binding.heartIcon.setImageResource(heartIconRes)

        // Atualiza o número de likes
        binding.likesCountTextView.text = likesCount.toString()
    }

    private fun navigateToEditPost(postId: String) {
        val bundle = Bundle().apply {
            putString("postId", postId)
        }
        findNavController().navigate(R.id.action_postDetailFragment_to_editPostFragment, bundle)
    }

    private fun deletePost(postId: String) {
        firestore.collection("posts").document(postId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Post excluído com sucesso", Toast.LENGTH_SHORT).show()
                // Voltar para a tela anterior ou atualizar a lista
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                Log.e("PostDetailFragment", "Erro ao excluir post", e)
                Toast.makeText(context, "Erro ao excluir post", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}