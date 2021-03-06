package com.arghyam.springdetails.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.Constants
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.springdetails.models.RequestSpringDetailsDataModel
import com.arghyam.springdetails.models.SpringDetailsModel
import com.arghyam.springdetails.repository.SpringDetailsRepository
import com.arghyam.springdetails.ui.fragments.AnalysisFragment
import com.arghyam.springdetails.ui.fragments.DetailsFragment
import com.arghyam.springdetails.ui.fragments.DischargeDataFragment
import com.arghyam.springdetails.viewmodel.SpringDetailsViewModel
import kotlinx.android.synthetic.main.activity_spring_details.*
import kotlinx.android.synthetic.main.content_spring_details.*
import javax.inject.Inject


class SpringDetailsActivity : AppCompatActivity() {

    private lateinit var springCode: String
    @Inject
    lateinit var springDetailsRepository: SpringDetailsRepository
    private var springName: String? = null


    private var springDetailsViewModel: SpringDetailsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spring_details)
        init()
    }

    private fun checkForUpcoming(): Boolean {
        return intent.getBooleanExtra("flag", false)
    }

    private fun getSpringId() {
        var dataIntent: Intent = intent
        springCode = dataIntent.getStringExtra("SpringCode")
        springName = dataIntent.getStringExtra("SpringName")
        Log.e("Code", springCode)
    }

    private fun init() {
        initComponent()

        getSpringId()
        initRepository()
        initSpringDetails()
        initToolBar()
        initViewPager()
        loadFragment()
    }

    private fun loadFragment() {
        if (checkForUpcoming()) {
            Log.e("SpringDetails", "coming inside")
            details_view_pager.currentItem = 1
        }
    }

    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initToolBar() {
        setSupportActionBar(custom_toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = springName.toString()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun setActionBarTitle(title: String) {
        supportActionBar!!.title = title
    }

    private fun initViewPager() {
        setupViewPager(details_view_pager)
        details_tabs.setupWithViewPager(details_view_pager)
    }


    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
//        var detailsFragment:DetailsFragment

//        detailsFragment.arguments=
        adapter.addFragment(DetailsFragment(), "Details")
        adapter.addFragment(DischargeDataFragment(), "Discharge Data")
        adapter.addFragment(AnalysisFragment(), "Analysis")
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

    private fun initRepository() {
        springDetailsViewModel = ViewModelProviders.of(this).get(SpringDetailsViewModel::class.java)
        springDetailsViewModel?.setSpringDetailsRepository(springDetailsRepository)
    }

    private fun initSpringDetails() {
        var springDetailObject = RequestModel(
            id = Constants.GET_ALL_SPRINGS_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = RequestSpringDetailsDataModel(
                springs = SpringDetailsModel(
                    springCode = springCode
                )
            )
        )
        springDetailsViewModel?.springDetailsApi(this@SpringDetailsActivity, springDetailObject)

    }

}
