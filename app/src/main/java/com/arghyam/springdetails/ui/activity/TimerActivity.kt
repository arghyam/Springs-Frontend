package com.arghyam.springdetails.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.springdetails.adapter.TimerAdapter
import com.arghyam.springdetails.interfaces.TimerInterface
import com.arghyam.springdetails.models.TimerModel
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*

class TimerActivity : AppCompatActivity() {

    var milliSecondTime: Long = 0L
    var startTime: Long = 0L
    lateinit var handler: Handler
    var seconds: Int = 0
    var minutes: Int = 0
    var isTimerRunning: Boolean = false
    private var timerList: ArrayList<TimerModel> = ArrayList()
    private lateinit var timerAdapter: TimerAdapter

    private var selectedTimerItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        init()
    }

    private fun init() {
        initItems()
        initToolbar()
        initStartButton()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        timerAdapter = TimerAdapter(timerList, this@TimerActivity, timerInterface)
        attempt_recycler_view.layoutManager = LinearLayoutManager(this@TimerActivity)
        attempt_recycler_view.adapter = timerAdapter
        initTimerData()
    }

    private fun initTimerData() {
        timerList.add(TimerModel(0))
        timerList.add(TimerModel(0))
        timerList.add(TimerModel(0))
        timerAdapter.notifyDataSetChanged()
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
        updateCurrentTimer()
        handler.removeCallbacks(runnable)
        timerButton.text = resources.getText(R.string.start)
        milliSecondTime = 0L
        startTime = 0L
        seconds = 0
        minutes = 0
        timer.text = "00:00"
    }

    private fun updateCurrentTimer() {
        if (selectedTimerItem <= 2) {
            timerList[selectedTimerItem].seconds = seconds
            timerAdapter.notifyDataSetChanged()
            if (selectedTimerItem < 2) {
                selectedTimerItem++
            }
            timerAdapter.updateSelectedItem(selectedTimerItem)
            updateAverage()
        }
    }

    private fun updateAverage() {
        var average = timerList.map { item -> item.seconds }.average().toInt()
        average_timer.text = ArghyamUtils().secondsToMinutes(average)
    }

    private var runnable: Runnable = object : Runnable {
        override fun run() {
            milliSecondTime = SystemClock.uptimeMillis() - startTime
            seconds = (milliSecondTime / 1000).toInt()
            timer.text = ArghyamUtils().secondsToMinutes(seconds)
            handler.postDelayed(this, 0)
        }

    }

    private var timerInterface = object : TimerInterface {
        override fun onItemSelected(position: Int) {
            selectedTimerItem = position
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
