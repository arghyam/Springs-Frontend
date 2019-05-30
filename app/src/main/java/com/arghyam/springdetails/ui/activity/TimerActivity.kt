package com.arghyam.springdetails.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*

class TimerActivity : AppCompatActivity() {

    var milliSecondTime: Long = 0L
    var startTime: Long = 0L
    lateinit var handler: Handler
    var seconds: Int = 0
    var minutes: Int = 0
    var isTimerRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        init()
    }

    private fun init() {
        initItems()
        initToolbar()
        initStartButton()
    }

    private fun initItems() {
        handler = Handler()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun initStartButton() {
        timerButton.setOnClickListener {
            isTimerRunning = !isTimerRunning
            if (isTimerRunning) {
                startTimer()
            } else {
                resetTimer()
            }
        }
    }

    private fun startTimer() {
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(runnable, 0)
        timerButton.text = resources.getText(R.string.stop)
    }

    private fun resetTimer() {
        handler.removeCallbacks(runnable)
        timerButton.text = resources.getText(R.string.start)
        milliSecondTime = 0L
        startTime = 0L
        seconds = 0
        minutes = 0
        timer.text = "00:00"
    }

    private var runnable: Runnable = object : Runnable {
        override fun run() {
            milliSecondTime = SystemClock.uptimeMillis() - startTime
            seconds = (milliSecondTime / 1000).toInt()
            minutes = seconds / 60
            seconds %= 60
            timer.text = "" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
            handler.postDelayed(this, 0)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
