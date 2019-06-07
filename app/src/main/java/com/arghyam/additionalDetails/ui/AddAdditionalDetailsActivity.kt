package com.arghyam.additionalDetails.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.arghyam.R
import com.arghyam.additionalDetails.adapter.CalenderAdapter
import kotlinx.android.synthetic.main.content_add_additional_details.*

class AddAdditionalDetailsActivity : AppCompatActivity(), CalenderAdapter.OnRecyclerViewItemClickListener {
    internal var calender: ArrayList<String> = ArrayList()
    internal var selectedMonth: ArrayList<Int> = ArrayList()
    internal var selectedMonthNames: ArrayList<String> = ArrayList()
    internal lateinit var seasonality : String

    private var waterUse : ArrayList<String> = ArrayList()

    private  lateinit var perennialRadio : RadioButton
    private lateinit var seasonalRadio : RadioButton
    private lateinit var checkBoxDomestic: CheckBox
    private lateinit var checkBoxIrrigation: CheckBox
    private lateinit var checkBoxIndustrial: CheckBox
    private lateinit var checkBoxLivestock: CheckBox
    private lateinit var checkBoxOthers: CheckBox
    private lateinit var houseHoldNumber : EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_additional_details)
        init()
    }

    private fun init() {
        initViews()
        initToolbar()
        addCalender()
        initRecyclerview()
        initClick()
    }

    private fun initViews() {
        checkBoxDomestic = findViewById<CheckBox>(R.id.domestic)
        checkBoxIrrigation = findViewById<CheckBox>(R.id.irrigation)
        checkBoxIndustrial = findViewById<CheckBox>(R.id.industrial)
        checkBoxLivestock = findViewById<CheckBox>(R.id.livestock)
        checkBoxOthers = findViewById<CheckBox>(R.id.others)
        perennialRadio = findViewById(R.id.radioButton_pernnial)
        seasonalRadio = findViewById(R.id.radioButton_seasonal1)
        houseHoldNumber = findViewById(R.id.mobile)
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
        select_month_count.text = "${selectedMonth.size} selected"
        Log.d("month",""+select_month_count.text)
        Log.e("Month",selectedMonth.toString())
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

        submit.setOnClickListener{
            saveData()
        }
    }

    private fun initRecyclerview() {

        var recyclerViewAdapter = CalenderAdapter(calender, this@AddAdditionalDetailsActivity, this)
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

    private fun saveData() {
        var args = Bundle()

        var houseNumber: Int = Integer.parseInt(houseHoldNumber.text.toString())
        getSelectedCheckboxes()
        getSeasonality()
        if(seasonality.equals("Seasonal")){
            args.putStringArrayList("SelectedMonths", selectedMonthNames)
        }
        args.putInt("HouseHoldNumbers", houseNumber)
        args.putCharSequence("Seasonality", seasonality)
        args.putStringArrayList("WaterUse", waterUse)


        var dataIntent: Intent = Intent().apply {
            putExtra("DataBundle", args)
        }
        setResult(Activity.RESULT_OK, dataIntent)
        finish()


    }

    private fun getSeasonality(){
        if (perennialRadio.isChecked) {
            seasonality = "Perennial"
        } else {
            seasonality = "Seasonal"
            convertToNames()

        }
    }

    private fun getSelectedCheckboxes(){
        if (checkBoxDomestic.isChecked) {
            waterUse.add(checkBoxDomestic.text.toString())
        }
        if (checkBoxIrrigation.isChecked) {
            waterUse.add(checkBoxIrrigation.text.toString())
        }
        if (checkBoxIndustrial.isChecked) {
            waterUse.add(checkBoxIndustrial.text.toString())
        }
        if (checkBoxLivestock.isChecked) {
            waterUse.add(checkBoxLivestock.text.toString())
        }
        if (checkBoxOthers.isChecked) {
            waterUse.add(checkBoxOthers.text.toString())
        }
    }

    private fun convertToNames(){
        for(month in selectedMonth){
            when(month){
                1 -> selectedMonthNames.add("Jan")
                2 -> selectedMonthNames.add("Feb")
                3 -> selectedMonthNames.add("Mar")
                4 -> selectedMonthNames.add("Apr")
                5 -> selectedMonthNames.add("May")
                6 -> selectedMonthNames.add("Jun")
                7 -> selectedMonthNames.add("Jul")
                8 -> selectedMonthNames.add("Aug")
                9 -> selectedMonthNames.add("Sep")
               10 -> selectedMonthNames.add("Oct")
               11 -> selectedMonthNames.add("Nov")
               12 -> selectedMonthNames.add("Dec")

            }
        }
    }
}

