package com.CollegeCircle.clg.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.CollegeCircle.clg.R
import com.CollegeCircle.clg.Activities.ContactList
import com.CollegeCircle.clg.Activities.FirstActivity
import com.CollegeCircle.clg.Activities.MessageActivityMainGroup
import com.CollegeCircle.clg.Adapter.friendAdapter
import com.CollegeCircle.clg.Models.ProfileMain
import com.CollegeCircle.clg.databinding.FragmentChattBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChattFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var binding: FragmentChattBinding
    private lateinit var database: DatabaseReference
    private lateinit var fauth: FirebaseAuth
    private var userUid: String? = null
    private var clgUid: String? = null
    private var clgName: String? = null
    private var clgImage: String? = null
    lateinit var friendList: ArrayList<ProfileMain>
    lateinit var friendAdapter: friendAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChattBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference()
        fauth = FirebaseAuth.getInstance()
        userUid = (activity as FirstActivity).userUid.toString()
        clgUid = (activity as FirstActivity).clgUid.toString()
        clgName = (activity as FirstActivity).clgName.toString()
        clgImage = (activity as FirstActivity).clgImage.toString()
        binding.contactView.setOnClickListener {
            var intent = Intent(context, ContactList::class.java)
            intent.putExtra("UsersUid", userUid)
            intent.putExtra("ClgUid", clgUid)
            startActivity(intent)
        }
        friendList = arrayListOf()
        friendAdapter =
            context?.let { friendAdapter(it, friendList, userUid.toString(), clgUid.toString()) }!!
        binding.friendsView.layoutManager = LinearLayoutManager(context)
        binding.friendsView.adapter = friendAdapter

        binding.mainCollegeName.setText(clgName)
        Glide.with(this)
            .load(clgImage)
            .override(1000, 1000)
            .circleCrop()
            .placeholder(R.drawable.user)
            .into(binding.MainClgiamge)
        binding.mainCollegeView.setOnClickListener {
            var intent = Intent(context, MessageActivityMainGroup::class.java)
            intent.putExtra("UsersUid", userUid)
            intent.putExtra("ClgUid", clgUid)
            intent.putExtra("ClgName", clgName)
            intent.putExtra("ClgImage", clgImage)
            startActivity(intent)
        }
        database.child("Colleges").child(clgUid.toString()).child("Users").child(userUid!!)
            .child("Friends").orderByChild("lastMsgTime")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    friendList.clear()
                    for (friendSnapshot in snapshot.children) {
                        var friend = friendSnapshot.getValue(ProfileMain::class.java)
                        if (friend != null) {
                            friendList.add(friend)
                        }
                    }
                    friendList.reverse()


                    friendAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        return binding.root
    }
}