package com.arghyam.landing.ui.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.addspring.ui.NewSpringActivity
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.GET_ALL_SPRINGS_ID
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.adapters.LandingAdapter
import com.arghyam.landing.model.AllSpringDataModel
import com.arghyam.landing.model.AllSpringDetailsModel
import com.arghyam.landing.model.AllSpringModel
import com.arghyam.landing.model.GetAllSpringsModel
import com.arghyam.landing.repository.GetAllSpringRepository
import com.arghyam.landing.viewmodel.GetAllSpringViewModel
import com.arghyam.landing.viewmodel.LandingViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.location.LocationManager
import android.location.GpsStatus.GPS_EVENT_STOPPED
import android.location.GpsStatus.GPS_EVENT_STARTED


/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    @Inject
    lateinit var getAllSpringRepository: GetAllSpringRepository
    private var getAllSpringViewModel: GetAllSpringViewModel? = null
    private var springsList = ArrayList<AllSpringDataModel>()
    private var count: Int = 1
    private var maxItem: Int = 0
    private var itemsAvailable: Boolean = true
    private lateinit var adapter: LandingAdapter
    private lateinit var landingViewModel: LandingViewModel
    private var firstCallMade: Boolean = false

    /**
     * Initialize newInstance for passing paameters
     */
    companion object {
        fun newInstance(): HomeFragment {
            var fragmentHome = HomeFragment()
            var args = Bundle()
            fragmentHome.arguments = args
            return fragmentHome
        }

    }

    private lateinit var intentFilter: IntentFilter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getViewModel()
        setObserver()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()
        intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        activity?.registerReceiver(gpsSwitchStateReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(gpsSwitchStateReceiver)

    }

    private fun getViewModel() {
        landingViewModel = ViewModelProviders.of(activity!!).get(LandingViewModel::class.java)
    }

    private fun setObserver() {
        landingViewModel.getIsGpsEnabled().observe(this, Observer {
            if (it) {
//                if (!firstCallMade) {
//                    springsList.clear()
//                    adapter.notifyDataSetChanged()
//                    count = 1
//                    Log.e("Call", "from landing view model")
//                    initApiCall()
//                    firstCallMade = true
//                }
                if (activity?.let { ArghyamUtils().isLocationEnabled(it) }!!) {
                    if (!firstCallMade) {
                        springsList.clear()
                        adapter.notifyDataSetChanged()
                        count = 1
                        initApiCall()
                    }
                } else {
                    Log.e("call", "from observer")
//                    activity?.let { ArghyamUtils().turnOnLocation(it) }!!
                    errorItems.visibility = VISIBLE
                    errorDesc.text = activity!!.resources.getText(R.string.turn_on_location_desc)
                    springsLocation.visibility = GONE
                    firstCallMade = false
                }
            }
        })
    }

    private val gpsSwitchStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {

                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                if (isGpsEnabled) {
                    getAllSpringRequest()
                    errorItems.visibility = GONE
//                    errorDesc.text = activity!!.resources.getText(R.string.turn_on_location_desc)
                    springsLocation.visibility = VISIBLE
                } else {
                    springsList.clear()
                    adapter.notifyDataSetChanged()
                    count=1
                    activity?.let { ArghyamUtils().turnOnLocation(it) }!!
                    errorItems.visibility = VISIBLE
                    errorDesc.text = activity!!.resources.getText(R.string.turn_on_location_desc)
                    springsLocation.visibility = GONE
                    Log.e("Anirudh", "GPS Disabled")
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initComponent()
        if (ArghyamUtils().permissionGranted(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initRecyclerView()
            if (activity?.let { ArghyamUtils().isLocationEnabled(it) }!!) {
                if (springsList.size == 0) {

                    initRepository()
                    initApiCall()
                }
            } else {
                Log.e("call", "from init")
                activity?.let { ArghyamUtils().turnOnLocation(it) }!!
                errorItems.visibility = VISIBLE
                errorDesc.text = activity!!.resources.getText(R.string.turn_on_location_desc)
                springsLocation.visibility = GONE
                firstCallMade = false
            }
        }
        initFab()
        reload()
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initApiCall() {
        if (itemsAvailable) {
            if (!firstCallMade) {
                Log.e("call made ", "from initApiCall")
                errorItems?.visibility = GONE
                springsLocation?.visibility = VISIBLE
                progressBar.visibility = VISIBLE
                initRepository()
                getAllSpringRequest()
                initGetAllSpring()
                firstCallMade = true
            }
        } else {
            errorItems.visibility = VISIBLE
            errorDesc.text = activity!!.resources.getText(R.string.turn_on_location_desc)
            springsLocation.visibility = GONE
        }
    }

    private fun initGetAllSpring() {
        getAllSpringViewModel?.getAllSpringResponse()?.observe(this, Observer {
            progressBar.visibility = GONE
            saveGetAllSpringsData(it)
//            if (getAllSpringViewModel?.getAllSpringResponse()?.hasObservers()!!) {
//                getAllSpringViewModel?.getAllSpringResponse()?.removeObservers(this)
//            }
        })
        getAllSpringViewModel?.getAllSpringError()?.observe(this, Observer {
            Log.e("error", it)
            if (getAllSpringViewModel?.getAllSpringError()?.hasObservers()!!) {
                getAllSpringViewModel?.getAllSpringError()?.removeObservers(this)
            }
        })
    }

    private fun saveGetAllSpringsData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            var responseData: AllSpringDetailsModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<AllSpringDetailsModel>() {}.type
            )
            Log.e(
                "Total Springs", ArghyamUtils().convertToString(responseModel.response.responseObject)
            )

            Log.e("Total Springs", responseData.totalSprings.toString() + "springs")
            Log.e("Springs", "added")
            springsList.addAll(responseData.springs)
            for (spring in springsList)
                Log.e("SpringList", spring.springCode)
            maxItem = responseData.totalSprings / 5
            if (responseData.totalSprings % 5 != 0) {
                maxItem++
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun reload() {
        reload.setOnClickListener {
            Log.e("Anirudh", "reloaded")
            springsList.clear()
            adapter.notifyDataSetChanged()
            count = 1
//            initApiCall()
            getAllSpringRequest()
        }
    }


    private fun getAllSpringRequest() {
        var getAllSpringObject = RequestModel(
            id = GET_ALL_SPRINGS_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = GetAllSpringsModel(
                springs = AllSpringModel(
                    type = "springs"
                )
            )
        )
        getAllSpringViewModel?.getAllSpringApi(context!!, count, getAllSpringObject)
    }

    private fun initFab() {
        floatingActionButton.setOnClickListener {
            if (SharedPreferenceFactory(activity!!.applicationContext).getString(Constants.ACCESS_TOKEN) == "") {
                view?.let { it ->
                    ArghyamUtils().makeSnackbar(
                        it,
                        "Sign In to Continue",
                        "SIGN IN",
                        activity,
                        LoginActivity::class.java
                    )
                }
            } else
                activity?.startActivity(Intent(activity, NewSpringActivity::class.java))
        }
    }


    private fun initRecyclerView() {
        springsLocation.visibility = VISIBLE
        springRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = activity?.let { LandingAdapter(springsList, it) }!!
        springRecyclerView.adapter = adapter
        springRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if (maxItem > count) {
                        count++
                        Log.e("karthik", "$count")
                        progressBar.visibility = VISIBLE
                        Log.e("Call made", " from recycler View")
                        getAllSpringRequest()
//                        initApiCall()
                    }
                }
            }
        })
    }

    private fun initRepository() {
        getAllSpringViewModel = ViewModelProviders.of(this).get(GetAllSpringViewModel::class.java)
        getAllSpringViewModel?.setGetAllSpringRepository(getAllSpringRepository)
    }

}
