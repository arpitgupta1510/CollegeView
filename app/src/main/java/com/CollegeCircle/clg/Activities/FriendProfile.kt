package com.CollegeCircle.clg.Activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.CollegeCircle.clg.Models.Profile
import com.CollegeCircle.clg.Models.ProfileMain
import com.CollegeCircle.clg.Models.completeProfile
import com.CollegeCircle.clg.R
import com.CollegeCircle.clg.databinding.ActivityFriendProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class FriendProfile : AppCompatActivity() {
    private lateinit var binding:ActivityFriendProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private lateinit var userUid: String
    private lateinit var clgUid: String
    private lateinit var friendUid: String
    private lateinit var friendName: String
    private lateinit var friendImage: String
    private lateinit var userProfile: Map<String, String>
    private lateinit var friendProfile: Map<String, String>
    private var x: Long = 0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()
        clgUid = intent.getStringExtra("ClgUid").toString()
        userUid = intent.getStringExtra("UsersUid").toString()
        friendUid = intent.getStringExtra("FriendUid").toString()
        friendImage = intent.getStringExtra("FriendImage").toString()
        friendName = intent.getStringExtra("FriendName").toString()
        binding.friendNameShow.setText(friendName)
        Glide.with(this)
            .load(friendImage)
            .override(1000, 1000)
            .circleCrop()
            .placeholder(R.drawable.user)
            .into(binding.friendsImage)
        database.child("Profile").child(friendUid).get().addOnSuccessListener {
            var completeProfile=it.getValue(completeProfile::class.java)
            var profile=it.getValue(Profile::class.java)
            binding.friendDescriptionShow.setText(profile!!.description.toString())
            if(completeProfile!=null)
            {
                if(completeProfile.city!=null)
                binding.userCity.setText("  "+completeProfile.city)
                else
                    binding.userCity.setText("  Not Available")
                if(completeProfile.githubId!=null)
                binding.userGithub.setText("  "+completeProfile.githubId)
                else
                    binding.userGithub.setText("  Not Available")
                if(completeProfile.facebookId!=null)
                binding.userFacebook.setText("  "+completeProfile.facebookId)
                else
                    binding.userFacebook.setText("  Not Available")
                if(completeProfile.instaId!=null)
                binding.userInstagram.setText("  "+completeProfile.instaId)
                else
                    binding.userInstagram.setText("  Not Available")
                if(completeProfile.linkedinId!=null)
                binding.userLinkedIn.setText("  "+completeProfile.linkedinId)
                else
                    binding.userLinkedIn.setText("  Not Available")
                if(completeProfile.phoneNo!=null)
                binding.userPhone.setText("  "+completeProfile.phoneNo)
                else
                    binding.userPhone.setText("  Not Available")

                binding.userInstagram.setOnClickListener {
                    completeProfile.instaId?.let { it1 -> searchInsta(it1) }
                }
                binding.userFacebook.setOnClickListener {
                    completeProfile.facebookId?.let { it1 -> searchFacebook(it1) }
                }
                binding.userGithub.setOnClickListener {
                    completeProfile.githubId?.let { it1 -> searchGithub(it1) }
                }
                binding.userLinkedIn.setOnClickListener {
                    completeProfile.linkedinId?.let { it1 -> searchLinedIn(it1) }
                }

            }
        }
        database.child("Profile").child(userUid).child("Posts").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.childrenCount==x)
                {
                    binding.noOfPostsfriend.setText("0")
                    binding.userPostView.visibility=View.GONE
                    binding.noPostAvailable.visibility=View.VISIBLE
                }
                else
                {
                    binding.noOfPostsfriend.setText(snapshot.childrenCount.toString())
                    binding.userPostView.visibility=View.VISIBLE
                    binding.noPostAvailable.visibility=View.GONE

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        binding.message.setOnClickListener {
            database.child("Profile").child(userUid).get().addOnSuccessListener {
                var user = it.getValue(ProfileMain::class.java)
                var userName = user!!.name.toString()
                var userImage = user.image.toString()
                userProfile = mapOf<String, String>(
                    "name" to userName,
                    "image" to userImage,
                    "usersUid" to userUid
                )
                friendProfile = mapOf<String, String>(
                    "name" to friendName,
                    "image" to friendImage,
                    "usersUid" to friendUid
                )
                database.child("Colleges").child(clgUid).child("Users").child(userUid).child("Friends").child(friendUid).updateChildren(friendProfile).addOnSuccessListener {
                    database.child("Colleges").child(clgUid).child("Users").child(friendUid).child("Friends").child(userUid).updateChildren(userProfile).addOnSuccessListener {
                        var intent = Intent(this, FriendsChat::class.java)
                        intent.putExtra("UsersUid", userUid)
                        intent.putExtra("FriendUid", friendUid)
                        intent.putExtra("FriendName", friendName)
                        intent.putExtra("FriendImage", friendImage)
                        intent.putExtra("ClgUid", clgUid)
                        startActivity(intent)
                    }
                }
            }
        }
    }


    private fun searchInsta(words:String) {
        try{
            var uri= Uri.parse("https://www.instagram.com/"+words)
            var intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }catch (e:ActivityNotFoundException){
            e.printStackTrace()
            Toast.makeText(this, "Error in searching", Toast.LENGTH_SHORT).show()
        }
    }
    private fun searchFacebook(words:String) {
        try{
            var uri= Uri.parse("https://www.facebook.com/"+words)
            var intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }catch (e:ActivityNotFoundException){
            e.printStackTrace()
            Toast.makeText(this, "Error in searching", Toast.LENGTH_SHORT).show()
        }
    }
    private fun searchGithub(words:String) {
        try{
            var uri= Uri.parse("https://github.com/"+words)
            var intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }catch (e:ActivityNotFoundException){
            e.printStackTrace()
            Toast.makeText(this, "Error in searching", Toast.LENGTH_SHORT).show()
        }
    }
    private fun searchLinedIn(words:String) {
        try{
            var uri= Uri.parse("https://www.linkedin.com/in/"+words)
            var intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }catch (e:ActivityNotFoundException){
            e.printStackTrace()
            Toast.makeText(this, "Error in searching", Toast.LENGTH_SHORT).show()
        }
    }



}