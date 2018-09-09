package br.com.cleofase.alunoon.view

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.Student
import br.com.cleofase.alunoon.presenter.ChatActivity

class StudentsAdapter(private val context: Context): RecyclerView.Adapter<StudentViewHolder>() {
    private var students: MutableList<Student> = mutableListOf()

    public fun setData(newStudents: MutableList<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.student_cell, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(cell: StudentViewHolder, position: Int) {
        val currentStudent = students[position]
        cell.txt_name.text = currentStudent.name
        cell.txt_unread_msg.text = " "
        cell.itemView.setOnClickListener { goToChatWithStudentAt(position) }
    }

    override fun getItemCount(): Int {
        return students.size
    }

    private fun goToChatWithStudentAt(studentIndex: Int) {
        val studentId: String = students[studentIndex].uuid
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra("studentId", studentId)
        context.startActivity(intent)
    }
}