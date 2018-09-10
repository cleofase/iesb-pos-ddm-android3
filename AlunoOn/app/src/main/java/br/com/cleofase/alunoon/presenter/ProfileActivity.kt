package br.com.cleofase.alunoon.presenter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.New
import br.com.cleofase.alunoon.entity.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.*
import kotlinx.android.synthetic.main.activity_student_detail.*
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    private val CAMERA = 1
    private val REQUEST_PERMISSION = 1
    private val PHOTO_NAME: String = "my_profile_photo"
    private val PHOTO_MAX_SIZE: Long = 100 * 1024
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var studentDBRef: DatabaseReference? = null
    private var newDBRef: DatabaseReference? = null
    private var studentNewsDBRef: DatabaseReference? = null
    private var studentStoreRef: StorageReference? = null
    private var student: Student = Student()
    private var news: MutableList<New> = mutableListOf()
    private var photoData: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_detail)
        btn_save.setOnClickListener { saveStudent() }
        btn_change_password.setOnClickListener { changePassword() }
        btn_logout.setOnClickListener { performLogOut() }
        img_photo.setOnClickListener { tryCapturePhoto() }
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
        storage = FirebaseStorage.getInstance()
        studentStoreRef = storage?.getReference("student")
    }

    private fun updateUI() {
        retrieveStudentProfileFromCloud()
    }

    private fun retrieveStudentProfileFromCloud() {
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
                    if (student.photo_url.trim().isNotEmpty()) {
                        retrieveStudentPhotoFromCloudWith(student.photo_url)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Erro recuperando Estudante: ${error.toException()}", Toast.LENGTH_LONG).show()
            }
        }
        studentDBRef!!.child(user!!.uid).addListenerForSingleValueEvent(studentListener)
    }

    private fun retrieveStudentPhotoFromCloudWith(photoStoreRef: String) {
        val storageRef: StorageReference? = storage?.getReference(photoStoreRef)
        if (storageRef == null) {
            Toast.makeText(this@ProfileActivity, "Erro ao obter referência ao armazenamento!", Toast.LENGTH_LONG).show()
            return
        }
        storageRef!!.getBytes(PHOTO_MAX_SIZE)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val photoByteArray = task.result
                        photoData = photoByteArray
                        val photoBMP = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.size)
                        photoBMP.let {
                            img_photo.setImageBitmap(photoBMP)
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "Erro baixando foto do perfil: ${task.exception.toString()}", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun saveStudent() {
        saveStudentPhoto() {
            saveStudentInfo() {
                generateNews() {
                    finish()
                }
            }
        }
    }

    private fun saveStudentInfo(completion: () -> Unit) {
        if (user == null) {
            Toast.makeText(this@ProfileActivity, "Erro obtendo usuário corrente!", Toast.LENGTH_LONG).show()
            performLogOut()
            return
        }
        if (studentDBRef == null) {
            Toast.makeText(this@ProfileActivity, "Erro ao obter referência do banco de dados!", Toast.LENGTH_LONG).show()
            return
        }
        student.name = txt_name.text.toString()
        student.enrolling = txt_enrolling.text.toString()
        student.phone = txt_phone.text.toString()
        student.uuid = user!!.uid
        student.email = txt_email.text.toString()
        studentDBRef!!.child(user!!.uid).setValue(student).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@ProfileActivity, "Informações salvas com sucesso!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@ProfileActivity, "Erro ao salvar informações: ${task.exception.toString()}", Toast.LENGTH_LONG).show()
            }
            completion()
        }
    }

    private fun saveStudentPhoto(completion: () -> Unit) {
        val photoData = photoData
        if (photoData == null) { return } else
        if (user == null) {
            Toast.makeText(this@ProfileActivity, "Erro obtendo usuário corrente!", Toast.LENGTH_LONG).show()
            performLogOut()
            return
        }
        if (studentStoreRef == null) {
            Toast.makeText(this@ProfileActivity, "Erro ao obter referência ao armazenamento!", Toast.LENGTH_LONG).show()
            return
        }
        studentStoreRef!!.child(user!!.uid).child(PHOTO_NAME).putBytes(photoData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        student.photo_url = task.result.metadata!!.path
                    } else {
                        student.photo_url = ""
                        Toast.makeText(this@ProfileActivity, "Erro ao salvar photo: ${task.exception.toString()}", Toast.LENGTH_LONG).show()
                    }
                    completion()
                }

    }

    private fun generateNews(completion: () -> Unit) {
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
                completion()
            }
            override fun onDataChange(newSnapshot: DataSnapshot) {
                if (newSnapshot.exists()) {
                    news.clear()
                    newSnapshot.children.mapNotNullTo(news) {it.getValue<New>(New::class.java)}
                    studentNewsDBRef!!.child(user!!.uid).setValue(news)
                    completion()
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

    private fun tryCapturePhoto() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION)
            return
        }
        intentToCaptureImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_PERMISSION -> {
                if (permissions.contains(Manifest.permission.CAMERA) && grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                    intentToCaptureImage()
                }
            }
        }
    }

    private fun intentToCaptureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageStream = ByteArrayOutputStream()
        val imageData = data!!.extras!!.get("data") as Bitmap
        img_photo.setImageBitmap(imageData)
        imageData.compress(Bitmap.CompressFormat.PNG, 100, imageStream)
        photoData = imageStream.toByteArray()
    }

}
