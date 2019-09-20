package com.arghyam.springdetails.ui.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.arghyam.additionalDetails.ui.AddAdditionalDetailsActivity
import com.arghyam.addspring.repository.UploadImageRepository
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.GET_ADDITIONAL_DETAILS
import com.arghyam.commons.utils.Constants.GET_ALL_SPRINGS_ID
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.springdetails.adapter.ImageAdapter
import com.arghyam.springdetails.models.*
import com.arghyam.springdetails.repository.GetAdditionalDetailsRepository
import com.arghyam.springdetails.repository.SpringDetailsRepository
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.arghyam.springdetails.viewmodel.SpringDetailsViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.spring_details.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 *
 */
class DetailsFragment : Fragment() {

    val REQUEST_CODE = 3
    internal var selectedMonthNames: ArrayList<String> = ArrayList()
    internal lateinit var seasonality: String
    private var houseHoldNumber: Int = 0
    private lateinit var parentIntent: Intent
    private var images: ArrayList<String> = ArrayList()
    lateinit var intent: Intent
    lateinit var response: ArrayList<SpringProfileResponse>
    var springCode: String? = null
    private var springName: String? = null
    private lateinit var springProfileResponse: SpringProfileResponse

    @Inject
    lateinit var springDetailsRepository: SpringDetailsRepository

    @Inject
    lateinit var getAdditionalDetailsRepository: GetAdditionalDetailsRepository

    private var springDetailsViewModel: SpringDetailsViewModel? = null

    private var getAdditionalDetialsViewModel: GetAdditionalDetialsViewModel? = null


    @Inject
    lateinit var uploadImageRepository: UploadImageRepository

    private var waterUse: ArrayList<String> = ArrayList()

