package com.CollegeCircle.clg.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.CollegeCircle.clg.databinding.ActivityCompleteProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CompleteProfile : AppCompatActivity() {
    private lateinit var binding:ActivityCompleteProfileBinding
    private lateinit var fauth:FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var completeProfile: Map<String, String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fauth= FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance().reference

        binding.completeProfileUpdateBtn.setOnClickListener {
            completeProfile = mapOf<String, String>(
                "phoneNo" to binding.completeProfilePhone.text.toString(),
                "instaId" to binding.completeProfileInstaId.text.toString(),
                "facebookId" to binding.completeProfileFacebookId.text.toString(),
                "linkedinId" to binding.completeProfileLinkedId.text.toString(),
                "githubId" to binding.completeProfileGithubId.text.toString(),
                "city" to binding.completeProfileCity.text.toString()
            )
            database.child("Profile").child(fauth.currentUser!!.uid).updateChildren(completeProfile).addOnSuccessListener {
                Toast.makeText(this, "Profile Uploaded", Toast.LENGTH_SHORT).show()
                var intent=Intent(this,MainScreen::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}