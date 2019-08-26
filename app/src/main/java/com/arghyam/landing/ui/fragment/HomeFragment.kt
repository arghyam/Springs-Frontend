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
import com.arghyam.landing.repository.GetAllSpringRepository
import com.arghyam.landing.viewmodel.GetAllSpringViewModel
import com.arghyam.landing.viewmodel.LandingViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject
import android.content.Context
import android.location.LocationManager
import android.location.GpsStatus.GPS_EVENT_STOPPED
import android.location.GpsStatus.GPS_EVENT_STARTED
import com.arghyam.favourites.model.AllFavSpringsData
import com.arghyam.favourites.model.FavSpringDataModel
import com.arghyam.favourites.model.FavSpringDetailsModel
import com.arghyam.favourites.model.GetAllFavSpringsModel
import com.arghyam.favourites.repository.GetFavSpringsRepository
import com.arghyam.favourites.viewmodel.FavouritesViewModel
import com.arghyam.landing.interfaces.FavouritesInterface
import com.arghyam.landing.model.*
import com.arghyam.landing.repository.NotificationCountRepository
import com.arghyam.landing.viewmodel.NotificationCountViewModel
import com.arghyam.notification.model.NotificationModel
import com.arghyam.notification.model.notificationSpringModel
import com.arghyam.notification.ui.activity.NotificationActivity
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_home.progressBar




