package com.CollegeCircle.clg.Activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.CollegeCircle.clg.Adapter.memberAdapter
import com.CollegeCircle.clg.Models.College
import com.CollegeCircle.clg.Models.Profile
import com.CollegeCircle.clg.Models.ProfileMain
import com.CollegeCircle.clg.R
import com.CollegeCircle.clg.databinding.ActivityCollegeProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CollegeProfile : AppCompatActivity() {
    private lateinit var binding: ActivityCollegeProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var collegeUid: String? = null
    private var college: College? = null
    private var userUid: String? = null
    private var user: Profile? = null
    private lateinit var memberAdapter: memberAdapter
    private lateinit var grpFriends: ArrayList<ProfileMain>
    private var x: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollegeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()
        userUid = fAuth.currentUser!!.uid
        collegeUid = intent.getStringExtra("ClgUid")

        database.child("Colleges").child(collegeUid.toString()).get().addOnSuccessListener {
            college = it.getValue(College::class.java)
            binding.clgName.setText(college!!.CollegeName)
            Glide.with(this)
                .load(college!!.image)
                .override(1000, 1000)
                .placeholder(R.drawable.user)
                .into(binding.collegeImageShoww)
            database.child("Profile").child(userUid.toString()).child("Groups")
                .child(collegeUid.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            binding.sendCollegeRequest.visibility = View.GONE
                        } else {
                            database.child("Profile").child(userUid.toString()).get()
                                .addOnSuccessListener {
                                    user = it.getValue(Profile::class.java)
                                    binding.sendCollegeRequest.setOnClickListener {
                                        database.child("Colleges").child(collegeUid.toString())
                                            .child("GrpAdmin").get().addOnSuccessListener {
                                                var admin = it.value.toString()
                                                database.child("Profile").child(admin)
                                                    .child("GroupAdmin")
                                                    .child(collegeUid.toString())
                                                    .child("RequestsPending")
                                                    .child(userUid.toString())
                                                    .setValue(user).addOnSuccessListener {
                                                        Toast.makeText(
                                                            this@CollegeProfile,
                                                            "Request Sent Successfully",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        binding.sendCollegeRequest.visibility =
                                                            View.INVISIBLE
                                                    }
                                            }

                                    }
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            database.child("Colleges").child(collegeUid.toString()).child("Posts")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var postCount = snapshot.childrenCount
                        binding.noOfGroupPosts.setText(postCount.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            database.child("Colleges").child(collegeUid.toString()).child("Users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var memberCount = snapshot.childrenCount
                        binding.noOfGroupMembers.setText(memberCount.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            grpFriends= arrayListOf()
            memberAdapter = memberAdapter(this, grpFriends)
            binding.contactListView.layoutManager = LinearLayoutManager(this)
            binding.contactListView.adapter = memberAdapter
            collegeUid?.let { it1 ->
                database.child("Colleges").child(it1).child("Users")
                    .addValueEventListener(object : ValueEventListener {
                        @SuppressLint("SetTextI18n")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.childrenCount == x) {
                                Toast.makeText(this@CollegeProfile, "No Other Users", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                grpFriends.clear()
                                for (friendSnapshot in snapshot.children) {
                                    var friend = friendSnapshot.getValue(ProfileMain::class.java)
                                    if (friend != null && friend.usersUid != userUid) {
                                        grpFriends.add(friend)
                                    }
                                }
                                memberAdapter.notifyDataSetChanged()
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
            }

        }
    }
}