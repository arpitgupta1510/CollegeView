package com.CollegeCircle.clg.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.CollegeCircle.clg.Activities.FirstActivity
import com.CollegeCircle.clg.Activities.StatusSelect
import com.CollegeCircle.clg.Adapter.StatusAdapter
import com.CollegeCircle.clg.Models.Status
import com.CollegeCircle.clg.R
import com.CollegeCircle.clg.databinding.FragmentStatusBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class StatusFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private lateinit var binding: FragmentStatusBinding
    private lateinit var database: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var usersUid: String
    private lateinit var clgUid: String
    private lateinit var clgName: String
    private lateinit var clgImage: String
    private lateinit var statusList: ArrayList<Status>
    private lateinit var statusAdapter: StatusAdapter
    private var x: Long = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatusBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()
        usersUid = (activity as FirstActivity).userUid.toString()
        clgUid = (activity as FirstActivity).clgUid.toString()
        clgName = (activity as FirstActivity).clgUid.toString()
        clgImage = (activity as FirstActivity).clgUid.toString()
        statusList = arrayListOf()
        statusAdapter = StatusAdapter(requireContext(), statusList, usersUid)
        binding.unseenStatus.layoutManager = LinearLayoutManager(context)
        binding.unseenStatus.adapter = statusAdapter
        database.child("Colleges").child(clgUid).child("Status").orderByChild("date")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount == x) {
                        Toast.makeText(context, "No status", Toast.LENGTH_SHORT).show()
                    } else {
                        for (StatusSnapshot in snapshot.children) {
                            var status = StatusSnapshot.getValue(Status::class.java)
                            if (status != null) {
                                if (System.currentTimeMillis() - status.date.toString()
                                        .toLong() >= 86400000
                                ) {
                                    database.child(status.friendUid.toString()).removeValue()
                                } else {
                                    statusList.add(status)
                                }
                            }
                        }
                        statusAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        binding.cardView4.setOnClickListener {
            var intent = Intent(context, StatusSelect::class.java)
            intent.putExtra("ClgUid", clgUid)
            intent.putExtra("UsersUid", usersUid)
            intent.putExtra("ClgName", clgName)
            intent.putExtra("ClgImage", clgImage)
            startActivity(intent)
        }
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_status, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.savedStatus->
            {
                Toast.makeText(context, "Saved Status", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.mutedFriends->{
                Toast.makeText(context, "Muted Status", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}