/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    @Inject
    lateinit var getAllSpringRepository: GetAllSpringRepository
    private var getAllSpringViewModel: GetAllSpringViewModel? = null
    private var springsList = ArrayList<AllSpringDataModel>()
    private var favSpringsList = ArrayList<FavSpringDataModel>()
    private var count: Int = 1
    private var maxItem: Int = 0
    private var itemsAvailable: Boolean = true
    private lateinit var adapter: LandingAdapter
    private lateinit var landingViewModel: LandingViewModel
    private var firstCallMade: Boolean = false

    @Inject
    lateinit var notificationCountRepository: NotificationCountRepository

    private var notificationCountViewModel: NotificationCountViewModel? = null

    @Inject
    lateinit var favouritesRepository: GetFavSpringsRepository
    private var favouritesViewModel: FavouritesViewModel? = null

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

    private fun initbell(notificationCount:Int) {
        Log.d("notificationCountBell--","" + notificationCount)

        if(notificationCount>0){
            badge.visibility = VISIBLE
            notification_count.visibility = VISIBLE
            notification_count.text = notificationCount.toString()
        }
    }
    private fun initNotifications() {
        if (context?.let { SharedPreferenceFactory(it).getString(Constants.ACCESS_TOKEN) } == ""){
            bell.visibility = GONE
            notification_bell.visibility = GONE
        }
        else
            bell.visibility = VISIBLE
        notification_bell.setOnClickListener {
            Log.e("Fragment","bell clicked")
            activity?.startActivity(Intent(activity, NotificationActivity::class.java))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getViewModel()
        setObserver()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("MissingPermission")
    private fun registerReceiver() {

        val lm = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        lm!!.addGpsStatusListener { event ->
            when (event) {
                GPS_EVENT_STARTED -> {
//                    Log.e("Anirudh","gps Enabled")
                }
                GPS_EVENT_STOPPED -> {
//                    Log.e("Anirudh","gps Disabled")
                }
            }// do your tasks
            // do your tasks
        }
    }

    private fun getViewModel() {
        landingViewModel = ViewModelProviders.of(activity!!).get(LandingViewModel::class.java)
    }

    private fun setObserver() {
        landingViewModel.getIsGpsEnabled().observe(this, Observer {
            if (it) {
                if (activity?.let { ArghyamUtils().isLocationEnabled(it) }!!) {
                    if (!firstCallMade) {
                        springsList.clear()
                        adapter.notifyDataSetChanged()
                        count = 1
                        initApiCall()
                    }
                } else {
                    Log.e("call", "from observer")
                    errorItems.visibility = VISIBLE
                    errorDesc.text = activity!!.resources.getText(R.string.turn_on_location_desc)
                    springsLocation.visibility = GONE
                    firstCallMade = false
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initComponent()
        initNotifications()
        initRepository()
        initNotificationCountApi()
        initNotificationCountResponse()
        getFavSpringRequest()
        if (ArghyamUtils().permissionGranted(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initRecyclerView()
            if (activity?.let { ArghyamUtils().isLocationEnabled(it) }!!) {
                if (springsList.size == 0) {
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
        registerReceiver()

    }

    private fun getFavSpringsData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200" && null!=responseModel.response.responseObject) {
            var responseData: FavSpringDetailsModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<FavSpringDetailsModel>() {}.type
            )
            if (null!=responseData.FavouriteSpring)
                favSpringsList.addAll(responseData.FavouriteSpring)
            for (spring in favSpringsList) {
                Log.e("FavSpringList", spring.springCode)

            }
        }
    }

    private fun initNotificationCountResponse() {
        //NotificationCountObservers
        notificationCountViewModel?.getNotificationCountResponse()?.observe(this, Observer {
            saveNotificationCountData(it)
        })
        notificationCountViewModel?.notificationCountError()?.observe(this, Observer {
            Log.e("error---", it)
            if (notificationCountViewModel?.notificationCountError()?.hasObservers()!!) {
                notificationCountViewModel?.notificationCountError()?.removeObservers(this)
            }
        })

        //FavouritesObservers
        favouritesViewModel?.favouritesResponse()?.observe(this,Observer {
            Log.e("success", it.toString())
        })
        favouritesViewModel?.favouritesError()?.observe(this, Observer {
            Log.e("error---", it)
            if (favouritesViewModel?.favouritesError()?.hasObservers()!!) {
                favouritesViewModel?.favouritesError()?.removeObservers(this)
            }
        })

        //FetchFavouritesObservers
        favouritesViewModel?.getFavSpringData?.observe(this, Observer {
            getFavSpringsData(it)
        })
        favouritesViewModel?.favouritesError()?.observe(this, Observer {
            Log.e("error", it)
            if (favouritesViewModel!!.favouritesError().hasObservers()) {
                favouritesViewModel!!.favouritesError().removeObservers(this)
            }
        })
    }

    private fun getFavSpringRequest() {
        var getFavSpringObject = context?.let { it ->
            SharedPreferenceFactory(it).getString(Constants.USER_ID)?.let {
                AllFavSpringsData(
                    userId = it
                )
            }?.let {
                GetAllFavSpringsModel(
                    favourites = it
                )
            }?.let {
                RequestModel(
                    id = Constants.GET_ADDITIONAL_DETAILS,
                    ver = BuildConfig.VER,
                    ets = BuildConfig.ETS,
                    params = Params(
                        did = "",
                        key = "",
                        msgid = ""
                    ),
                    request = it
                )
            }
        }
        getFavSpringObject?.let { favouritesViewModel?.getfavouriteSpringsApi(context!!, it) }
    }

    fun sendRequest(springCode: String, userId: String?) {
        val mRequestData = userId?.let {
            AllFavouritesRequest(
                springCode = springCode,
                userId = it
            )
        }?.let {
            FavouritesModel(
                favourites = it
            )
        }?.let {
            RequestModel(
                id = Constants.GET_ADDITIONAL_DETAILS,
                ver = BuildConfig.VER,
                ets = BuildConfig.ETS,
                params = Params(
                    did = "",
                    key = "",
                    msgid = ""
                ),
                request = it
            )
        }
        mRequestData?.let { favouritesViewModel?.storefavouriteSpringsApi(context!!, it) }

    }

    private fun saveNotificationCountData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            Log.d("success_notication", "yes")

            var notificationCountResponseModel: NotificationCountResponseModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<NotificationCountResponseModel>() {}.type
            )

            Log.d("notificationCount--Home", notificationCountResponseModel.notificationCount.toString())

            var notificationCountBell = notificationCountResponseModel.notificationCount

            initbell(notificationCountBell)


        }
    }

    private fun initNotificationCountApi() {

        var userId = SharedPreferenceFactory(activity!!.applicationContext).getString(Constants.USER_ID)!!

        var notificationCountObject = RequestModel(
            id = GET_ALL_SPRINGS_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = notificationSpringModel(
                notifications = NotificationModel(
                    type = "notifications"
                )
            )
        )

        notificationCountViewModel?.notificationCountApi(context!!, userId, notificationCountObject)
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
            springsList.addAll(responseData.springs)
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
            if (activity?.let { ArghyamUtils().isLocationEnabled(it) }!!) {
                if (!firstCallMade) {
                    springsList.clear()
                    adapter.notifyDataSetChanged()
                    count = 1
                    initApiCall()
                }
            } else {
                Log.e("call", "from observer")
                activity?.let { ArghyamUtils().turnOnLocation(it) }!!
                errorItems.visibility = VISIBLE
                errorDesc.text = activity!!.resources.getText(R.string.turn_on_location_desc)
                springsLocation.visibility = GONE
                firstCallMade = false
            }
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
        adapter = activity?.let { LandingAdapter(springsList, it, favouritesInterface, favSpringsList) }!!
        springRecyclerView.adapter = adapter
        springRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && maxItem > count) {
                        count++
                        Log.e("karthik", "$count")
                        progressBar.visibility = VISIBLE
                        Log.e("Call made", " from recycler View")
                        getAllSpringRequest()
                }
            }
        })
    }

    private fun initRepository() {
        Log.e("HomeFragment","Called")
        //GetAllSprings
        getAllSpringViewModel = ViewModelProviders.of(this).get(GetAllSpringViewModel::class.java)
        getAllSpringViewModel?.setGetAllSpringRepository(getAllSpringRepository)

        //Notifications
        notificationCountViewModel = ViewModelProviders.of(this).get(NotificationCountViewModel::class.java)
        notificationCountViewModel?.setNotificationCountRepository(notificationCountRepository)

        //Favourites
        favouritesViewModel = ViewModelProviders.of(this).get(FavouritesViewModel::class.java)
        favouritesViewModel?.setFavouritesRepository(favouritesRepository)

    }

    private var favouritesInterface: FavouritesInterface = object : FavouritesInterface {
        override fun onFavouritesItemClickListener(springCode: String, userId: String) {
            Log.e("HomeFragment","Fav Called")
            sendRequest(springCode,userId)
        }
    }

}
