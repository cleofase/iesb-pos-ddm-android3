package br.com.cleofase.alunoon.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

private enum class MessageChatType(val id: Int) {
    MY_MESSAGE_CHAT_TYPE(0),
    HIS_MESSAGE_CHAT_TYPE(1)
}

class ChatAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var chat: MutableList<Chat> = mutableListOf()

    public fun setData(newChat: MutableList<Chat>) {
        chat = newChat
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth.currentUser
        val msgChatSenderId: String = chat[position].sender
        if (user != null) {
            if (user!!.uid == msgChatSenderId) {
                return MessageChatType.MY_MESSAGE_CHAT_TYPE.id
            }
        }
        return return MessageChatType.HIS_MESSAGE_CHAT_TYPE.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            MessageChatType.MY_MESSAGE_CHAT_TYPE.id -> {
                val view = LayoutInflater.from(context).inflate(R.layout.my_chat_cell, parent, false)
                return MyChatViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.his_chat_cell, parent, false)
                return HisChatViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(cell: RecyclerView.ViewHolder, position: Int) {
        when (cell) {
            is MyChatViewHolder -> {
                val message = chat[position]
                cell.txt_msg_chat.text = message.message
            }

            is HisChatViewHolder -> {
                val message = chat[position]
                cell.txt_msg_chat.text = message.message
            }
        }
    }

    override fun getItemCount(): Int {
        return chat.size
    }

}