package com.kyolili.socialcat.ui.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.FragmentFeedBinding
import com.kyolili.socialcat.model.Post
import com.kyolili.socialcat.adapter.FeedAdapter

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val posts = mutableListOf<Post>()
    private lateinit var adapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchPostsFromFirestore()
    }

    private fun setupRecyclerView() {
        adapter = FeedAdapter(posts) { post ->
            navigateToPostDetails(post)
        }

        binding.recyclerViewFeed.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewFeed.adapter = adapter
    }

    private fun navigateToPostDetails(post: Post) {
        try {
            val bundle = Bundle().apply {
                putString("postId", post.id)
                putString("username", post.username)
                putString("description", post.description)
                putString("time", post.time)
                putString("imageUri", post.postImage)
                putInt("likes", post.likes)
            }
            findNavController().navigate(R.id.action_navigation_home_to_postDetailFragment, bundle)
        } catch (e: Exception) {
            Log.e("FeedFragment", "Erro ao navegar para os detalhes do post", e)
        }
    }

    private fun fetchPostsFromFirestore() {
        firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                processFetchedPosts(querySnapshot.documents)
            }
            .addOnFailureListener { exception ->
                Log.e("FeedFragment", "Erro ao buscar posts", exception)
            }
    }

    private fun processFetchedPosts(documents: List<com.google.firebase.firestore.DocumentSnapshot>) {
        try {
            posts.clear()
            for (document in documents) {
                val post = Post(
                    id = document.id,
                    username = document.getString("username") ?: "Usuário",
                    profileImage = R.drawable.ic_person,
                    postImage = document.getString("imageUri").orEmpty(),
                    description = document.getString("description").orEmpty(),
                    time = formatTimestamp(document.getLong("timestamp") ?: System.currentTimeMillis()),
                    likes = (document.get("likes") as? Long ?: 0).toInt(),
                    likedBy = document.get("likedBy") as? List<String> ?: listOf()
                )
                posts.add(post)
            }
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Log.e("FeedFragment", "Erro ao processar posts", e)
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - timestamp
        return when {
            diff < 60000 -> "Agora"
            diff < 3600000 -> "${diff / 60000}m"
            diff < 86400000 -> "${diff / 3600000}h"
            else -> "${diff / 86400000}d"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
