package com.CollegeCircle.clg.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.CollegeCircle.clg.Models.Post
import com.CollegeCircle.clg.R
import kotlinx.android.synthetic.main.postlist.view.*

class PostListAdapter(private var context: Context, private var postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostListAdapter.PostVewHolder>() {
    class PostVewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostVewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.postlist, parent, false)
        return PostVewHolder(view)
    }

    override fun onBindViewHolder(holder: PostVewHolder, position: Int) {
        var post = postList[position]
        Glide.with(context)
            .load(post.image)
            .override(1000, 1000)
            .placeholder(R.drawable.user)
            .into(holder.itemView.postListShow)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}