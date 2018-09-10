package br.com.cleofase.alunoon.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import br.com.cleofase.alunoon.R

class StudentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var img_student: ImageView
    var txt_name: TextView

    init {
        img_student = itemView.findViewById(R.id.img_student)
        txt_name = itemView.findViewById(R.id.txt_name)
    }
}