package com.kyolili.socialcat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kyolili.socialcat.R
import com.kyolili.socialcat.databinding.ItemFeedBinding
import com.kyolili.socialcat.model.Post

class FeedAdapter(
    private val posts: List<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemFeedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)

        holder.itemView.setOnClickListener { onItemClick(post) }
        holder.binding.postImage.setOnClickListener { onItemClick(post) }
        holder.binding.heartIcon.setOnClickListener { handleLikeAction(post, holder) }
    }

    private fun handleLikeAction(post: Post, holder: FeedViewHolder) {
        val currentUserId = auth.currentUser?.uid ?: return

        val postRef = firestore.collection("posts").document(post.id)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val likedBy = snapshot["likedBy"] as? MutableList<String> ?: mutableListOf()

            if (likedBy.contains(currentUserId)) {
                likedBy.remove(currentUserId)
                transaction.update(postRef, "likedBy", likedBy)
                transaction.update(postRef, "likes", likedBy.size)
                holder.binding.heartIcon.setImageResource(R.drawable.ic_heart_border)
            } else {
                likedBy.add(currentUserId)
                transaction.update(postRef, "likedBy", likedBy)
                transaction.update(postRef, "likes", likedBy.size)
                holder.binding.heartIcon.setImageResource(R.drawable.ic_heart)
            }
        }.addOnSuccessListener {
            Log.d("FeedAdapter", "Ação de like concluída")
        }.addOnFailureListener { e ->
            Log.e("FeedAdapter", "Erro ao processar like", e)
        }
    }

    override fun getItemCount() = posts.size

    class FeedViewHolder(val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.username.text = post.username

            Glide.with(binding.root.context)
                .load(post.postImage)
                .error(R.drawable.ic_person)
                .placeholder(R.drawable.ic_person)
                .into(binding.postImage)

            binding.postDescription.text = post.description
            binding.postTime.text = post.time

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            binding.heartIcon.setImageResource(
                if (currentUserId != null && post.likedBy.contains(currentUserId)) {
                    R.drawable.ic_heart
                } else {
                    R.drawable.ic_heart_border
                }
            )

            binding.profileImage.setImageResource(post.profileImage)
        }
    }
}
