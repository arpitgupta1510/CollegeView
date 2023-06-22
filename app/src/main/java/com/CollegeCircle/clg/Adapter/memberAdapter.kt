package com.CollegeCircle.clg.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.CollegeCircle.clg.Models.ProfileMain
import com.CollegeCircle.clg.R
import kotlinx.android.synthetic.main.row_table.view.*

class memberAdapter(
    private var context: Context,
    private var contactList: ArrayList<ProfileMain>
) : RecyclerView.Adapter<memberAdapter.ContactViewHolder>() {
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.row_table, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        var contact = contactList[position]
        holder.itemView.apply {
            friendClgName.setText(contact.name)
            Glide.with(this)
                .load(contact.image)
                .override(1000, 1000)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(friendClgImage)
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

}