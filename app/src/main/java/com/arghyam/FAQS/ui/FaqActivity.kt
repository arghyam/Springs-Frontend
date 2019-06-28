package com.arghyam.FAQS.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.FAQS.adapter.FaqAdapter
import com.arghyam.FAQS.model.FaqModel
import com.arghyam.R
import kotlinx.android.synthetic.main.activity_add_discharge.*
import kotlinx.android.synthetic.main.content_faq.*

class FaqActivity : AppCompatActivity() {
    private var faqList = ArrayList<FaqModel>()
    private var goBack: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        init()
    }

    private fun init() {
        initRecyclerView()
        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun initRecyclerView() {
        FAQ_recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = this.let { FaqAdapter(faqList, it) }
        FAQ_recyclerview.adapter = adapter

        faqList.add(FaqModel(getString(R.string.faq_title_one), getString(R.string.faq_question_one)))

        faqList.add(FaqModel(getString(R.string.faq_title_two), getString(R.string.faq_question_two)))

        faqList.add(FaqModel(getString(R.string.faq_title_three), getString(R.string.faq_question_three)))

    }


}
