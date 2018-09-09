package br.com.cleofase.alunoon.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import br.com.cleofase.alunoon.R

class MyChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var txt_msg_chat: TextView

    init {
        txt_msg_chat = itemView.findViewById(R.id.txt_msg_chat)
    }
}