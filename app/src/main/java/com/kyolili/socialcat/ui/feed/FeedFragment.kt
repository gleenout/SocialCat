package com.kyolili.socialcat.ui.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kyolili.socialcat.R
import com.kyolili.socialcat.adapter.FeedAdapter
import com.kyolili.socialcat.databinding.FragmentFeedBinding
import com.kyolili.socialcat.model.Post

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Simulação de posts fictícios
        val posts = listOf(
            Post("Ana C.", R.drawable.ic_profile, R.drawable.ic_launcher_foreground, "Lorem ipsum dolor sit amet.", "1hr ago"),
            Post("Bruna S.", R.drawable.ic_profile, R.drawable.ic_launcher_foreground, "Lorem ipsum dolor sit amet.", "2hr ago"),
            Post("Joyce C.", R.drawable.ic_profile, R.drawable.ic_launcher_foreground, "Lorem ipsum dolor sit amet.", "3hr ago"),
            Post("Adam W.", R.drawable.ic_profile, R.drawable.ic_launcher_foreground, "Lorem ipsum dolor sit amet.", "4hr ago"),
            Post("Carol A.", R.drawable.ic_profile, R.drawable.ic_launcher_foreground, "Lorem ipsum dolor sit amet.", "1hr ago"),
            Post("Sauro B.", R.drawable.ic_profile, R.drawable.ic_launcher_foreground, "Lorem ipsum dolor sit amet.", "2hr ago"),
            Post("Carlos J.", R.drawable.ic_profile, R.drawable.ic_launcher_foreground, "Lorem ipsum dolor sit amet.", "3hr ago"),
            Post("Weslley A.", R.drawable.ic_profile, R.drawable.ic_launcher_foreground, "Lorem ipsum dolor sit amet.", "4hr ago")
        )

        // Configurar o RecyclerView com GridLayoutManager (2 colunas)
        val adapter = FeedAdapter(posts) { post ->
            // Navegar para o fragment de detalhes ao clicar no post
            val bundle = Bundle().apply {
                putString("username", post.username)
                putString("description", post.description)
                putString("time", post.time)
                putInt("imageResId", post.postImage)
            }
            findNavController().navigate(R.id.action_navigation_home_to_postDetailFragment, bundle)
        }

        binding.recyclerViewFeed.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewFeed.adapter = adapter

        // Verificar se o RecyclerView está sendo chamado
        Log.d("FeedFragment", "RecyclerView configurado")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
