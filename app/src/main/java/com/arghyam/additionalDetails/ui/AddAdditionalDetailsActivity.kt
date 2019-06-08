package com.arghyam.additionalDetails.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.arghyam.commons.utils.ArghyamUtils
import kotlinx.android.synthetic.main.content_add_additional_details.*
import java.util.*
import kotlin.collections.ArrayList

class AddAdditionalDetailsActivity : AppCompatActivity(), CalenderAdapter.OnRecyclerViewItemClickListener {
    internal var calender: ArrayList<String> = ArrayList()
    internal var selectedMonth: ArrayList<Int> = ArrayList()
    internal var selectedMonthNames: ArrayList<String> = ArrayList()
    internal var seasonality: String = ""

    private var waterUse: ArrayList<String> = ArrayList()

    private lateinit var perennialRadio: RadioButton
    private lateinit var seasonalRadio: RadioButton
    private lateinit var checkBoxDomestic: CheckBox
    private lateinit var checkBoxIrrigation: CheckBox
    private lateinit var checkBoxIndustrial: CheckBox
    private lateinit var checkBoxLivestock: CheckBox
    private lateinit var checkBoxOthers: CheckBox
    private lateinit var houseHoldNumber: EditText


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
        initListener()
    }

    private fun initListener() {
        initCheckBoxListeners()
        initRadioButtonListeners()
        initEditTextListener()
    }

    private fun initEditTextListener() {
        isTextWritten()
    }

    private fun initRadioButtonListeners() {
        if (isRadioButtonClicked()) {
            var validated: Boolean = validateListener()
            if (validated) {
                submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            } else {
                submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
            }
        }
    }

    private fun initCheckBoxListeners() {
        othersListener()
        domesticListener()
        irrigationListener()
        industrialListener()
        livestockListener()
    }

    private fun livestockListener() {
        checkBoxLivestock.setOnCheckedChangeListener { buttonView, isChecked ->
            var validated: Boolean = validateListener()
            if (validated) {
                submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            } else {
                submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
            }
        }
    }

    private fun industrialListener() {
        checkBoxIndustrial.setOnCheckedChangeListener { buttonView, isChecked ->
            var validated: Boolean = validateListener()
            if (validated) {
                submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            } else {
                submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
            }
        }

    }

    private fun irrigationListener() {
        checkBoxIrrigation.setOnCheckedChangeListener { buttonView, isChecked ->
            var validated: Boolean = validateListener()
            if (validated) {
                submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            } else {
                submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
            }
        }

    }

    private fun domesticListener() {
        checkBoxDomestic.setOnCheckedChangeListener { buttonView, isChecked ->

            var validated: Boolean = validateListener()
            if (validated) {
                submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            } else {
                submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
            }
        }
    }

    private fun othersListener() {
        checkBoxOthers.setOnCheckedChangeListener { buttonView, isChecked ->

            var validated: Boolean = validateListener()
            if (validated) {
                submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            } else {
                submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
            }

        }
    }

    private fun isTextWritten() {
        houseHoldNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                var validated: Boolean = validateListener()
                if (validated) {
                    submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                } else {
                    submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                }
            }
        })
    }

    private fun isRadioButtonClicked(): Boolean {
        if ((radioGroup_character.checkedRadioButtonId != -1)) {
            selectedMonthNames.clear()
            getSeasonality()
            if (seasonality.equals("Seasonal")) {
                return selectedMonth.size != 0
            }
            return true
        } else {
            return false
        }
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
        if (selectedMonth.contains(position + 1)) {
            selectedMonth.remove(position + 1)
        } else {
            selectedMonth.add(position + 1)
        }
        select_month_count.text = "${selectedMonth.size} selected"

        if (selectedMonth.size > 0) {
            var validated: Boolean = validateListener()
            if (validated) {
                submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            } else {
                submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
            }
        } else {
            submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
        }
        Log.d("month", "" + select_month_count.text)
        Log.e("Month", selectedMonth.toString())
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
            initRadioButtonListeners()

        }
        initRecyclerview()
        initSubmitClickListener()
    }

    private fun initSubmitClickListener() {
        submit.setOnClickListener {
            var validated: Boolean = validateData()
            if (validated)
                saveData()
        }

    }

    private fun validateData(): Boolean {
        return checkSeasonality() && checkUsages() && checkHouseHold()
    }

    private fun checkSeasonality(): Boolean {
        if ((radioGroup_character.checkedRadioButtonId != -1)) {
            selectedMonthNames.clear()
            getSeasonality()
            if (seasonality.equals("Seasonal")) {
                if (selectedMonth.size != 0) {
                    return true
                } else {
                    ArghyamUtils().longToast(this, getString(R.string.select_months))
                    return false
                }
            }
            return true
        } else {
            ArghyamUtils().longToast(this, getString(R.string.select_season))
            return false
        }
    }

    private fun checkUsages(): Boolean {
        if (checkBoxOthers.isChecked || checkBoxLivestock.isChecked || checkBoxIndustrial.isChecked ||
            checkBoxIrrigation.isChecked || checkBoxDomestic.isChecked
        ) {
            return true
        } else {
            ArghyamUtils().longToast(this, getString(R.string.select_spring))
            return false
        }
    }

    private fun checkHouseHold(): Boolean {
        if (houseHoldNumber.text == null || houseHoldNumber.text.toString().equals("")) {
            ArghyamUtils().longToast(this, getString(R.string.enter_household))
            return false
        } else
            return true
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
        selectedMonthNames.clear()
        getSeasonality()
        if (seasonality.equals("Seasonal")) {
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

    private fun getSeasonality() {
        if (perennialRadio.isChecked) {
            seasonality = "Perennial"
        } else {
            seasonality = "Seasonal"
            convertToNames()
        }
    }

    private fun getSelectedCheckboxes() {
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

    private fun convertToNames() {
        selectedMonth.sort()
        for (month in selectedMonth) {
            when (month) {
                1 -> {
                    if (!selectedMonthNames.contains("Jan"))
                        selectedMonthNames.add("Jan")
                }
                2 -> {
                    if (!selectedMonthNames.contains("Feb"))
                        selectedMonthNames.add("Feb")
                }
                3 -> {
                    if (!selectedMonthNames.contains("Mar"))
                        selectedMonthNames.add("Mar")
                }
                4 -> {
                    if (!selectedMonthNames.contains("Apr"))
                        selectedMonthNames.add("Apr")
                }
                5 -> {
                    if (!selectedMonthNames.contains("May"))
                        selectedMonthNames.add("May")
                }
                6 -> {
                    if (!selectedMonthNames.contains("Jun"))
                        selectedMonthNames.add("Jun")
                }
                7 -> {
                    if (!selectedMonthNames.contains("Jul"))
                        selectedMonthNames.add("Jul")
                }
                8 -> {
                    if (!selectedMonthNames.contains("Aug"))
                        selectedMonthNames.add("Aug")
                }
                9 -> {
                    if (!selectedMonthNames.contains("Sep"))
                        selectedMonthNames.add("Sep")
                }
                10 -> {
                    if (!selectedMonthNames.contains("Oct"))
                        selectedMonthNames.add("Oct")
                }
                11 -> {
                    if (!selectedMonthNames.contains("Nov"))
                        selectedMonthNames.add("Nov")
                }
                12 -> {
                    if (!selectedMonthNames.contains("Dec"))
                        selectedMonthNames.add("Dec")
                }

            }
        }
    }

    private fun validateListener(): Boolean {
        return checkUsageForListener() && checkSeasonalityForListener() && checkHouseHoldForListener()
    }

    private fun checkHouseHoldForListener(): Boolean {
        return !(houseHoldNumber.text == null || houseHoldNumber.text.toString().equals(""))
    }

    private fun checkSeasonalityForListener(): Boolean {
        if ((radioGroup_character.checkedRadioButtonId != -1)) {
            selectedMonthNames.clear()
            getSeasonality()
            if (seasonality.equals("Seasonal")) {
                return selectedMonth.size != 0
            }
            return true
        } else {
            return false
        }
    }

    private fun checkUsageForListener(): Boolean {
        return checkBoxOthers.isChecked || checkBoxLivestock.isChecked || checkBoxIndustrial.isChecked ||
                checkBoxIrrigation.isChecked || checkBoxDomestic.isChecked
    }
}

