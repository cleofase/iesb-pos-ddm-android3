package br.com.cleofase.alunoon.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.cleofase.alunoon.R
import br.com.cleofase.alunoon.entity.New

class NewsAdapter(private val context: Context): RecyclerView.Adapter<NewViewHolder>() {
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
    }

    override fun getItemCount(): Int {
        return news.size
    }
}