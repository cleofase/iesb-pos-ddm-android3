package br.com.cleofase.alunoon.presenter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.com.cleofase.alunoon.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot.*

class ForgotActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        btn_forgot.setOnClickListener { resetPassword() }
        auth = FirebaseAuth.getInstance()
    }

    private fun resetPassword() {
        val email = txt_email.text.toString()
        if (auth == null) {
            Toast.makeText(this,"Serviço não disponível, tente mais tarde!", Toast.LENGTH_LONG).show()
            return
        }
        if (!isEmailValid(email)) {
            Toast.makeText(this, "Informe o seu email corretamente!", Toast.LENGTH_LONG).show()
            return
        }
        auth!!.sendPasswordResetEmail(email).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Foi enviado um email de redefinição", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Ocorreu um erro ao tentar redefinir a senha!", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}".toRegex()
        return emailRegex.matches(email)
    }
}
