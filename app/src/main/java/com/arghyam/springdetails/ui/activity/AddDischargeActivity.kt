package com.arghyam.springdetails.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import com.arghyam.commons.utils.Constants.STOP_WATCH_TIMER_RESULT_CODE
import kotlinx.android.synthetic.main.content_add_discharge.*

class AddDischargeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_discharge)
        init()
    }

    private fun init() {
        initStopWatchButton()
    }

    private fun initStopWatchButton() {
        stop_watch.setOnClickListener {
            var intent = Intent(this@AddDischargeActivity, TimerActivity::class.java)
            startActivityForResult(intent, STOP_WATCH_TIMER_RESULT_CODE)
        }
    }

}
