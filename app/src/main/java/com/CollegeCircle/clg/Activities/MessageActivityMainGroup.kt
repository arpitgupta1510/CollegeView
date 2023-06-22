package com.CollegeCircle.clg.Activities

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.CollegeCircle.clg.R
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.CollegeCircle.clg.Adapter.GroupMembers
import com.CollegeCircle.clg.Adapter.messageAdapter
import com.CollegeCircle.clg.Models.Message
import com.CollegeCircle.clg.Models.ProfileMain
import com.CollegeCircle.clg.databinding.ActivityMessageMainGroupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_message_main_group.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageActivityMainGroup : AppCompatActivity() {
    private lateinit var binding: ActivityMessageMainGroupBinding
    private lateinit var database: DatabaseReference
    private lateinit var database2: DatabaseReference
    private lateinit var fauth: FirebaseAuth
    private var isTyping: Boolean = false
    private lateinit var messages: ArrayList<Message>
    private lateinit var messagesId: ArrayList<String>
    private lateinit var msgAdapter: messageAdapter
    private lateinit var memberAdapter:GroupMembers
    private lateinit var membersList:ArrayList<ProfileMain>
    private val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    private var value: Int = 0
    private final var delay: Long = 1500
    private lateinit var message: Message
    private lateinit var rand: String
    private lateinit var usersUid: String
    private lateinit var clgUid: String
    private lateinit var clgImage: String
    private lateinit var clgName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageMainGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        usersUid = intent.getStringExtra("UsersUid").toString()
        clgUid = intent.getStringExtra("ClgUid").toString()
        clgImage = intent.getStringExtra("ClgImage").toString()
        clgName = intent.getStringExtra("ClgName").toString()
        Toast.makeText(this, clgName, Toast.LENGTH_SHORT).show()
        messages = arrayListOf()
        messagesId = arrayListOf()
        var linearLayoutmanager = LinearLayoutManager(this)
        linearLayoutmanager.stackFromEnd = true
        binding.mainCollegeChatView.layoutManager = linearLayoutmanager
        msgAdapter = messageAdapter(this, messages, usersUid,messagesId)
        binding.mainCollegeChatView.adapter = msgAdapter
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = null
            Glide.with(this@MessageActivityMainGroup)
                .load(clgImage)
                .override(120, 120)
                .circleCrop()
                .into(mainCollegeImage)
            binding.title.text = clgName
        }
        binding.toolbar.setOnClickListener {
            var intent = Intent(this, CollegeProfile::class.java)
            intent.putExtra("UsersUid", usersUid)
            intent.putExtra("ClgUid", clgUid)
            startActivity(intent)
        }
        database2 = FirebaseDatabase.getInstance().getReference()
        database = FirebaseDatabase.getInstance().getReference().child("Colleges").child(clgUid)
            .child("Chats")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for (dataSnapshot in snapshot.children) {
                    var message = dataSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                msgAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageActivityMainGroup, "No Message sent", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        msgAdapter.setOnItemClickListener(object : messageAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

            }
        })
