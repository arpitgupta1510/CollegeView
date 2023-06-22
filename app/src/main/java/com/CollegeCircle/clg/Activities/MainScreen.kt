package com.CollegeCircle.clg.Activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.CollegeCircle.clg.Adapter.CollegeAdapter
import com.CollegeCircle.clg.Models.College
import com.CollegeCircle.clg.Models.ProfileMain
import com.CollegeCircle.clg.databinding.ActivityMainScreenBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.CollegeCircle.clg.R
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.nav_header.*

class MainScreen : AppCompatActivity() {
    private lateinit var binding: ActivityMainScreenBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var database: DatabaseReference
    private lateinit var fauth: FirebaseAuth
    private lateinit var userUid: String
    private lateinit var grpList: ArrayList<College>
    private lateinit var clgAdapter: CollegeAdapter
    private var x: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#363C4A")))
        fauth = FirebaseAuth.getInstance()
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.myPosts -> {
                    val intent = Intent(this, MyPosts::class.java)
                    intent.putExtra("UsersUid", userUid)
                    startActivity(intent)
                }
                R.id.addCollege -> {
                    Toast.makeText(this, "Add College", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SelectCollege::class.java)
                    intent.putExtra("UsersUid", userUid)
                    startActivity(intent)
                }
                R.id.myLocation->{
                    Toast.makeText(this, "MyLocation yet to implement", Toast.LENGTH_SHORT).show()
                }
                R.id.share->{
                    Toast.makeText(this, "Share yet to implement", Toast.LENGTH_SHORT).show()
                }
                R.id.rateUs->{
                        Toast.makeText(this, "Share yet to implement", Toast.LENGTH_SHORT).show()
                }
                R.id.settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                }
                R.id.logout -> {
                    val dialogBuilder = AlertDialog.Builder(this)
                    dialogBuilder.setMessage("Do you want to Clear all chats?")
                        .setCancelable(false)
                        .setPositiveButton("LogOut", DialogInterface.OnClickListener { _, _ ->
                            database.child("Profile").child(userUid).child("token")
                                .setValue(null).addOnSuccessListener {
                                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                                    deleteUserInfoFromSharedPreferences()
                                    fauth.signOut()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        })
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                            dialog.cancel()
                            Toast.makeText(this, "Not Logout", Toast.LENGTH_SHORT).show()
                        })
                        .setNeutralButton(
                            "Clear",
                            DialogInterface.OnClickListener { _, _ ->
                                Toast.makeText(this, "Neutral Button", Toast.LENGTH_SHORT).show()
                            })

                    val alert = dialogBuilder.create()
                    alert.show()

                }
            }
            true
        }
        database = FirebaseDatabase.getInstance().reference
        userUid = fauth.currentUser!!.uid
        grpList = arrayListOf()

        clgAdapter = CollegeAdapter(this, grpList, userUid)
        binding.clgShowView.layoutManager = LinearLayoutManager(this)
        binding.clgShowView.adapter = clgAdapter

        database.child("Profile").child(userUid).child("Groups")
            .addValueEventListener(object :
                ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount == x) {
                        Toast.makeText(
                            this@MainScreen,
                            "Not added in any group",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        grpList.clear()

                        for (clgSnapshot in snapshot.children) {
                            val clg = clgSnapshot.getValue(College::class.java)
                            if (clg != null) {
                                grpList.add(clg)
                            }
                        }
                        clgAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            database.child("Profile").child(userUid).child("token").setValue(token)

        })
        database.child("Online_Status").child(userUid).setValue("Online")
        database.child("Online_Status").child(userUid).onDisconnect()
            .setValue(System.currentTimeMillis())
        database.child("Profile").child(userUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(ProfileMain::class.java)
                    if (user != null) {
                        Glide.with(this@MainScreen)
                            .load(user.image)
                            .override(1000, 1000)
                            .circleCrop()
                            .placeholder(R.drawable.user)
                            .into(userDashBoardImage)
                        profileEdit.setOnClickListener {
                            val intent = Intent(this@MainScreen, CompleteProfile::class.java)
                            intent.putExtra("UsersUid", userUid)
                            startActivity(intent)
                        }
                        userDashBoardName.setText(user.name)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun deleteUserInfoFromSharedPreferences() {
        val sharedPref = getSharedPreferences("User Info", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}