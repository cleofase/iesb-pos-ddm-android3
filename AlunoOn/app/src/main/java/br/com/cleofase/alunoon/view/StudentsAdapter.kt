package br.com.cleofase.alunoon.view

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.Student
import br.com.cleofase.alunoon.presenter.ChatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StudentsAdapter(private val context: Context): RecyclerView.Adapter<StudentViewHolder>() {
    private val PHOTO_MAX_SIZE: Long = 50 * 1024
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
        cell.itemView.setOnClickListener { goToChatWithStudentAt(position) }
        if (currentStudent.photo_url.trim().isNotEmpty()) {
            retrievePhotoFromCloudWith(currentStudent.photo_url, cell)
        }
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

    private fun retrievePhotoFromCloudWith(photoStoreRef: String, cell: StudentViewHolder) {
        val storageRef: StorageReference? = FirebaseStorage.getInstance().getReference(photoStoreRef)
        if (storageRef == null) {
            return
        }
        storageRef!!.getBytes(PHOTO_MAX_SIZE)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val photoByteArray = task.result
                        val photoBMP = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.size)
                        photoBMP.let {
                            cell.img_student.setImageBitmap(photoBMP)
                        }
                    }
                }
    }
}