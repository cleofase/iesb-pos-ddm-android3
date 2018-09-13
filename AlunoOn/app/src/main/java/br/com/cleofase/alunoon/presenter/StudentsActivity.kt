package br.com.cleofase.alunoon.presenter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.Student
import br.com.cleofase.alunoon.adapter.StudentsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class StudentsActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var database: FirebaseDatabase? = null
    private var studentsDBRef: DatabaseReference? = null
    private var students: MutableList<Student> = mutableListOf()
    private lateinit var studentsTable: RecyclerView
    private lateinit var studentsAdapter: StudentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)
        studentsTable = this.findViewById(R.id.rec_view_students)
        studentsTable.layoutManager = LinearLayoutManager(this)
        studentsTable.itemAnimator = DefaultItemAnimator()
        studentsAdapter = StudentsAdapter(this)
        studentsTable.adapter = studentsAdapter
        setupFirebase()
        updateUI()
    }

    private fun setupFirebase() {
        auth = FirebaseAuth.getInstance()
        user = auth?.currentUser
        database = FirebaseDatabase.getInstance()
        studentsDBRef = database?.getReference("/student")
    }

    private fun updateUI() {
        retrieveStudentsFromCloud()
    }

    private fun retrieveStudentsFromCloud() {
        if (user == null) {
            Toast.makeText(this@StudentsActivity, "Erro obtendo usuário corrente!", Toast.LENGTH_LONG).show()
            performLogOut()
            return
        }
        if (studentsDBRef == null) {
            Toast.makeText(this@StudentsActivity, "Erro obtendo lista de estudantes!", Toast.LENGTH_LONG).show()
            return
        }
        val studentsListener = object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StudentsActivity, "Erro recuperando lista de estudantes: ${error.toException()}", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(studentsSnapshot: DataSnapshot) {
                if (studentsSnapshot.exists()) {
                    students.clear()
                    studentsSnapshot.children.forEach {snap ->
                        val student: Student? = snap.getValue(Student::class.java)
                        if (student != null) {
                            if (student.uuid != user!!.uid) {
                                students.add(student)
                            }
                        }
                    }
                    studentsAdapter.setData(students)
                }
            }
        }
        studentsDBRef!!.addListenerForSingleValueEvent(studentsListener)
    }

    private fun performLogOut() {
        auth!!.signOut()
        val intent = Intent(this@StudentsActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
