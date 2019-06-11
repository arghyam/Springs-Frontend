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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.additionalDetails.ui.AddAdditionalDetailsActivity
import com.arghyam.addspring.repository.UploadImageRepository
import com.arghyam.addspring.viewmodel.UploadImageViewModel
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants.GET_ALL_SPRINGS_ID
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseDataModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.springdetails.adapter.ImageAdapter
import com.arghyam.springdetails.models.*
import com.arghyam.springdetails.repository.SpringDetailsRepository
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.viewmodel.SpringDetailsViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.spring_details.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class DetailsFragment : Fragment() {

    val REQUEST_CODE = 3
    internal var selectedMonthNames: ArrayList<String> = ArrayList()
    internal lateinit var seasonality: String
    private var houseHoldNumber: Int = 0

    lateinit var response: ArrayList<Any>

    private lateinit var springProfileResponse: SpringProfileResponse

    @Inject
    lateinit var springDetailsRepository: SpringDetailsRepository

    private var springDetailsViewModel: SpringDetailsViewModel? = null


    @Inject
    lateinit var uploadImageRepository: UploadImageRepository

    private var waterUse: ArrayList<String> = ArrayList()

    private lateinit var uploadImageViewModel: UploadImageViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initComponent()

        initClick()
        initImageAdapter()
        initAddDischargeData()
        initRepository()
        initSpringDetailsResponse()
        initSpringDetails()
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initSpringDetailsResponse() {
        springDetailsViewModel?.getSpringDetailsResponse()?.observe(this, Observer {

            Log.e("stefy", it.response.responseCode)

            saveSpringDetailsData(it)
            if (springDetailsViewModel?.getSpringDetailsResponse()?.hasObservers()!!) {
                springDetailsViewModel?.getSpringDetailsResponse()?.removeObservers(this)
            }

//            imagesList.add(it.response.imageUrl)
        })
        springDetailsViewModel?.getSpringError()?.observe(this, Observer {
            Log.e("stefy error", it)
        })
    }

    private fun saveSpringDetailsData(responseModel: ResponseModel) {
        response = responseModel.response.responseObject as ArrayList<Any>
        springProfileResponse = Gson().fromJson(
            ArghyamUtils().convertToString(response[0]),
            object : TypeToken<SpringProfileResponse>() {}.type
        )
        initSetData()
        Log.e("stefy5", springProfileResponse.ownership)
    }

    private fun initSetData() {
        tv_spring_name.text=":  ${springProfileResponse.springName}"
        tv_spring_ownership.text=":  ${springProfileResponse.ownership}"
        tv_spring_id.text=":  ${springProfileResponse.springCode}"
        tv_spring_submtted.text=":  ${springProfileResponse.uploadedBy}"
    }

    private fun initSpringDetails() {
        var springDetailObject = RequestModel(
            id = GET_ALL_SPRINGS_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = RequestSpringDetailsDataModel(
                springs = SpringDetailsModel(
                    springCode = "8H7RjK"


                )
            )
        )
        springDetailsViewModel?.springDetailsApi(context!!, springDetailObject)

    }

    private fun initClick() {
        additional_details_layout.setOnClickListener {
            val intent = Intent(activity, AddAdditionalDetailsActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
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

    private fun initRepository() {
        springDetailsViewModel = ViewModelProviders.of(this).get(SpringDetailsViewModel::class.java)
        springDetailsViewModel?.setSpringDetailsRepository(springDetailsRepository)

        uploadImageViewModel = ViewModelProviders.of(this).get(UploadImageViewModel::class.java)
        uploadImageViewModel.setUploadImageRepository(uploadImageRepository)
    }


}
