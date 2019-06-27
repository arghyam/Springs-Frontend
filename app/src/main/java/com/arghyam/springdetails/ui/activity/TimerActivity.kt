package com.arghyam.springdetails.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.springdetails.adapter.TimerAdapter
import com.arghyam.springdetails.interfaces.TimerInterface
import com.arghyam.springdetails.models.TimerModel
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*

class TimerActivity : AppCompatActivity() {

    private var milliSecondTime: Long = 0L
    private var startTime: Long = 0L
    private lateinit var handler: Handler
    private var seconds: Int = 0
    private var minutes: Int = 0
    private var isTimerRunning: Boolean = false
    private var timerList: ArrayList<TimerModel> = ArrayList()
    private lateinit var timerAdapter: TimerAdapter

    private var selectedTimerItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        init()
    }

    private fun init() {
        initTimeFetch()
        initItems()
        initToolbar()
        initStartButton()
        initRecyclerView()
        initTimerDone()
        validateListener()
    }

    private fun validateListener() {
        if (validate()){
            timerDone.setBackgroundColor(resources.getColor(R.color.cornflower_blue))

        }
        else{
            timerDone.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        }

    }

    private fun validate(): Boolean {
        return timerList[0].seconds == 0 || timerList[1].seconds == 0 || timerList[2].seconds == 0
    }

    private fun initTimeFetch() {
        var intent = intent
        var args: Bundle = intent.getBundleExtra("Bundle")
        if (args.getSerializable("ArrayList") != null) {
            timerList = args.getSerializable("ArrayList") as ArrayList<TimerModel>
            updateAverage()
        }
    }

    private fun initTimerDone() {
        timerDone.setOnClickListener {
            if (!validate()){
                returnIntent()
            }
            else
                ArghyamUtils().longToast(this,"Please set the time first")
        }
    }

    private fun returnIntent() {
        var dataIntent: Intent = Intent().apply {
            putExtra("timerList", timerList)
        }
        if (!isTimerSetEmpty()) {
            setResult(Activity.RESULT_OK, dataIntent)
            finish()
        } else {
            setResult(Activity.RESULT_CANCELED, dataIntent)
            finish()
        }
    }

    private fun isTimerSetEmpty(): Boolean {
        return timerList.any { t -> t.seconds == 0 }
    }

    private fun initRecyclerView() {
        timerAdapter = TimerAdapter(timerList, this@TimerActivity, timerInterface)
        attempt_recycler_view.layoutManager = LinearLayoutManager(this@TimerActivity)
        attempt_recycler_view.adapter = timerAdapter
        if (timerList.size == 0)
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
        timerButton.background = ContextCompat.getDrawable(this, R.drawable.stop_button)
        timerButton.setTextColor(Color.parseColor("#0D65D9"))

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
        timerButton.background = ContextCompat.getDrawable(this, R.drawable.start_button)
        timerButton.setTextColor(Color.parseColor("#ffffff"))
        validateListener()

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
            if (seconds == 5975){
                ArghyamUtils().longToast(this@TimerActivity,"Longer than expected")
                Log.e("Anirudh","Timer: " +  ArghyamUtils().secondsToMinutes(seconds))
                handler.postDelayed(this, 0)
                timerButton.performClick()
            }
        }

    }

    private var timerInterface = object : TimerInterface {
        override fun onItemSelected(position: Int) {
            if (!isTimerRunning) {
                selectedTimerItem = position
                timerAdapter.updateSelectedItem(position)
            }

        }

        override fun onRemoveClicked(position: Int) {
            if (!isTimerRunning) {
                timerList[position].seconds = 0
                timerAdapter.notifyDataSetChanged()
                updateAverage()
                validateListener()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        returnIntent()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        returnIntent()
    }
}
