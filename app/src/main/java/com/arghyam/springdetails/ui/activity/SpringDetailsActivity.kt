package com.arghyam.springdetails.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R

import kotlinx.android.synthetic.main.activity_spring_details.*
import kotlinx.android.synthetic.main.content_spring_details.*
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.arghyam.springdetails.ui.fragments.DetailsFragment
import com.arghyam.springdetails.ui.fragments.DischargeDataFragment


class SpringDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spring_details)
        init()
    }

    private fun init() {
        initToolBar()
        initViewPager()
    }

    private fun initToolBar() {
        setSupportActionBar(details_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViewPager() {
        setupViewPager(details_view_pager)
        details_tabs.setupWithViewPager(details_view_pager)
    }


    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(DetailsFragment(), "Details")
        adapter.addFragment(DischargeDataFragment(), "Discharge Data")
        viewPager.adapter = adapter
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList.get(position)
        }
    }
}
