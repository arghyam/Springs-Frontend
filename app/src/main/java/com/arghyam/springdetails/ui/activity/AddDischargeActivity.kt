package com.arghyam.springdetails.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants.STOP_WATCH_TIMER_RESULT_CODE
import com.arghyam.springdetails.models.TimerModel
import kotlinx.android.synthetic.main.activity_add_discharge.*
import kotlinx.android.synthetic.main.activity_spring_details.*
import kotlinx.android.synthetic.main.content_add_discharge.*

class AddDischargeActivity : AppCompatActivity() {

    private var timerList: ArrayList<TimerModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_discharge)
        init()
    }

    private fun init() {
        initViewComponents()
        initClicks()
        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initClicks() {
        initStopWatchButton()
        initViewAttempts()
    }

    private fun initViewAttempts() {
        view_attempts.setOnClickListener {
            if (attempts.visibility == VISIBLE) {
                attempts.visibility = GONE
            } else {
                attempts.visibility = VISIBLE
            }
        }
    }

    private fun initViewComponents() {
        attempt_details.visibility = GONE
    }

    private fun initStopWatchButton() {
        stop_watch.setOnClickListener {
            var intent = Intent(this@AddDischargeActivity, TimerActivity::class.java)
            startActivityForResult(intent, STOP_WATCH_TIMER_RESULT_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            STOP_WATCH_TIMER_RESULT_CODE -> {
                attempt_details.visibility = View.VISIBLE
                attempts.visibility = View.GONE
                timerList = data?.getSerializableExtra("timerList") as ArrayList<TimerModel>
                initTimerData()
            }
        }
    }

    private fun initTimerData() {
        attempt_details.visibility = VISIBLE
        average_body.visibility = VISIBLE
        view_attempts.visibility = VISIBLE
        averageTimer.text = ArghyamUtils().secondsToMinutes(timerList.map { item -> item.seconds }.average().toInt())
        attempt1.text = ArghyamUtils().secondsToMinutes(timerList[0].seconds)
        attempt2.text = ArghyamUtils().secondsToMinutes(timerList[1].seconds)
        attempt3.text = ArghyamUtils().secondsToMinutes(timerList[2].seconds)
        attempts.visibility = GONE
    }
}
