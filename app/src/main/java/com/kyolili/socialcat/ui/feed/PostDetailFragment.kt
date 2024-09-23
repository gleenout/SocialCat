package com.kyolili.socialcat.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.FragmentPostDetailBinding

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private var isLiked = false // Estado inicial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recebe os dados do post
        val username = arguments?.getString("username")
        val description = arguments?.getString("description")
        val time = arguments?.getString("time")
        val imageResId = arguments?.getInt("imageResId")

        // Exibe os dados no layout
        binding.usernameTextView.text = username
        binding.postDescriptionTextView.text = description
        binding.postTimeTextView.text = time
        imageResId?.let {
            binding.postImageView.setImageResource(it)
        }

        // Configura a ação do botão de curtir
        binding.heartIcon.setOnClickListener {
            isLiked = !isLiked
            val heartIconRes = if (isLiked) R.drawable.ic_heart else R.drawable.ic_heart_border
            binding.heartIcon.setImageResource(heartIconRes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

