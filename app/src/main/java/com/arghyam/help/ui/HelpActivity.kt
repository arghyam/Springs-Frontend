package com.arghyam.help.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arghyam.R
import kotlinx.android.synthetic.main.activity_help.*
import android.text.style.URLSpan
import android.text.SpannableString
import android.text.Spannable
import android.widget.TextView
import android.text.TextPaint





class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        init()

    }

    private fun init() {
        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
