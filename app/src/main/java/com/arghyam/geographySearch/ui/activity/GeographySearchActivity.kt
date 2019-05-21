package com.arghyam.geographySearch.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.arghyam.R
import com.arghyam.geographySearch.interfaces.SearchInterface
import com.arghyam.geographySearch.ui.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.content_geography_search.*
import kotlinx.android.synthetic.main.content_search.*

class GeographySearchActivity : AppCompatActivity(), SearchInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geography_search)
        init()
    }

    private fun init() {
        this.setTitle("Search by Geography")
        initClick()
    }

    private fun initClick() {
        search_state.setOnClickListener {
            val fragment = StateFragment.newInstance()
            addFragment(fragment)
        }
        search_district.setOnClickListener {
            val fragment = DistrictFragment.newInstance()
            addFragment(fragment)
        }

        search_block.setOnClickListener {
            val fragment = BlockFragment.newInstance()
            addFragment(fragment)

        }

        town_name.setOnClickListener {
            val fragment = TownFragment.newInstance()
            addFragment(fragment)
        }

        panchayat_name.setOnClickListener {
            val fragment = PanchayatFragment.newInstance()
            addFragment(fragment)
        }

    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.geography_search, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.getSimpleName())
            .commit()
    }

    override fun getTitle(id: String, title: String, type : Int) {
        Log.e("stefy", title +" "+ type)
        if(type == 1){
            state_name.text = title

        } else if(type == 2){
            district_name.text = title

        } else if (type == 3){
            block_name.text = title

        }else if (type == 4){
            town_name.text = title

        }else if (type == 5){
            panchayat_name.text = title
        }
        else {
            state_name.text = ""
            district_name.text = ""
            block_name.text = ""
            town_name.text = ""
            panchayat_name.text = ""

        }

       }

}
