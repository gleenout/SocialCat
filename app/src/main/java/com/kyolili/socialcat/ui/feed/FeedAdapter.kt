package com.kyolili.socialcat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.ItemFeedBinding
import com.kyolili.socialcat.model.Post

class FeedAdapter(
    private val posts: List<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
        holder.itemView.setOnClickListener {
            onItemClick(post)
        }

        // Alternar ícone de curtir
        holder.binding.heartIcon.setOnClickListener {
            post.isLiked = !post.isLiked
            val heartIconRes = if (post.isLiked) R.drawable.ic_heart else R.drawable.ic_heart_border
            holder.binding.heartIcon.setImageResource(heartIconRes)
        }
    }

    override fun getItemCount() = posts.size

    class FeedViewHolder(val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.profileImage.setImageResource(post.profileImage)
            binding.username.text = post.username
            binding.postImage.setImageResource(post.postImage)
            binding.postDescription.text = post.description
            binding.postTime.text = post.time

            // Atualiza o ícone de curtir com base no estado atual
            val heartIconRes = if (post.isLiked) R.drawable.ic_heart else R.drawable.ic_heart_border
            binding.heartIcon.setImageResource(heartIconRes)
        }
    }
}