//        val startForResult =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//                if (result.resultCode == Activity.RESULT_OK) {
//                    val intent = result.data
//                    if (intent != null) {
//                        selectedImage = intent.getStringExtra("SelectedImage").toString().toUri()
//                        caption = intent.getStringExtra("ImageCaption").toString()
//                        rand = database.push().key.toString()
//                        storage = FirebaseStorage.getInstance().getReference().child(senderRoom)
//                            .child(rand.toString())
//                        storage.putFile(selectedImage!!).addOnCompleteListener(OnCompleteListener {
//                            if (it.isSuccessful) {
//                                storage.downloadUrl.addOnSuccessListener {
//                                    val message = Message(
//                                        it.toString(),
//                                        caption,
//                                        senderId,
//                                        receiverName,
//                                        System.currentTimeMillis().toString(),
//                                        false,
//                                        rand
//                                    )
//                                    database2.child("Profile").child(senderId).child("friend")
//                                        .child(receiverId).child("messages")
//                                        .child(rand).setValue(message).addOnSuccessListener {
//                                            database2.child("Profile").child(receiverId)
//                                                .child("friend").child(senderId).child("messages")
//                                                .child(rand).setValue(message)
//                                                .addOnSuccessListener {
//                                                    Toast.makeText(
//                                                        this,
//                                                        "Message sent",
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                }
//                                        }
//                                }
//                            }
//                        })
//                    }
//                }
//            }
        binding.mainCollegefileSendBtn.setOnClickListener {
            Toast.makeText(this, "File Send Yet to Implement", Toast.LENGTH_SHORT).show()
        }
        membersList= arrayListOf()
        memberAdapter= GroupMembers(this,membersList,usersUid,clgUid)
        binding.grpMembersView.layoutManager=LinearLayoutManager(this)
        binding.grpMembersView.adapter=memberAdapter
        binding.mainCollegeMsgBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                database.child("IsTyping").setValue("typing..")

//                if(s!=null)
//                {
//                    var str=s.toString()
//                    if(str.get(str.length-1).toString().equals("@"))
//                    {
//
//
//                        database.child("Colleges").child(clgUid).child("Users").addValueEventListener(object :ValueEventListener{
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                membersList.clear()
//                                for(memberSnapshot in snapshot.children){
//                                    var user=memberSnapshot.getValue(ProfileMain::class.java)
//                                    if(user!=null)
//                                    {
//                                        membersList.add(user)
//                                    }
//                                }
//                                memberAdapter.notifyDataSetChanged()
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//
//                            }
//
//                        })
//                    }
//                }
            }

            var timer = Timer()

            override fun afterTextChanged(s: Editable?) {
                if (!isTyping) {
                    isTyping = true
                }
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            isTyping = false
                            database.child("IsTyping").setValue(null)
                        }
                    },
                    delay
                )
            }
        })
        binding.mainCollegeMsgBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                database2.child("Chats").child(senderRoom).child(fauth.currentUser!!.uid)
//                    .setValue("typing..")
            }

            var timer = Timer()

            override fun afterTextChanged(s: Editable?) {
                if (!isTyping) {
                    isTyping = true
                }
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            isTyping = false
//                            database2.child("Chats").child(senderRoom)
//                                .child(fauth.currentUser!!.uid).setValue(null)
                        }
                    },
                    delay
                )
            }
        })
