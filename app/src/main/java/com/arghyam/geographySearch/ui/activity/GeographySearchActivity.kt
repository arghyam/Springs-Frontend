package com.arghyam.geographySearch.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
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
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.search.ui.SearchFragment
import kotlinx.android.synthetic.main.content_geography_search.*
import kotlinx.android.synthetic.main.list_spring.*

class GeographySearchActivity : AppCompatActivity(), SearchInterface {

    var statesFlag: Boolean = false
    var districtsFlag:Boolean = false
    var blockFlag:Boolean = false
    var stateOsid:String = ""
    var districtOsid:String = ""
    var subDistrictOsid:String = ""
    var villageOsid:String = ""
    var cityOsid:String = ""
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

    private fun initClick() {
        search_state.setOnClickListener {
            val fragment = StateFragment.newInstance()
            addFragment(fragment)
        }
        search_district.setOnClickListener {
            if(statesFlag){
                bundle.putString("osid", stateOsid)
                val fragment = DistrictFragment.newInstance()
                fragment.arguments = bundle
                addFragment(fragment)
            }
            else
                ArghyamUtils().longToast(this,"Please select the state before you select the district")
        }

        search_block.setOnClickListener {
            if (districtsFlag){
                bundle.putString("osid", districtOsid)
                val fragment = BlockFragment.newInstance()
                fragment.arguments = bundle
                addFragment(fragment)
            }
            else
                ArghyamUtils().longToast(this,"Please select the district before you select the block")
        }

        town_name.setOnClickListener {
            if (!radioUrban.isChecked)
                ArghyamUtils().longToast(this,"Please check Urban before you select the town")
            else if (blockFlag){
                bundle.putString("osid", subDistrictOsid)
                val fragment = TownFragment.newInstance()
                fragment.arguments = bundle
                addFragment(fragment)
            }
            else
                ArghyamUtils().longToast(this,"Please select the block before you select the urban")
        }

        panchayat_name.setOnClickListener {

            if (!radioRural.isChecked)
                ArghyamUtils().longToast(this,"Please check Rural before you select the villages")
            else  if (blockFlag) {
                bundle.putString("osid", subDistrictOsid)
                val fragment = PanchayatFragment.newInstance()
                fragment.arguments = bundle
                addFragment(fragment)
            }
            else
                ArghyamUtils().longToast(this,"Please select the block before you select the rural")
        }
        search_button.setOnClickListener {
            if (cityOsid.isNotEmpty()){
                val intent = Intent()
                intent.putExtra("searchText",town_name.text)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            else if (villageOsid.isNotEmpty()){
                val intent = Intent()
                intent.putExtra("searchText",panchayat_name.text)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            else
                ArghyamUtils().longToast(this,"please select the area correctly")
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.geography_search, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }

    override fun getTitle(id: String, title: String, osid: String, type: Int) {
        Log.e("Geography search", "$title $osid $type")
        if (type == 1) {
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
        } else if (type == 2) {
            district_name.text = title
            districtsFlag = true
            block_name.text = ""
            town_name.text = ""
            panchayat_name.text = ""
            cityOsid = ""
            villageOsid = ""
            districtOsid = osid
        } else if (type == 3) {
            block_name.text = title
            blockFlag = true
            town_name.text = ""
            panchayat_name.text = ""
            cityOsid = ""
            villageOsid = ""
            subDistrictOsid = osid
        } else if (type == 4) {
            town_name.text = title
            cityOsid = osid
        } else if (type == 5) {
            panchayat_name.text = title
            villageOsid = osid
        } else {
            state_name.text = ""
            district_name.text = ""
            block_name.text = ""
            town_name.text = ""
            panchayat_name.text = ""
            cityOsid = ""
            villageOsid = ""
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
