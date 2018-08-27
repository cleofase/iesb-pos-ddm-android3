package br.com.cleofase.alunoon.presenter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        btn_signUp.setOnClickListener { performSignup() }
        auth = FirebaseAuth.getInstance()
    }

    private fun performSignup() {
        val email = txt_email.text.toString()
        val password = txt_password.text.toString()
        val re_password = txt_re_password.text.toString()
        if (auth == null) {
            Toast.makeText(this,"Serviço não disponível, tente mais tarde!", Toast.LENGTH_LONG).show()
            return
        }
        if (!isEmailValid(email)) {
            Toast.makeText(this,"Informe um email válido!", Toast.LENGTH_LONG).show()
            return
        }
        if (!isPasswordValid(password)) {
            Toast.makeText(this, "Informe uma senha válida!", Toast.LENGTH_LONG).show()
            return
        }
        if (!password.equals(re_password)) {
            Toast.makeText(this, "Redigite o mesmo password informado!", Toast.LENGTH_LONG).show()
            return
        }
        auth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser = auth!!.currentUser!!
                var database: FirebaseDatabase = FirebaseDatabase.getInstance()
                var studentDBRef: DatabaseReference = database.getReference("student")
                var student = Student()
                student.uuid = user.uid
                student.email = user.email!!
                studentDBRef.child(user.uid).setValue(student)
                val intent = Intent(this@SignupActivity, NewsActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val resultDetail = task.result.toString()
                Toast.makeText(this, "Erro no cadastro: ${resultDetail}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}".toRegex()
        return emailRegex.matches(email)
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex = "[A-Z0-9a-z._%+-]{6,}".toRegex()
        return passwordRegex.matches(password)
    }
}
