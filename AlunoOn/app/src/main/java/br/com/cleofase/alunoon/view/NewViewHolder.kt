package br.com.cleofase.alunoon.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import br.com.cleofase.alunoon.R

class NewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var img_new: ImageView
    var txt_title_new: TextView
    var txt_body_new: TextView
    var txt_date_new: TextView

    init {
        img_new = itemView.findViewById(R.id.img_new)
        txt_title_new = itemView.findViewById(R.id.txt_title_new)
        txt_body_new = itemView.findViewById(R.id.txt_body_new)
        txt_date_new = itemView.findViewById(R.id.txt_date_new)
    }
}