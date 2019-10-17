package com.arghyam.geographySearch.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.geographySearch.interfaces.SearchInterface
import com.arghyam.geographySearch.ui.fragment.*
import kotlinx.android.synthetic.main.content_geography_search.*

class GeographySearchActivity : AppCompatActivity(), SearchInterface {

    private var statesFlag: Boolean = false
    private var districtsFlag:Boolean = false
    private var blockFlag:Boolean = false
    private var stateOsid:String = ""
    private var districtOsid:String = ""
    private var subDistrictOsid:String = ""
    private var villageOsid:String = ""
    private var cityOsid:String = ""
    var bundle = Bundle()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geography_search)
        init()
    }

    private fun init() {
        this.title = "Search by Geography"
        initToolbar()
        initClick()
        validateListener()
    }


    private fun initToolbar() {
        val toolbar = toolbar as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = "Search by Geography"

    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(Activity.RESULT_CANCELED)
        finish()
        return true
    }
    private var stateClicked = false
    private var districtClicked = false
    private var blockClicked = false
    private var townClicked = false
    private var villageClicked = false
    private fun initClick() {
        search_state.setOnClickListener {
            if (!stateClicked){
                val fragment = StateFragment.newInstance()
                addFragment(fragment)
                stateClicked = true
            }
        }
        search_district.setOnClickListener {
            if(statesFlag && !districtClicked){
                bundle.putString("osid", stateOsid)
                val fragment = DistrictFragment.newInstance()
                fragment.arguments = bundle
                addFragment(fragment)
                districtClicked = true
            }
            else if (!statesFlag)
                ArghyamUtils().longToast(this,"Please select the state before you select the district")
        }

        search_block.setOnClickListener {
            if (districtsFlag && !blockClicked){
                bundle.putString("osid", districtOsid)
                val fragment = BlockFragment.newInstance()
                fragment.arguments = bundle
                addFragment(fragment)
                blockClicked = true

            }
            else if (!districtsFlag)
                ArghyamUtils().longToast(this,"Please select the district before you select the block")
        }

        town_name.setOnClickListener {
            if (!radioUrban.isChecked)
                ArghyamUtils().longToast(this,"Please check Urban before you select the town")
            else if (blockFlag && !townClicked){
                bundle.putString("osid", subDistrictOsid)
                val fragment = TownFragment.newInstance()
                fragment.arguments = bundle
                addFragment(fragment)
                townClicked = true
            }
            else if (!blockFlag)
                ArghyamUtils().longToast(this,"Please select the block before you select the urban")
        }

        panchayat_name.setOnClickListener {

            if (!radioRural.isChecked)
                ArghyamUtils().longToast(this,"Please check Rural before you select the villages")
            else  if (blockFlag && !villageClicked) {
                bundle.putString("osid", subDistrictOsid)
                val fragment = PanchayatFragment.newInstance()
                fragment.arguments = bundle
                addFragment(fragment)
                villageClicked = true
            }
            else if (!blockFlag)
                ArghyamUtils().longToast(this,"Please select the block before you select the rural")
        }
        search_button.setOnClickListener {
            when {
                cityOsid.isNotEmpty() -> {
                    val intent = Intent()
                    intent.putExtra("searchText",town_name.text)
                    setResult(Activity.RESULT_OK, intent)
                    Log.e("GeoSearch",this.javaClass.name.toString())

                    finish()
                }
                villageOsid.isNotEmpty() -> {
                    val intent = Intent()
                    intent.putExtra("searchText",panchayat_name.text)
                    setResult(Activity.RESULT_OK, intent)
                    Log.e("GeoSearch",this.javaClass.name.toString())

                    finish()
                }
                else -> ArghyamUtils().longToast(this,"please select the area correctly")
            }
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.geography_search, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }


    override fun isClicked(click: Boolean, type: Int) {
        Log.e("isclicked", click.toString())
        when (type) {
            1 -> {
                stateClicked = click
            }
            2 -> {
                districtClicked = click
            }
            3 -> {
                blockClicked = click
            }
            4 -> {
                townClicked = click
            }
            5 -> {
                villageClicked = click
            }
        }
    }

    override fun getTitle(id: String, title: String, osid: String, type: Int) {
        Log.e("Geography search", "$title $osid $type")
        when (type) {
            1 -> {
                state_name.text = title
                statesFlag = true
                districtsFlag = false
                blockFlag = false
                district_name.text = ""
                block_name.text = ""
                town_name.text = ""
                cityOsid = ""
                villageOsid = ""
                panchayat_name.text = ""
                stateOsid = osid
            }
            2 -> {
                district_name.text = title
                districtsFlag = true
                block_name.text = ""
                town_name.text = ""
                panchayat_name.text = ""
                cityOsid = ""
                villageOsid = ""
                districtOsid = osid
            }
            3 -> {
                block_name.text = title
                blockFlag = true
                town_name.text = ""
                panchayat_name.text = ""
                cityOsid = ""
                villageOsid = ""
                subDistrictOsid = osid
            }
            4 -> {
                town_name.text = title
                cityOsid = osid
            }
            5 -> {
                panchayat_name.text = title
                villageOsid = osid
            }
            else -> {
                state_name.text = ""
                district_name.text = ""
                block_name.text = ""
                town_name.text = ""
                panchayat_name.text = ""
                cityOsid = ""
                villageOsid = ""
            }
        }
        validateListener()
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.radioUrban ->
                    if (checked) {
                        radioRural.isChecked = false
                        panchayat_name.text = ""
                        villageOsid = ""
                    }
                R.id.radioRural ->
                    if (checked) {
                        radioUrban.isChecked = false
                        town_name.text = ""
                        cityOsid = ""
                    }
            }
            validateListener()
        }
    }

    private fun validateListener() {
        if (validate()){
            search_button.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            Log.e("search","true")
        }
        else{
            search_button.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
            Log.e("search","false")
        }

    }

    private fun validate(): Boolean {
        return cityOsid.isNotEmpty() || villageOsid.isNotEmpty()
    }

}
