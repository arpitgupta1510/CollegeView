package com.CollegeCircle.clg.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.CollegeCircle.clg.Activities.FriendsChat
import com.CollegeCircle.clg.Models.ProfileMain
import com.CollegeCircle.clg.R
import kotlinx.android.synthetic.main.groupmembers.view.*

class GroupMembers(
    private var context: Context,
    private var contactList: ArrayList<ProfileMain>,
    private var usersUid: String,
    private var clgUid: String
) : RecyclerView.Adapter<GroupMembers.GroupMembersViewHolder>() {
    class GroupMembersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var friendsChat: FriendsChat = FriendsChat()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMembersViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.groupmembers, parent, false)
        return GroupMembersViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupMembersViewHolder, position: Int) {
        var contact = contactList[position]
        holder.itemView.apply {
            friendGrpName.setText(contact.name)
            friendGrpUid.setText(contact.usersUid)
            Toast.makeText(context, contact.name, Toast.LENGTH_SHORT).show()
            Glide.with(this)
                .load(contact.image)
                .override(1000, 1000)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(friendGrpImage)

        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

}