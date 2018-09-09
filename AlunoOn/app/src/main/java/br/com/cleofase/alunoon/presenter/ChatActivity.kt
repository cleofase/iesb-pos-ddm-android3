package br.com.cleofase.alunoon.presenter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.Chat
import br.com.cleofase.alunoon.view.ChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

class ChatActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var database: FirebaseDatabase? = null
    private var chatDBRef: DatabaseReference? = null
    private var chat: MutableList<Chat> = mutableListOf()
    private lateinit var chatTable: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private var studentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatTable = this.findViewById(R.id.rec_view_chat)
        btn_send_msg.setOnClickListener { sendMsg() }
        chatTable.layoutManager = LinearLayoutManager(this)
        chatTable.itemAnimator = DefaultItemAnimator()
        chatAdapter = ChatAdapter(this)
        chatTable.adapter = chatAdapter
        studentId = this.intent.getStringExtra("studentId")
        setupFirebase()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun setupFirebase() {
        auth = FirebaseAuth.getInstance()
        user = auth?.currentUser
        database = FirebaseDatabase.getInstance()
        chatDBRef = database?.getReference("/student_chat")
    }

    private fun updateUI() {
        retrieveChatFromCloud()
        txt_chat.text.clear()
    }

    private fun retrieveChatFromCloud() {
        if (user == null) {
            Toast.makeText(this@ChatActivity, "Erro obtendo usuário corrente!", Toast.LENGTH_LONG).show()
            performLogOut()
            return
        }
        if (studentId == null) {
            Toast.makeText(this@ChatActivity, "Erro obtendo estudante!", Toast.LENGTH_LONG).show()
            return
        }
        if (chatDBRef == null) {
            Toast.makeText(this@ChatActivity, "Erro obtendo chat!", Toast.LENGTH_LONG).show()
            return
        }
        val chatListener = object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Erro atualizando chat! ${error.toException()}", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(chatSnapshot: DataSnapshot) {
                if (chatSnapshot.exists()) {
                    chat.clear()
                    chatSnapshot.children.forEach { snap ->
                        val message: Chat? = snap.getValue(Chat::class.java)
                        if (message != null) {
                            chat.add(message)
                        }
                    }
                    chatAdapter.setData(chat)
                }
            }
        }
        chatDBRef!!.child(user!!.uid).child(studentId).addValueEventListener(chatListener)
    }

    private fun sendMsg() {
        val chatMessage = txt_chat.text.toString()

        if (chatMessage.trim() == "") { return }
        if (user == null) {
            Toast.makeText(this@ChatActivity, "Erro obtendo usuário corrente!", Toast.LENGTH_LONG).show()
            performLogOut()
            return
        }
        if (studentId == null) {
            Toast.makeText(this@ChatActivity, "Erro obtendo estudante!", Toast.LENGTH_LONG).show()
            return
        }
        if (chatDBRef == null) {
            Toast.makeText(this@ChatActivity, "Erro obtendo chat!", Toast.LENGTH_LONG).show()
            return
        }
        val messageId = UUID.randomUUID().toString()
        val timeStampRequest: MutableMap<String, String> = ServerValue.TIMESTAMP
        val message = Chat(messageId, chatMessage, studentId!!, user!!.uid, mutableMapOf(Pair("timestamp", timeStampRequest)))
        chatDBRef!!.child(user!!.uid).child(studentId).child(messageId).setValue(message)
        chatDBRef!!.child(studentId).child(user!!.uid).child(messageId).setValue(message)
        updateUI()
    }

    private fun performLogOut() {
        auth!!.signOut()
        val intent = Intent(this@ChatActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
