package com.arghyam.springdetails.ui.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.arghyam.R
import com.arghyam.additionalDetails.ui.AddAdditionalDetailsActivity
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.springdetails.adapter.ImageAdapter
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.spring_details.*
import android.content.Context.MODE_PRIVATE
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.iam.ui.LoginActivity
import com.google.android.material.snackbar.Snackbar


/**
 * A simple [Fragment] subclass.
 *
 */
class DetailsFragment : Fragment() {

    val REQUEST_CODE = 3
    internal var selectedMonthNames: ArrayList<String> = ArrayList()
    internal lateinit var seasonality: String
    private var houseHoldNumber: Int = 0

    private var waterUse: ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initClick()
        initImageAdapter()
        initAddDischargeData()
    }

    private fun initClick() {
        additional_details_layout.setOnClickListener {
            if (SharedPreferenceFactory(activity as SpringDetailsActivity).getString(Constants.ACCESS_TOKEN) == "") {
                view?.let { it1 ->
                    ArghyamUtils().makeSnackbar(
                        it1,
                        "SignIn to continue",
                        "SIGN In",
                        activity,
                        LoginActivity::class.java
                    )
                }

            } else{
                val intent = Intent(activity, AddAdditionalDetailsActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                var bundle = data?.getBundleExtra("DataBundle")
                if (bundle?.get("SelectedMonths") != null)
                    selectedMonthNames = bundle?.get("SelectedMonths") as ArrayList<String>
                waterUse = bundle?.get("WaterUse") as ArrayList<String>
                seasonality = bundle.get("Seasonality") as String
                houseHoldNumber = bundle.get("HouseHoldNumbers") as Int

                Log.e("Water use", waterUse.toString())
                showAdditionalData()

            }
        }
    }

    private fun showAdditionalData() {
        additional_details_layout.visibility = GONE
        details_view.visibility = GONE
        additional_data.visibility = VISIBLE
        if (seasonality.equals("Perennial")) {
            tv_seasonality.text = seasonality
        } else {
            tv_seasonality.text = "$seasonality $selectedMonthNames"
        }
        var usages: StringBuffer = StringBuffer()
        for (usage in waterUse) {
            usages.append(usage)
            if (waterUse.indexOf(usage) != waterUse.size - 1)
                usages.append(", ")
        }
        tv_water_use.text = usages.toString()
        tv_household_dependency.text = houseHoldNumber.toString()

    }

    private fun initAddDischargeData() {
        add_discharge_data.setOnClickListener {
            if (SharedPreferenceFactory(activity as SpringDetailsActivity).getString(Constants.ACCESS_TOKEN) == "") {
                view?.let { it1 ->
                    ArghyamUtils().makeSnackbar(
                        it1,
                        "SignIn to continue",
                        "SIGN In",
                        activity,
                        LoginActivity::class.java
                    )
                                    }

            } else
                activity?.startActivity(Intent(activity, AddDischargeActivity::class.java))
        }
    }


    private fun initImageAdapter() {
        val adapter = activity?.let { ImageAdapter(it, imageSample()) }
        images_view_pager.addOnPageChangeListener(imageChangeListener())
        images_view_pager.adapter = adapter
    }


    private fun imageSample(): ArrayList<String> {
        var images: ArrayList<String> = ArrayList()
        images.add("https://picsum.photos/200/300")
        images.add("https://picsum.photos/300/300")
        images.add("https://picsum.photos/400/300")
        return images
    }

    private fun imageChangeListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

        }
    }

}
