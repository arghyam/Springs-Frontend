package com.arghyam.geographySearch.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arghyam.R
import kotlinx.android.synthetic.main.content_search.*

class GeographySearch : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geography_search)
        init()
    }

    private fun init() {
        initClick()
    }

    private fun initClick() {

    }
}
