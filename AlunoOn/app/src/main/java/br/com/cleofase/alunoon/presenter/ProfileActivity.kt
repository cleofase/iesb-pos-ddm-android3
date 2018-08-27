package br.com.cleofase.alunoon.presenter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_student_detail.*

class ProfileActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var studentDBRef: DatabaseReference? = null
    private var student: Student = Student()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_detail)
        btn_save.setOnClickListener { saveStudent() }
        btn_logout.setOnClickListener { performLogOut() }
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        studentDBRef = database!!.getReference("student")
        updateUI()
    }

    private fun updateUI() {
        val studentListener = object: ValueEventListener {
            override fun onDataChange(studentSnapshot: DataSnapshot) {
                student = studentSnapshot.getValue(Student::class.java)!!
                txt_name.setText(student.name)
                txt_email.setText(student.email)
                txt_enrolling.setText(student.enrolling)
                txt_phone.setText(student.phone)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Erro recuperando Estudante: ${error.toException()}", Toast.LENGTH_LONG).show()
            }
        }
        studentDBRef!!.child(auth!!.currentUser!!.uid).addListenerForSingleValueEvent(studentListener)
    }

    private fun saveStudent() {
        student.name = txt_name.text.toString()
        student.enrolling = txt_enrolling.text.toString()
        student.phone = txt_phone.text.toString()
        student.uuid = auth!!.currentUser!!.uid
        student.email = txt_email.text.toString()
        studentDBRef!!.child(auth!!.currentUser!!.uid).setValue(student)
        finish()
    }

    private fun performLogOut() {
        auth!!.signOut()
        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
