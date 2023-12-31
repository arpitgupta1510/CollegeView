package com.CollegeCircle.clg.Adapter


import android.content.Context
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.bumptech.glide.Glide
import com.CollegeCircle.clg.Activities.FriendsChat
import com.CollegeCircle.clg.Models.Message
import com.CollegeCircle.clg.R
import com.CollegeCircle.clg.databinding.ItemRecieveBinding
import com.CollegeCircle.clg.databinding.ItemSentBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_friends_chat.view.*
import kotlinx.android.synthetic.main.item_recieve.view.*
import kotlinx.android.synthetic.main.item_sent.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class messageAdapter(
    private var context: Context,
    var msgList: ArrayList<Message>,
    private var usersUid: String,
    private var msgIdList:ArrayList<String>
) : Adapter<RecyclerView.ViewHolder>() {
    private var date: DateFormat = SimpleDateFormat("hh:mm a")
    private var date2: DateFormat = SimpleDateFormat("dd:mm")
    private lateinit var mListener: onItemClickListener
    private var isAlready: ArrayList<Boolean>? = null
    var replyPosition:Int=msgList.size-1
    private var friendsChat: FriendsChat = FriendsChat()
    private var database = FirebaseDatabase.getInstance().reference

    interface onItemClickListener {
        fun onItemClick(position: Int)
//        fun onItemLongClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            var view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false)
            return sentViewHolder(view, mListener)
        } else {
            var view = LayoutInflater.from(context).inflate(R.layout.item_recieve, parent, false)
            return recieveViewHolder(view, mListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = msgList[position]
        if (holder::class.java == sentViewHolder::class.java) {
            val viewHolder = holder as sentViewHolder
            if(currentMessage.repliedTo==null){
                holder.itemView.msgRepliedTo.visibility=View.GONE
            }else{
                holder.itemView.msgRepliedTo.visibility=View.VISIBLE
                 replyPosition=msgIdList.indexOf(currentMessage.repliedTo)
                if(replyPosition==-1)
                {
                    replyPosition=msgList.size-1
                    holder.itemView.msgRepliedTo.setText("Message Deleted")
                }
                else{
                    holder.itemView.msgRepliedTo.setText(msgList[replyPosition].msg.toString())
                    holder.itemView.msgRepliedTo.setOnClickListener {
                    }
                }
            }
            if(currentMessage.seen==true)
            {
                holder.itemView.sendMsgDone.visibility=View.VISIBLE
                holder.itemView.sendMsg.visibility=View.GONE
            }
            else
            {
                holder.itemView.sendMsgDone.visibility=View.GONE
                holder.itemView.sendMsg.visibility=View.VISIBLE
            }
            if (currentMessage.image == null) {
                holder.itemView.ImageSentt.visibility = View.GONE
            } else {
                holder.itemView.ImageSentt.visibility = View.VISIBLE
                Glide.with(context)
                    .load(currentMessage.image)
                    .override(1000, 1000)
                    .placeholder(R.drawable.user)
                    .into(holder.itemView.ImageSent)
            }
                holder.itemView.msgSentText.setText(currentMessage.msg.toString())

            holder.itemView.msgSentTime.setText(
                date.format(currentMessage.date.toString().toLong()).toString()
            )
        } else {
            val viewHolder = holder as recieveViewHolder
            if (currentMessage.image == null) {
                holder.itemView.ImageRecieve.visibility = View.GONE
            } else {
                holder.itemView.ImageRecieve.visibility = View.VISIBLE
                Glide.with(context)
                    .load(currentMessage.image)
                    .override(1000, 1000)
                    .placeholder(R.drawable.user)
                    .into(holder.itemView.ImageRecieve)
            }
            holder.itemView.msgRecieveBox.setText(currentMessage.msg.toString())
            holder.itemView.msgRecieveTime.setText(
                date.format(
                    currentMessage.date.toString().toLong()
                ).toString()
            )
        }
    }

    override fun getItemCount(): Int {
        return msgList.size
    }

    override fun getItemViewType(position: Int): Int {
        var message = msgList[position]
        if (usersUid.equals(message.senderId)) {
            return 1
        } else {
            return 2
        }
    }

    class sentViewHolder(itemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.msgSentText
        var binding = ItemSentBinding.bind(itemView)

        init {
//            itemView.setOnLongClickListener {
//                listener.onItemLongClick(adapterPosition)
//            }
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    class recieveViewHolder(itemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val recieveMessage = itemView.msgRecieveBox
        var binding = ItemRecieveBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        when (event.action) {
            MotionEvent.ACTION_OUTSIDE -> {
                Toast.makeText(context, "Slide", Toast.LENGTH_SHORT).show()
            }
            MotionEvent.ACTION_UP -> {

            }
            MotionEvent.ACTION_MOVE -> {

            }

        }
        return true
    }

}