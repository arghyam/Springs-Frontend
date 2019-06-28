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
import com.arghyam.addspring.viewmodel.UploadImageViewModel
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.GET_ALL_SPRINGS_ID
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.springdetails.adapter.ImageAdapter
import com.arghyam.springdetails.models.RequestSpringDetailsDataModel
import com.arghyam.springdetails.models.SpringDetailsModel
import com.arghyam.springdetails.models.SpringProfileResponse
import com.arghyam.springdetails.repository.SpringDetailsRepository
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.arghyam.springdetails.viewmodel.SpringDetailsViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_display_discharge_data.*
import kotlinx.android.synthetic.main.activity_spring_details.*
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

    private var springDetailsViewModel: SpringDetailsViewModel? = null


    @Inject
    lateinit var uploadImageRepository: UploadImageRepository

    private var waterUse: ArrayList<String> = ArrayList()

    private lateinit var uploadImageViewModel: UploadImageViewModel

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
        initAddDischargeData()
        initSpringDetailsResponse()
    }


    private fun initComponent() {
        intent = activity?.intent!!

        springCode = intent.getStringExtra("SpringCode")

        Log.d("Anirudh", "" + springCode)

        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initSpringDetailsResponse() {
        springDetailsViewModel?.getSpringDetailsResponse()?.observe(this, Observer {
            initImageAdapter(it)
            saveSpringDetailsData(it)
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
        Log.e("Anirudh", springProfileResponse.toString())
        initSetData(springProfileResponse)
        imageSample(springProfileResponse)
    }

    private fun initSetData(springProfileResponse: SpringProfileResponse) {

        springName = "${springProfileResponse.springName}"

        tv_spring_name.text = ":  ${springProfileResponse.springName}"
        tv_spring_ownership.text = ":  ${springProfileResponse.ownershipType}"
        tv_spring_id.text = ":  ${springProfileResponse.springCode}"
        if (!springProfileResponse.createdTimeStamp.equals(null)) {
            date.text = "${ArghyamUtils().getDate(springProfileResponse.createdTimeStamp)}"
            time.text = "${ArghyamUtils().getTime(springProfileResponse.createdTimeStamp)}"
        }


//        tv_spring_submtted.text = ":  ${springProfileResponse.uploadedBy}"
//        tv_spring_location.text = ":  ${springProfileResponse.latitude}" + " ${springProfileResponse.longitude}"
    }

    override fun onResume() {
        super.onResume()
        initSpringDetails()
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
                intent.putExtra("springName", springName)

                Log.d("spring_name", ""+ springName)

                Log.e("Code in details", springCode)
                startActivityForResult(intent, REQUEST_CODE)


//                val intent = Intent(activity, AddAdditionalDetailsActivity::class.java)
//                startActivityForResult(intent, REQUEST_CODE)
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

            } else {
                val intent = Intent(context, AddDischargeActivity::class.java)
                intent.putExtra("SpringCode", springCode)
                Log.e("Code in details", springCode)
                startActivity(intent)
            }
        }
    }

    private fun initImageAdapter(responseModel: ResponseModel) {
        Log.d("responseCheck", responseModel.response.responseObject.toString())
        var springProfileResponse: SpringProfileResponse = Gson().fromJson(
            ArghyamUtils().convertToString(responseModel.response.responseObject),
            object : TypeToken<SpringProfileResponse>() {}.type
        )

        val adapter = activity?.let { ImageAdapter(it, imageSample(springProfileResponse)) }
        images_view_pager.addOnPageChangeListener(imageChangeListener())
        images_view_pager.adapter = adapter
        setupAutoPager()
    }

    private fun setupAutoPager() {
        val handler = Handler()

        val update = Runnable {
            images_view_pager.setCurrentItem(currentPage, true)
            if (currentPage == Integer.MAX_VALUE) {
                currentPage = 0
            } else {
                ++currentPage
            }
        }


        timer?.schedule(object : TimerTask() {

            override fun run() {
                handler.post(update)
            }
        }, 500, 2500)
    }

    private fun imageSample(springProfileResponse: SpringProfileResponse): ArrayList<String> {
        return springProfileResponse.images
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
