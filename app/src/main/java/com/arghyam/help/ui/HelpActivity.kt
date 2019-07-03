package com.arghyam.help.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arghyam.R
import kotlinx.android.synthetic.main.activity_help.*
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.arghyam.commons.utils.URLSpanNoUnderline
import kotlinx.android.synthetic.main.content_help.*
import android.text.style.URLSpan
import android.text.Spannable
import com.arghyam.commons.utils.StringUtil
import java.net.URL


class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        init()

    }

    private fun init() {
        initToolbar()
        initEmail()
    }

    private fun initEmail() {
        StringUtil().removeUnderlines(email.text as Spannable)
        StringUtil().removeUnderlines(phone_1.text as Spannable)
        StringUtil().removeUnderlines(phone_2 .text as Spannable)
    }

    private fun initToolbar() {
        val toolbar = toolbar as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = "Help"
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