//        database2.child("Chats").child(receiverRoom).get().addOnSuccessListener {
//            var typing = it.child(receiverId).value
//        }
        binding.mainGroupMsgSentBtn.setOnClickListener {
            var newMsg = binding.mainCollegeMsgBox.text
            rand = database2.push().key.toString()
            if (newMsg == null) {
                Toast.makeText(this, "Type something", Toast.LENGTH_SHORT).show()
            } else {
                binding.mainCollegeMsgBox.setText("")
                var rand = database2.push().key.toString()
                message = Message(
                    null,
                    newMsg.toString().trim(),
                    usersUid,
                    null,
                    System.currentTimeMillis().toString(),
                    false,
                    rand
                )
                if (rand != null) {
                    database2.child("Colleges").child(clgUid).child("Chats")
                        .child(rand).setValue(message).addOnSuccessListener {
                        }
                }
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.call -> {
                Toast.makeText(applicationContext, "Call yet to Implemet", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.video -> {
                Toast.makeText(applicationContext, "Video Yet to Implement", Toast.LENGTH_LONG).show()
                return false
            }
            R.id.mediaBox -> {
                Toast.makeText(this, "Media Box Yet to Implement", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.mute -> {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("Do you want to mute Notifications from user")
                    .setCancelable(false)
                    .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
                        var sharedPref =
                            getSharedPreferences("Mute Notifications", Context.MODE_PRIVATE)
                        var editor = sharedPref.edit()
                        editor.putBoolean(clgUid, true)
                        editor.apply()
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                        Toast.makeText(this, "Not Muted", Toast.LENGTH_SHORT).show()
                    })

                val alert = dialogBuilder.create()
                alert.show()
                return true
            }
            R.id.clear -> {
                Toast.makeText(this, "Clear Yet to Implement", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.messageRequest -> {
                Toast.makeText(this, "Message Request Yet to Implement", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.search -> {
                Toast.makeText(this, "Search Yet to Implement", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
//    private fun performSearch() {
//        binding.searchMessage.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                search(query)
//                return true
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//                search(newText)
//                return true
//            }
//        })
//    }

//    private fun search(text: String?) {
//        searchmessages = arrayListOf()
//
//        text?.let {
//            messages.forEach { message ->
//                if (message.msg?.contains(text, true) == true) {
//                    searchmessages.add(message)
//                }
//            }
//            if (searchmessages.isEmpty()) {
//                Toast.makeText(this, "No match found!", Toast.LENGTH_SHORT).show()
//            }
////            updateRecyclerView()
//        }
//    }

//    private fun makePhoneCall() {
//        if (true) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    android.Manifest.permission.CALL_PHONE
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(android.Manifest.permission.CALL_PHONE),
//                    requestCall
//                )
//            } else {
//                val dial = "tel:$phone"
//                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
//            }
//        } else {
//            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show()
//        }
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String?>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == requestCall) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                makePhoneCall()
//            } else {
//                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

//    private fun updateRecyclerView() {
//        binding.mainCollegeChatView.apply {
//            msgAdapter.msgList = searchmessages
//            msgAdapter.notifyDataSetChanged()
//        }
//    }

//    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Void? {
//        super.onActivityResult(requestCode, resultCode, data)
//        Toast.makeText(this, "Activity", Toast.LENGTH_SHORT).show()
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                selectedImage = intent.getStringExtra("SelectedImage")?.toUri()!!
//                caption = intent.getStringExtra("Caption").toString()
//                Toast.makeText(this, "sending Image", Toast.LENGTH_SHORT).show()
//                sendImage()
//            }
//        }
//        return null
//    }

//    private fun sendImage() {
//        var rand = database.push().key.toString()
//        storage =
//            FirebaseStorage.getInstance().getReference().child(senderRoom).child(rand.toString())
//        if (selectedImage != null) {
//            Toast.makeText(this, "Not null", Toast.LENGTH_SHORT).show()
//            storage.putFile(selectedImage!!).addOnCompleteListener(OnCompleteListener {
//                if (it.isSuccessful) {
//                    storage.downloadUrl.addOnSuccessListener {
//                        val message = Message(
//                            it.toString(),
//                            caption,
//                            fauth.currentUser!!.uid,
//                            fauth.currentUser!!.uid,
//                            System.currentTimeMillis().toString(),
//                            false,
//                            rand
//                        )
//                        database.child("Profile").child(senderId).child("friend").child(receiverId)
//                            .child("messages").child(rand).setValue(message).addOnSuccessListener {
//                                database.child("Profile").child(receiverId).child("friend")
//                                    .child(senderId).child("messages").child(rand).setValue(message)
//                                    .addOnSuccessListener {
//                                        val intent = Intent()
//                                        intent.putExtra("SelectedImage", selectedImage)
//                                        Toast.makeText(this, "Image Send", Toast.LENGTH_SHORT)
//                                            .show()
//                                        finish()
//                                    }
//                            }
//                    }
//                }
//            })
//        }
//    }

    fun Activity.hideSoftKeyboard(editText: EditText) {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }

//    private fun askPermission() {
//        ActivityCompat.requestPermissions(this, permissions, requestCode)
//    }

//    private fun isPermissionGranted(): Boolean {
//        for (permission in permissions) {
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    permission
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return false
//            }
//        }
//        return true
//    }
}