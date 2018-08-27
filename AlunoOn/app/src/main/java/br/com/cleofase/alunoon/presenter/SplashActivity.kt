package br.com.cleofase.alunoon.presenter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import br.com.cleofase.alunoon.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = FirebaseAuth.getInstance()
    }

    override fun onResume() {
        super.onResume()
        val intent = if (auth != null) {
            if (auth!!.currentUser != null) {
                Intent(this@SplashActivity, NewsActivity::class.java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }
        } else {
            Intent(this@SplashActivity, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
