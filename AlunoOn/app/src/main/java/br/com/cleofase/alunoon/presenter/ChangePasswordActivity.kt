package br.com.cleofase.alunoon.presenter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.com.cleofase.alunoon.R
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        btn_change_password.setOnClickListener { changePassword() }
        auth = FirebaseAuth.getInstance()
    }

    private fun changePassword() {
        val old_password = txt_password_old.text.toString()
        val new_password = txt_password.text.toString()
        val re_new_password = txt_re_password.text.toString()

        if (auth == null) {
            Toast.makeText(this,"Serviço não disponível, tente mais tarde!", Toast.LENGTH_LONG).show()
            return
        }
        if (!isPasswordValid(old_password)) {
            Toast.makeText(this, "Informe a senha antiga válida!", Toast.LENGTH_LONG).show()
            return
        }
        if (!isPasswordValid(new_password)) {
            Toast.makeText(this, "Informe uma nova senha válida!", Toast.LENGTH_LONG).show()
            return
        }
        if (!new_password.equals(re_new_password)) {
            Toast.makeText(this, "Redigite a mesma nova senha informada!", Toast.LENGTH_LONG).show()
            return
        }
        val user: FirebaseUser = auth!!.currentUser!!
        val email = user.email!!
        val authCredential: AuthCredential = EmailAuthProvider.getCredential(email, old_password)
        auth!!.currentUser!!.reauthenticate(authCredential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                user.updatePassword(new_password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Senha atualizada com sucesso!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Falha ao atualizar senha: ${task.exception.toString()}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "É necessário informar a senha antiga corretamente!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex = "[A-Z0-9a-z._%+-]{6,}".toRegex()
        return passwordRegex.matches(password)
    }
}
