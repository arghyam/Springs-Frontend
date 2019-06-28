package com.arghyam.FAQS.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.FAQS.model.FaqModel
import com.arghyam.R
import com.arghyam.additionalDetails.adapter.CalenderAdapter
import com.arghyam.geographySearch.adapter.BlockAdapter
import kotlinx.android.synthetic.main.list_faq.view.*

class FaqAdapter (val faqList : ArrayList<FaqModel>, val context: Context) : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_faq, parent, false)
        return FaqAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
       return faqList.size
    }

    override fun onBindViewHolder(holder: FaqAdapter.ViewHolder, position: Int) {
        val faq: FaqModel = faqList[position]
        holder.title.text = faq.title
        holder.question.text = faq.question
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val title = view.faq_title
        val question = view.faq_question
    }
}