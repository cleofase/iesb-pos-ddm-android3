package br.com.cleofase.alunoon.view

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.New
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class NewsAdapter(private val context: Context): RecyclerView.Adapter<NewViewHolder>() {
    private val PHOTO_MAX_SIZE: Long = 50 * 1024
    private var news: MutableList<New> = mutableListOf()

    fun setData(newNews: MutableList<New>) {
        news = newNews
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.new_cell, parent, false)
        return NewViewHolder(view)
    }

    override fun onBindViewHolder(cell: NewViewHolder, position: Int) {
        val currentNew = news[position]
        cell.txt_title_new.text = currentNew.title
        cell.txt_body_new.text = currentNew.body
        cell.txt_date_new.text = currentNew.date
        if (currentNew.photo_url.trim().isNotEmpty()) {
            retrievePhotoFromCloudWith(currentNew.photo_url, cell)
        }
    }

    override fun getItemCount(): Int {
        return news.size
    }

    private fun retrievePhotoFromCloudWith(photoStoreRef: String, cell: NewViewHolder) {
        val storageRef: StorageReference? = FirebaseStorage.getInstance().getReference(photoStoreRef)
        if (storageRef == null) {
            return
        }
        storageRef!!.getBytes(PHOTO_MAX_SIZE)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val photoByteArray = task.result
                        val photoBMP = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.size)
                        photoBMP.let {
                            cell.img_new.setImageBitmap(photoBMP)
                        }
                    }
                }
    }
}