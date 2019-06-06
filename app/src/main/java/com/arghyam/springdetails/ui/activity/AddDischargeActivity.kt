package com.arghyam.springdetails.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arghyam.ArghyamApplication
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants.STOP_WATCH_TIMER_RESULT_CODE
import com.arghyam.springdetails.models.TimerModel
import com.arghyam.springdetails.repository.DischargeDataRepository
import com.arghyam.springdetails.viewmodel.AddDischargeDataViewModel
import kotlinx.android.synthetic.main.content_add_discharge.*
import javax.inject.Inject

class AddDischargeActivity : AppCompatActivity() {

    private var timerList: ArrayList<TimerModel> = ArrayList()
    @Inject
    lateinit var dischargeDataRepository: DischargeDataRepository

    private var addDischargeDataViewModel: AddDischargeDataViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_discharge)
        init()
    }

    private fun init() {
        initApplicationComponent()
        initRepository()
        initViewComponents()
        initClicks()
        initApiCalls()
    }

    private fun initApiCalls() {
        addDischargeDataViewModel?.getDischargeSuccess()?.observe(this@AddDischargeActivity, Observer {

        })
        addDischargeDataViewModel?.getDischargeError()?.observe(this@AddDischargeActivity, Observer {

        })
    }

    private fun initApplicationComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
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
                if (resultCode == Activity.RESULT_OK) {
                    attempt_details.visibility = View.VISIBLE
                    attempts.visibility = View.GONE
                    timerList = data?.getSerializableExtra("timerList") as ArrayList<TimerModel>
                    initTimerData()
                }
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


    private fun initRepository() {
        addDischargeDataViewModel = ViewModelProviders.of(this).get(AddDischargeDataViewModel::class.java)
        addDischargeDataViewModel?.setDischargeDataViewModel(dischargeDataRepository)
    }
}
