package br.com.cleofase.alunoon.presenter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.New
import br.com.cleofase.alunoon.entity.Student
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_student_detail.*

class ProfileActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var database: FirebaseDatabase? = null
    private var studentDBRef: DatabaseReference? = null
    private var newDBRef: DatabaseReference? = null
    private var studentNewsDBRef: DatabaseReference? = null
    private var student: Student = Student()
    private var news: MutableList<New> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_detail)
        btn_save.setOnClickListener { saveStudent() }
        btn_change_password.setOnClickListener { changePassword() }
        btn_logout.setOnClickListener { performLogOut() }
        setupFirebase()
        updateUI()
    }

    private fun setupFirebase() {
        auth = FirebaseAuth.getInstance()
        user = auth?.currentUser
        database = FirebaseDatabase.getInstance()
        studentDBRef = database?.getReference("student")
        newDBRef = database?.getReference("new")
        studentNewsDBRef = database?.getReference("student_news")
    }

    private fun updateUI() {
        if (user == null) {
            Toast.makeText(this@ProfileActivity, "Erro obtendo usuário corrente!", Toast.LENGTH_LONG).show()
            performLogOut()
            return
        }
        if (studentDBRef == null) {
            Toast.makeText(this@ProfileActivity, "Erro obtendo dados do usuário!", Toast.LENGTH_LONG).show()
            return
        }
        val studentListener = object: ValueEventListener {
            override fun onDataChange(studentSnapshot: DataSnapshot) {
                if (studentSnapshot.exists()) {
                    student = studentSnapshot.getValue(Student::class.java)!!
                    txt_name.setText(student.name)
                    txt_email.setText(student.email)
                    txt_enrolling.setText(student.enrolling)
                    txt_phone.setText(student.phone)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Erro recuperando Estudante: ${error.toException()}", Toast.LENGTH_LONG).show()
            }
        }
        studentDBRef!!.child(user!!.uid).addListenerForSingleValueEvent(studentListener)
    }

    private fun saveStudent() {
        if (user == null) {
            Toast.makeText(this@ProfileActivity, "Erro obtendo usuário corrente!", Toast.LENGTH_LONG).show()
            performLogOut()
            return
        }
        if (studentDBRef == null) {
            Toast.makeText(this@ProfileActivity, "Erro obtendo dados do usuário!", Toast.LENGTH_LONG).show()
            return
        }
        student.name = txt_name.text.toString()
        student.enrolling = txt_enrolling.text.toString()
        student.phone = txt_phone.text.toString()
        student.uuid = user!!.uid
        student.email = txt_email.text.toString()
        studentDBRef!!.child(user!!.uid).setValue(student)

        generateNews()
        finish()
    }

    private fun generateNews() {
        if (user == null) {
            Toast.makeText(this@ProfileActivity, "Erro obtendo usuário corrente!", Toast.LENGTH_LONG).show()
            performLogOut()
            return
        }
        if (newDBRef == null) {
            Toast.makeText(this@ProfileActivity, "Erro obtendo notícias!", Toast.LENGTH_LONG).show()
            return
        }
        if (studentNewsDBRef == null) {
            Toast.makeText(this@ProfileActivity, "Erro obtendo notícias do usuário!", Toast.LENGTH_LONG).show()
            return
        }
        val newListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Erro recuperando lista de notícias: ${error.toException()}", Toast.LENGTH_LONG).show()
            }
            override fun onDataChange(newSnapshot: DataSnapshot) {
                if (newSnapshot.exists()) {
                    news.clear()
                    newSnapshot.children.mapNotNullTo(news) {it.getValue<New>(New::class.java)}
                    studentNewsDBRef!!.child(user!!.uid).setValue(news)
                }
            }
        }
        newDBRef!!.addListenerForSingleValueEvent(newListener)
    }

    private fun changePassword() {
        val intent = Intent(this@ProfileActivity, ChangePasswordActivity::class.java)
        startActivity(intent)
    }

    private fun performLogOut() {
        auth!!.signOut()
        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
