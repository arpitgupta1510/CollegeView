package com.example.clg.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clg.Activities.FriendsChat
//import com.example.clg.Activities.CommentActivity
import com.example.clg.Models.Post
import com.example.clg.Models.ProfileMain
import com.example.clg.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.grpfriendlist.view.*
import kotlinx.android.synthetic.main.post.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class PostsAdapter(
    private var context: Context,
    var postList: ArrayList<Post>,
    private var usersUid: String,
    private var clgUid: String
) : RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {
    class PostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.post, parent, false)
        return PostsViewHolder(view)
    }
    private var const: Long = 1
    private var date: DateFormat = SimpleDateFormat("hh:mm a")
    private var date2: DateFormat = SimpleDateFormat("dd MMM")
    private lateinit var database: DatabaseReference
    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        var post = postList[position]
        holder.itemView.apply {
            database = FirebaseDatabase.getInstance().getReference()
            database.child("Colleges").child(clgUid).child("Posts").child(post.postsUid.toString())
                .child("LikedBy")
                .child(usersUid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            holder.itemView.postLikesBtn.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.like_red,
                                0,
                                0,
                                0
                            )
                            holder.itemView.postLikesBtn.setText(post.likes.toString())
                            holder.itemView.postLikesBtn.setOnClickListener {
                                holder.itemView.postLikesBtn.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.unlike_white,
                                    0,
                                    0,
                                    0
                                )
                                database.child("Colleges").child(clgUid).child("Posts")
                                    .child(post.postsUid.toString()).child("LikedBy")
                                    .child(usersUid).setValue(null).addOnSuccessListener {
                                        database.child("Colleges").child(clgUid).child("Posts")
                                            .child(post.postsUid.toString()).child("likes")
                                            .setValue(post.likes - 1)
                                        Toast.makeText(context, "UNLIKED", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                        } else {
                            holder.itemView.postLikesBtn.setText(post.likes.toString())
                            holder.itemView.postLikesBtn.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.unlike_white,
                                0,
                                0,
                                0
                            )
                            holder.itemView.postLikesBtn.setOnClickListener {
                                holder.itemView.postLikesBtn.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.like_red,
                                    0,
                                    0,
                                    0
                                )
                                database.child("Colleges").child(clgUid).child("Posts")
                                    .child(post.postsUid.toString()).child("LikedBy")
                                    .child(usersUid).setValue(true).addOnSuccessListener {
                                        database.child("Colleges").child(clgUid).child("Posts")
                                            .child(post.postsUid.toString()).child("likes")
                                            .setValue(post.likes + 1)
                                        Toast.makeText(context, "LIKED", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            Glide.with(this)
                .load(post.image)
                .override(1000, 1000)
                .placeholder(R.drawable.user)
                .into(postImageShow)
            postCommentbtn.setText(post.comment.toString())
            var time1 = System.currentTimeMillis() / 86400000
            var time2 = post.date.toString().toLong() / 86400000
            if (time1 != time2) {
                if (time1 - time2 == const)
                    postUploadTime.setText("Yesterday")
                else {
                    postUploadTime.setText(
                        date2.format(
                            post.date.toString().toLong()
                        ).toString()
                    )
                }
            } else {
                postUploadTime.setText(
                    date.format(
                        post.date.toString().toLong()
                    ).toString()
                )
            }
            if (post.postCaption != null) {
                postCaptionshow.visibility = View.VISIBLE
                postCaptionshow.setText(post.postCaption.toString())
            }
            database.child("Profile").child(post.uploaderId.toString()).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var user=snapshot.getValue(ProfileMain::class.java)
                    if (user != null) {
                        Glide.with(context)
                            .load(user.image)
                            .override(1000, 1000)
                            .circleCrop()
                            .placeholder(R.drawable.user)
                            .into(holder.itemView.postUploaderImage)
                            holder.itemView.postUploaderName.setText(user.name)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
//            postCommentbtn.setOnClickListener {
//                var intent = Intent(context, CommentActivity::class.java)
//                intent.putExtra("FriendUid",post.uploaderId)
//                intent.putExtra("UsersUid", usersUid)
//                intent.putExtra("ClgUid", clgUid)
//                intent.putExtra("PostUid", post.postsUid)
//                context.startActivity(intent)
//            }
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}