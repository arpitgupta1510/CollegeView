package com.example.clg.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clg.Activities.FirstActivity
import com.example.clg.Adapter.PostsAdapter
import com.example.clg.Models.Post
import com.example.clg.R
import com.example.clg.databinding.FragmentPostsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PostsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    private lateinit var binding: FragmentPostsBinding
    private lateinit var database: DatabaseReference
    private lateinit var fauth: FirebaseAuth
    lateinit var postLists: ArrayList<Post>
    lateinit var postAdapter: PostsAdapter
    private var userUid: String? = null
    private var clgUid: String? = null
    private var x: Long = 0
    private var isLoadedOnce:Boolean=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference
        fauth = FirebaseAuth.getInstance()
        userUid = (activity as FirstActivity).userUid.toString()
        clgUid = (activity as FirstActivity).clgUid.toString()
        Toast.makeText(context, userUid, Toast.LENGTH_SHORT).show()
        Toast.makeText(context, clgUid, Toast.LENGTH_SHORT).show()
        postLists = arrayListOf()
        postAdapter = PostsAdapter(requireContext(), postLists, userUid!!, clgUid!!)
        binding.postsView.layoutManager = LinearLayoutManager(context)
        binding.postsView.adapter = postAdapter
        database.child("Colleges").child(clgUid!!).child("Posts").limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount == x) {
                        Toast.makeText(context, "No Post Available", Toast.LENGTH_SHORT).show()
                    } else {
                        postLists.clear()
                        for (postSnapshot in snapshot.children) {
                            val post = postSnapshot.getValue(Post::class.java)
                            if (post != null) {
                                postLists.add(post)
                            }
                        }
                        postLists.reverse()
                        if(!isLoadedOnce){

                            isLoadedOnce=true
                        }

                        postAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_post, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.starredPosts->
            {
                Toast.makeText(context, "Starred posts", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        isLoadedOnce=false
    }


}