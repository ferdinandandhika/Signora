package com.capstone.signora.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.signora.R
import com.capstone.signora.data.model.Post
import com.capstone.signora.FullScreenImageDialogFragment
import androidx.fragment.app.FragmentActivity

class PostAdapter(private val posts: List<Post>, private val activity: FragmentActivity) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.nameTextView.text = post.name
        holder.contentTextView.text = post.content

        // Load profile image using Glide
        if (post.profileImageUri.isNotEmpty()) {
            Glide.with(holder.profileImageView.context)
                .load(post.profileImageUri)
                .placeholder(R.drawable.ic_profile_placeholder)
                .circleCrop() // Apply circular crop
                .into(holder.profileImageView)
        } else {
            holder.profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
        }

        // Set click listener to open full screen image dialog
        holder.profileImageView.setOnClickListener {
            val dialog = FullScreenImageDialogFragment.newInstance(post.profileImageUri)
            dialog.show(activity.supportFragmentManager, "FullScreenImageDialog")
        }
    }

    override fun getItemCount(): Int = posts.size
}
