package com.arghyam.additionalDetails.ui

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.arghyam.R
import com.arghyam.additionalDetails.adapter.CalenderAdapter
import kotlinx.android.synthetic.main.content_add_additional_details.*

class AddAdditionalDetailsActivity : AppCompatActivity(), CalenderAdapter.OnRecyclerViewItemClickListener {


    internal var calender: ArrayList<String> = ArrayList()
    internal var selectedMonth: ArrayList<Int> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_additional_details)
        init()
    }


    private fun init() {
        initToolbar()
        addCalender()
        initRecyclerview()
        initClick()
    }

    private fun initToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun selectedMonth(position: Int) {
        if (selectedMonth.contains(position+1)) {
            selectedMonth.remove(position+1)
        } else {
            selectedMonth.add(position+1)
        }
        if(selectedMonth.size>=12){
            (calenderRecyclerview.adapter as CalenderAdapter).clear()
            selectedMonth.clear()
            radioButton_pernnial.isChecked = true
            radioButton_seasonal1.isChecked = false
        }
        select_month_count.text = "${selectedMonth.size} selected"
        Log.d("month",""+select_month_count.text)
    }


    private fun initClick() {
        radioGroup_character?.setOnCheckedChangeListener { group, checkedId ->
            if (R.id.radioButton_seasonal1 == checkedId) {
                calenderRecyclerview.visibility = VISIBLE
                select_month_count.visibility = VISIBLE
                select_month_count.text = "${selectedMonth.size} selected"
                radioButton_seasonal1?.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_collapse_up),
                    null
                )

            } else {
                calenderRecyclerview.visibility = GONE
                select_month_count.visibility = GONE
                radioButton_seasonal1?.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_collapse_down),
                    null
                )

            }

        }
        initRecyclerview()
    }

    private fun initRecyclerview() {

        val recyclerViewAdapter = CalenderAdapter(calender, this@AddAdditionalDetailsActivity, this)
        val linearLayoutManager = GridLayoutManager(this, 4)
        calenderRecyclerview.layoutManager = linearLayoutManager
        calenderRecyclerview.setHasFixedSize(true)
        calenderRecyclerview.adapter = recyclerViewAdapter

    }

    private fun addCalender() {
        calender.add("Jan")
        calender.add("Feb")
        calender.add("Mar")
        calender.add("Apr")
        calender.add("May")
        calender.add("Jun")
        calender.add("Jul")
        calender.add("Aug")
        calender.add("Sep")
        calender.add("Oct")
        calender.add("Nov")
        calender.add("Dec")
    }

}

