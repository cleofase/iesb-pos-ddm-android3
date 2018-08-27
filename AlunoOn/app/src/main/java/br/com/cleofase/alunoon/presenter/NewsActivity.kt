package br.com.cleofase.alunoon.presenter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import br.com.cleofase.alunoon.R
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        btn_profile.setOnClickListener { goToProfile() }
        btn_friends.setOnClickListener { goToFriends() }
    }

    private fun goToProfile() {
        val intent = Intent(this@NewsActivity, ProfileActivity::class.java)
        startActivity(intent)
    }

    private  fun goToFriends() {
        val intent = Intent(this@NewsActivity, StudentsActivity::class.java)
        startActivity(intent)
    }
}
