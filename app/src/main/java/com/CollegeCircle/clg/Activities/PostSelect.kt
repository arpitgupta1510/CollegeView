package com.CollegeCircle.clg.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.CollegeCircle.clg.Models.Post
import com.CollegeCircle.clg.Models.ProfileMain
import com.CollegeCircle.clg.R
import com.CollegeCircle.clg.databinding.ActivityPostSelectBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PostSelect : AppCompatActivity() {
    private lateinit var binding: ActivityPostSelectBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var selectedImage: Uri? = null
    private lateinit var storage: StorageReference
    private var userUid: String? = null
    private var clgUid:String?=null
    private lateinit var dialog: ProgressDialog
    private lateinit var timer: CountDownTimer
    private var user: ProfileMain? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userUid = intent.getStringExtra("UsersUid")
        clgUid= intent.getStringExtra("ClgUid")
        database = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()
        setSupportActionBar(binding.toolbar3)
        title = "  "
        binding.selectedPostBtn.setOnClickListener {
            launchGallery()
        }
        database.child("Profile").child(fAuth.currentUser!!.uid).get().addOnSuccessListener {
            var profileMain=it.getValue(ProfileMain::class.java)
            if (profileMain != null) {
                binding.userPostName.setText(profileMain.name)
                Glide.with(this)
                    .load(profileMain.image)
                    .override(1000, 1000)
                    .circleCrop()
                    .placeholder(R.drawable.user)
                    .into(binding.userPostImage)
            }

        }


        binding.postCaption.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var desc = binding.postCaption.text.toString()
                if (desc.isNotEmpty()) {
                    binding.finalPostSendBtn.isEnabled = true
                    binding.finalPostSendBtn.setBackgroundColor(resources.getColor(R.color.skyBlue))
                } else {
                    binding.finalPostSendBtn.isEnabled = false
                    binding.finalPostSendBtn.setBackgroundColor(resources.getColor(R.color.grey))
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.finalPostSendBtn.setOnClickListener {
            dialogShow()
            var rand = database.push().key.toString()
            if (selectedImage != null) {
                storage = FirebaseStorage.getInstance().getReference().child("Posts")
                    .child(userUid.toString()).child(rand)
                storage.putFile(selectedImage!!).addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        storage.downloadUrl.addOnSuccessListener {
                            var post = Post(
                                rand,
                                it.toString(),
                                binding.postCaption.text.toString(),
                                userUid,
                                0,
                                0,
                                System.currentTimeMillis().toString()
                            )
                                database.child("Profile").child(userUid.toString()).child("Posts")
                                    .child(rand).setValue(post)
                                    .addOnSuccessListener {
                                        clgUid?.let { it1 ->
                                            database.child("Colleges").child(it1).child("Posts")
                                                .child(rand).setValue(post)
                                                .addOnSuccessListener {
                                                    dialog.cancel()
                                                    Toast.makeText(
                                                        this,
                                                        "Status Updated",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                    var intent = Intent(this, MainScreen::class.java)
                                                    intent.putExtra("UsersUid", userUid)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                        }
                                    }

                        }
                    }

                })


            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                binding.finalPostSendBtn.isEnabled = true
                binding.finalPostSendBtn.setBackgroundColor(resources.getColor(R.color.skyBlue))
                binding.selectedPost.setImageURI(data.data!!)
                selectedImage = data.data!!
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.post_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addPostRequest -> {
                Toast.makeText(this, "Add Posts", Toast.LENGTH_SHORT).show()
                var intent = Intent(this, PostRequest::class.java)
                intent.putExtra("UsersUid", userUid)
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun launchGallery() {
        var intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, 100)

    }

    private fun dialogShow() {
        dialog = ProgressDialog(this)
        dialog.setMessage("Uploading posts")
        dialog.setCancelable(false)
        dialog.show()
        if (dialog.isShowing) {
            timer = object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {

                }
            }
            timer.start()
        }
    }
}