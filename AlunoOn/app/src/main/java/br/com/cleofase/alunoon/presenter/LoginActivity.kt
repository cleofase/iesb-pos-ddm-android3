package br.com.cleofase.alunoon.presenter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.com.cleofase.alunoon.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btn_login.setOnClickListener { performLogin() }
        btn_signUp.setOnClickListener { goToSignupAct() }
        btn_forgot.setOnClickListener { goToForgotAct() }
        auth = FirebaseAuth.getInstance()
    }

    private fun performLogin() {
        val email = txt_email.text.toString()
        val password = txt_password.text.toString()
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
        auth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
            if (task.isSuccessful) {
                val intent = Intent(this@LoginActivity, NewsActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Erro no Login!! ${task.exception.toString()}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToSignupAct() {
        val intent = Intent(this@LoginActivity, SignupActivity::class.java)
        startActivity(intent)
    }

    private fun goToForgotAct() {
        val intent = Intent(this@LoginActivity, ForgotActivity::class.java)
        startActivity(intent)
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
