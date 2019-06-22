package com.arghyam.notification.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arghyam.R
import kotlinx.android.synthetic.main.activity_display_discharge_data.*
import kotlinx.android.synthetic.main.activity_notification.*


class DisplayDischargeDataActivity : AppCompatActivity() {

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_discharge_data)

        initToolBar()
    }

    private fun initToolBar() {
        setSupportActionBar(display_discharge_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