    //...
    var currentPage = 0
    var timer: Timer? = null
    val DELAY_MS: Long = 500//delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 3000 // time in milliseconds between successive task executions.

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.arghyam.R.layout.fragment_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        parentIntent = activity!!.intent
        init()
    }

    private fun init() {
        initComponent()
        initRepository()
        initClick()
        initSpringDetails()
        initGetAdditionalDetails()
        initAddDischargeData()
        initSpringDetailsResponse()
        initAdditionalDetailsResponse()
    }

    private fun initAdditionalDetailsResponse() {
        getAdditionalDetialsViewModel?.getAdditionalDetailsResponse()?.observe(this, Observer {
            saveAdditionalDetailsData(it)
        })
        getAdditionalDetialsViewModel?.getAdditionalDetailsError()?.observe(this, Observer {
            Log.e("error", it)
        })
    }

    private fun saveAdditionalDetailsData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode.equals("200")) {
            var additionalDetailsResponse: AdditionalDetailsResponse = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<AdditionalDetailsResponse>() {}.type
            )
            if (additionalDetailsResponse.springCode != null) {
                seasonality = additionalDetailsResponse.seasonality
                selectedMonthNames = ArghyamUtils().convertToNames(additionalDetailsResponse.months)
                waterUse = additionalDetailsResponse.usage
                houseHoldNumber = additionalDetailsResponse.numberOfHousehold
                showAdditionalData()
            }
        }
    }

    private fun initComponent() {
        intent = activity?.intent!!

        springCode = intent.getStringExtra("SpringCode")

        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initSpringDetailsResponse() {
        springDetailsViewModel?.getSpringDetailsResponse()?.observe(this, Observer {
            saveSpringDetailsData(it)
            initImageAdapter(it)
            if (springDetailsViewModel?.getSpringDetailsResponse()?.hasObservers()!!) {
                springDetailsViewModel?.getSpringDetailsResponse()?.removeObservers(this)
            }
        })
        springDetailsViewModel?.getSpringError()?.observe(this, Observer {
            Log.e("stefy error", it)
        })

        springDetailsViewModel?.getSpringFailure()?.observe(this, Observer {
            Log.e("stefy===", it)
        })
    }

    private fun saveSpringDetailsData(responseModel: ResponseModel) {

        var springProfileResponse: SpringProfileResponse = Gson().fromJson(
            ArghyamUtils().convertToString(responseModel.response.responseObject),
            object : TypeToken<SpringProfileResponse>() {}.type
        )
        initSetData(springProfileResponse)
    }

    private fun initSetData(springProfileResponse: SpringProfileResponse) {

        springName = "${springProfileResponse.springName}"
        (activity as SpringDetailsActivity).setActionBarTitle(springName!!)
        tv_spring_name.text = ":  ${springProfileResponse.springName}"
        val string = springProfileResponse.address
        val parts = string.split("|")
        var address = parts[parts.size - 1] + ", " + parts[0]
        tv_spring_location.text = ":  ${address.trim()}"
        tv_spring_ownership.text = ":  ${springProfileResponse.ownershipType}"
        tv_spring_id.text = ":  ${springProfileResponse.springCode}"
        tv_spring_submitted.text = ":  ${springProfileResponse.submittedBy}"
        if (!springProfileResponse.createdTimeStamp.equals(null)) {
            date.text = ArghyamUtils().epochToDateMonth(springProfileResponse.createdTimeStamp)
            time.text = ArghyamUtils().epochToTime(springProfileResponse.createdTimeStamp)
        }

        Log.e("Spring usage", springProfileResponse.usage.toString())
    }

    override fun onResume() {
        super.onResume()
        initSpringDetails()
        initGetAdditionalDetails()
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
                    springCode = springCode
                )
            )
        )
        springDetailsViewModel?.springDetailsApi(context!!, springDetailObject)
    }

    private fun initGetAdditionalDetails() {
        var springAdditionalDetailsObject = RequestModel(
            id = GET_ADDITIONAL_DETAILS,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = RequestSpringAdditionalDetailsDataModel(
                additionalInfo = AdditionalDetailsModel(
                    springCode = springCode
                )
            )
        )
        springCode?.let {
            getAdditionalDetialsViewModel?.getAdditionalDetailsApi(
                context!!,
                it, springAdditionalDetailsObject
            )
        }

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

            } else {
                val intent = Intent(context, AddAdditionalDetailsActivity::class.java)

                intent.putExtra("SpringCode", springCode)
                intent.putExtra("SpringName", springName)
                Log.e("Code in details", springCode)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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

            } else {
                val intent = Intent(context, AddDischargeActivity::class.java)
                intent.putExtra("SpringName", springName)
                intent.putExtra("SpringCode", springCode)
                Log.e("Code in details", springCode)
                startActivity(intent)
            }
        }
    }

    private fun initImageAdapter(responseModel: ResponseModel) {
        imagelist.clear()
        MAX_IMAGES = 3
        Log.e("responseCheckinitImage", responseModel.response.responseObject.toString())
        springProfileResponse = Gson().fromJson(
            ArghyamUtils().convertToString(responseModel.response.responseObject),
            object : TypeToken<SpringProfileResponse>() {}.type
        )


        val adapter = activity?.let { ImageAdapter(it, imageSample(springProfileResponse)) }
        images_view_pager.addOnPageChangeListener(imageChangeListener())
        images_view_pager.adapter = adapter
        setupAutoPager()
        if (imagelist.size == 1) {
            left_scroll.visibility = GONE
            right_scroll.visibility = GONE
        }

    }

    private fun setupAutoPager() {
        val handler = Handler()

        val update = Runnable {
            if (currentPage == Integer.MAX_VALUE) {
                currentPage = 0
            } else {
                ++currentPage
            }
            images_view_pager.setCurrentItem(currentPage, true)

        }
        left_scroll.setOnClickListener {
            if (currentPage >= 1)
                --currentPage
            images_view_pager.setCurrentItem(currentPage, true)

        }
        right_scroll.setOnClickListener {
            if (currentPage < imagelist.size - 1)
                ++currentPage
            images_view_pager.setCurrentItem(currentPage, true)
        }


        timer?.schedule(object : TimerTask() {

            override fun run() {
                handler.post(update)
            }
        }, 500, 2500)
    }

    var MAX_IMAGES = 3
    var imagelist: ArrayList<String> = ArrayList()

    private fun imageSample(springProfileResponse: SpringProfileResponse): ArrayList<String> {
        for (i in 0 until springProfileResponse.extraInformation.dischargeData.size) {
            for (j in 0 until springProfileResponse.extraInformation.dischargeData[i].images.size) {
                if (MAX_IMAGES > 0 && !springProfileResponse.extraInformation.dischargeData[i].status.equals(
                        "Rejected",
                        true
                    )
                ) {
                    Log.e("DetailsFragment", "images added$MAX_IMAGES   j$j  i$i")
                    imagelist.add(springProfileResponse.extraInformation.dischargeData[i].images[j])
                    MAX_IMAGES--
                }
            }
        }
        if (MAX_IMAGES > 0) {
            for (i in 0 until springProfileResponse.images.size) {
                imagelist.add(springProfileResponse.images[i])
                Log.e("DetailsFragment", "images added response " + i)
                MAX_IMAGES--
            }
        }
        return imagelist
    }


    private fun imageChangeListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                //Nothing Here
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //Nothing Here
            }

            override fun onPageSelected(position: Int) {
                //Nothing Here
            }

        }
    }

    private fun initRepository() {
        springDetailsViewModel = ViewModelProviders.of(this).get(SpringDetailsViewModel::class.java)
        springDetailsViewModel?.setSpringDetailsRepository(springDetailsRepository)

        getAdditionalDetialsViewModel = ViewModelProviders.of(this).get(GetAdditionalDetialsViewModel::class.java)
        getAdditionalDetialsViewModel?.setAdditionalDetailsRepository(getAdditionalDetailsRepository)
    }


}
