package br.com.cleofase.alunoon.presenter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.New
import br.com.cleofase.alunoon.adapter.NewsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var database: FirebaseDatabase? = null
    private var newsDBRef: DatabaseReference? = null
    private var newsForStudent: MutableList<New> = mutableListOf()
    private lateinit var studentNewsTable: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private var studentNewsListener: ValueEventListener? = null
    val bannerImagesIndex: Array<Int> = arrayOf(R.drawable.banner_baiatodosossantos, R.drawable.banner_fortesaomarcelo, R.drawable.banner_morrosaopaulo)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        btn_profile.setOnClickListener { goToProfile() }
        btn_friends.setOnClickListener { goToFriends() }
        bnr_top_news.photoResourcesIndex = bannerImagesIndex
        bnr_top_news.transitionTime = 3000
        studentNewsTable = this.findViewById(R.id.rec_view_news)
        studentNewsTable.layoutManager = LinearLayoutManager(this)
        studentNewsTable.itemAnimator = DefaultItemAnimator()
        newsAdapter = NewsAdapter(this)
        studentNewsTable.adapter = newsAdapter
        setupFirebase()
    }

    override fun onResume() {
        super.onResume()
        retrieveNewsFromCloud()
    }

    override fun onPause() {
        super.onPause()
        if (studentNewsListener != null) {
            newsDBRef!!.child(user!!.uid).removeEventListener(studentNewsListener)
        }

    }

    private fun setupFirebase() {
        auth = FirebaseAuth.getInstance()
        user = auth?.currentUser
        database = FirebaseDatabase.getInstance()
        newsDBRef = database?.getReference("/student_news")
    }

    private fun retrieveNewsFromCloud() {
        if (user == null) {
            Toast.makeText(this@NewsActivity, "Erro obtendo usuário corrente!", Toast.LENGTH_LONG).show()
            performLogOut()
            return
        }
        if (newsDBRef == null) {
            Toast.makeText(this@NewsActivity, "Erro obtendo dados do usuário!", Toast.LENGTH_LONG).show()
            return
        }
        studentNewsListener = object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NewsActivity, "Erro recuperando lista de notícias: ${error.toException()}", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(studentNewsSnapshot: DataSnapshot) {
                if (studentNewsSnapshot.exists()) {
                    newsForStudent.clear()
                    studentNewsSnapshot.children.forEach {snap ->
                        val newForStudent: New? = snap.getValue(New::class.java)
                        if (newForStudent != null) {
                            newsForStudent.add(newForStudent)
                        }
                    }
                    newsAdapter.setData(newsForStudent)
                }
            }
        }
        newsDBRef!!.child(user!!.uid).addListenerForSingleValueEvent(studentNewsListener)
    }

    private fun goToProfile() {
        val intent = Intent(this@NewsActivity, ProfileActivity::class.java)
        startActivity(intent)
    }

    private  fun goToFriends() {
        val intent = Intent(this@NewsActivity, StudentsActivity::class.java)
        startActivity(intent)
    }

    private fun performLogOut() {
        auth!!.signOut()
        val intent = Intent(this@NewsActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
