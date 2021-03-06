package com.arghyam.additionalDetails.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.additionalDetails.adapter.CalenderAdapter
import com.arghyam.additionalDetails.model.AdditionalDetailsModel
import com.arghyam.additionalDetails.model.RequestAdditionalDetailsDataModel
import com.arghyam.additionalDetails.repository.AdditionalDetailsRepository
import com.arghyam.additionalDetails.viewmodel.AddAdditionalDetailsViewModel
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.OnFocusLostListener
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import kotlinx.android.synthetic.main.content_add_additional_details.*
import kotlinx.android.synthetic.main.content_help.*
import kotlinx.android.synthetic.main.spring_details.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class AddAdditionalDetailsActivity : AppCompatActivity(), CalenderAdapter.OnRecyclerViewItemClickListener {
    internal var calender: ArrayList<String> = ArrayList()
    internal var selectedMonth: ArrayList<Int> = ArrayList()
    internal var selectedMonthNames: ArrayList<String> = ArrayList()
    internal var seasonality: String = ""
    var args = Bundle()

    private var waterUse: ArrayList<String> = ArrayList()

    private lateinit var perennialRadio: RadioButton
    private lateinit var seasonalRadio: RadioButton
    private lateinit var checkBoxDomestic: CheckBox
    private lateinit var checkBoxIrrigation: CheckBox
    private lateinit var checkBoxIndustrial: CheckBox
    private lateinit var checkBoxLivestock: CheckBox
    private lateinit var checkBoxOthers: CheckBox
    private lateinit var houseHoldNumber: EditText
    private var goBack: Boolean = false
    private lateinit var springCode: String
    private var springName: String? = null
    private var clickable = true


    private lateinit var mAdditionalDetailsViewModel: AddAdditionalDetailsViewModel

    @Inject
    lateinit var mAdditionalDataRepository: AdditionalDetailsRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_additional_details)
        init()
    }


    private fun init() {
        initComponent()

        initViews()
        initToolbar()
        addCalender()
        initRecyclerview()
        initRepository()
        observeData()
        initClick()

        initListener()
        initIntent()
        initSet()
    }

    private fun initIntent() {
        var dataIntent: Intent = intent
        springCode = dataIntent.getStringExtra("SpringCode")
        springName = dataIntent.getStringExtra("SpringName")
        Log.d("Anirudh", "" + springCode)
    }


    private fun observeData() {
        mAdditionalDetailsViewModel.getAdditionalDataSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("AddAdditionalDetails", "data added")
            ArghyamUtils().longToast(this, "Additional data successfully added")
            var dataIntent: Intent = Intent().apply {
                putExtra("DataBundle", args)
            }
            setResult(Activity.RESULT_OK, dataIntent)
            finish()
        })

        mAdditionalDetailsViewModel.getAdditionalDataError().observe(this, androidx.lifecycle.Observer {
            Log.d("AddAdditionalDetails", "Api Error")
        })
    }

    private fun initRepository() {
        mAdditionalDetailsViewModel = ViewModelProviders.of(this).get(AddAdditionalDetailsViewModel::class.java)
        mAdditionalDetailsViewModel.setAdditionalDataRepository(mAdditionalDataRepository)
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

    private fun initSet(){
       var  additionalSpring : String = "Add additional details for "+ "<b> ${springName} </b>"
        additional_spring.text = Html.fromHtml(additionalSpring)
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
        houseHoldNumber = findViewById(R.id.numberOfHouseHold)
    }

    private fun initToolbar() {
        val toolbar = toolbar as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = "Additional Details      "
    }

    override fun onSupportNavigateUp(): Boolean {
        if (goBack) {
            finish()
        } else {
            ArghyamUtils().longToast(this, "Are you sure you want to go back? You will lose all entered data.")
            startTimer()
        }
        goBack = true
        return true
    }

    override fun onBackPressed() {
        if (goBack) {
            finish()
        } else {
            ArghyamUtils().longToast(this, "Are you sure you want to go back? You will lose all entered data.")
            startTimer()
        }
        goBack = true
    }

    private fun startTimer() {
        Handler().postDelayed({
            goBack = false
        }, 2000)
    }


    override fun selectedMonth(position: Int) {
        if (selectedMonth.contains(position + 1)) {
            selectedMonth.remove(position + 1)
        } else {
            selectedMonth.add(position + 1)
        }
        if (selectedMonth.size >= 12) {
            (calenderRecyclerview.adapter as CalenderAdapter).clear()
            selectedMonth.clear()
            radioButton_pernnial.isChecked = true
            radioButton_seasonal1.isChecked = false
        }
        select_month_count.text = "${selectedMonth.size} selected"
        Log.d("month", "" + select_month_count.text)

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

                selectedMonth.clear()
                (calenderRecyclerview.adapter as CalenderAdapter).clear()

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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun initSubmitClickListener() {
        submit.setOnClickListener {
            var validated: Boolean = validateData()
            if (validated && clickable) {
                clickable = false
                saveData()
            }
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
        return if (checkBoxOthers.isChecked || checkBoxLivestock.isChecked || checkBoxIndustrial.isChecked ||
            checkBoxIrrigation.isChecked || checkBoxDomestic.isChecked
        ) {
            true
        } else {
            ArghyamUtils().longToast(this, getString(R.string.select_spring))
            false
        }
    }

    private fun checkHouseHold(): Boolean {
        return if (houseHoldNumber.text == null || houseHoldNumber.text.toString().equals("")) {
            ArghyamUtils().longToast(this, getString(R.string.enter_household))
            false
        }else if(houseHoldNumber.text.length >= 9 ){
            ArghyamUtils().longToast(this, getString(R.string.enter_proper))
            false
        } else
            true
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



    private fun saveData() {
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


        var mRequestData = SharedPreferenceFactory(this).getString(Constants.USER_ID)?.let {
            AdditionalDetailsModel(
                springCode = springCode,
                seasonality = seasonality,
                usage = waterUse,
                months = selectedMonth,
                numberOfHousehold = houseNumber,
                userId = it
            )
        }?.let {
            RequestAdditionalDetailsDataModel(
                additionalInfo = it
            )
        }?.let {
            RequestModel(
                id = Constants.ADDITIONAL_DATA_ID,
                ver = BuildConfig.VER,
                ets = BuildConfig.ETS,
                params = Params(
                    did = "",
                    key = "",
                    msgid = ""
                ),
                request = it
            )
        }

        Log.e("Request", mRequestData.toString())
        if (mRequestData != null) {
            makeApiCall(mRequestData)
        }
    }


    private fun getSeasonality() {
        if (perennialRadio.isChecked) {
            seasonality = "Perennial"
            if (selectedMonth.size==0){
                selectedMonth.add(1)
                selectedMonth.add(2)
                selectedMonth.add(3)
                selectedMonth.add(4)
                selectedMonth.add(5)
                selectedMonth.add(6)
                selectedMonth.add(7)
                selectedMonth.add(8)
                selectedMonth.add(9)
                selectedMonth.add(10)
                selectedMonth.add(11)
                selectedMonth.add(12)
            }
            convertToNames()
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


    private fun makeApiCall(mRequestData: RequestModel) {
        mAdditionalDetailsViewModel.addAdditionalDetailsApi(this,springCode, mRequestData)

    }

    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

}

