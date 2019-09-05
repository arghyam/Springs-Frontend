package com.arghyam.landing.ui.fragment


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.GpsStatus.GPS_EVENT_STARTED
import android.location.GpsStatus.GPS_EVENT_STOPPED
import android.location.Location
import android.location.LocationManager
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
import com.arghyam.deduplication.model.DeduplicationModel
import com.arghyam.deduplication.model.DeduplicationRequest
import com.arghyam.deduplication.model.DeduplicationRequestModel
import com.arghyam.deduplication.model.DeduplicationViewModel
import com.arghyam.deduplication.repository.DeduplicationRepository
import com.arghyam.deduplication.ui.activity.DeduplicationActivity
import com.arghyam.favourites.model.AllFavSpringsData
import com.arghyam.favourites.model.FavSpringDataModel
import com.arghyam.favourites.model.FavSpringDetailsModel
import com.arghyam.favourites.model.GetAllFavSpringsModel
import com.arghyam.favourites.repository.GetFavSpringsRepository
import com.arghyam.favourites.viewmodel.FavouritesViewModel
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.adapters.LandingAdapter
import com.arghyam.landing.interfaces.FavouritesInterface
import com.arghyam.landing.model.*
import com.arghyam.landing.repository.GetAllSpringRepository
import com.arghyam.landing.repository.NotificationCountRepository
import com.arghyam.landing.viewmodel.GetAllSpringViewModel
import com.arghyam.landing.viewmodel.LandingViewModel
import com.arghyam.landing.viewmodel.NotificationCountViewModel
import com.arghyam.notification.model.NotificationModel
import com.arghyam.notification.model.notificationSpringModel
import com.arghyam.notification.ui.activity.NotificationActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private val TAG = "Home Fragment"
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
    private var flag: Int = 0

    @Inject
    lateinit var notificationCountRepository: NotificationCountRepository

    private var notificationCountViewModel: NotificationCountViewModel? = null

    @Inject
    lateinit var favouritesRepository: GetFavSpringsRepository
    private var favouritesViewModel: FavouritesViewModel? = null
    private var mLocation: Location? = null
    @Inject
    lateinit var deduplicationRepository: DeduplicationRepository
    private var deduplicationViewModel: DeduplicationViewModel? = null

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

    private fun initbell(notificationCount: Int) {
        Log.d("notificationCountBell--", "" + notificationCount)

        if (notificationCount > 0) {
            badge.visibility = VISIBLE
            notification_count.visibility = VISIBLE
            notification_count.text = notificationCount.toString()
        }
    }

    private fun initNotifications() {
        if (context?.let { SharedPreferenceFactory(it).getString(Constants.ACCESS_TOKEN) } == "") {
            bell.visibility = GONE
            notification_bell.visibility = GONE
        } else
            bell.visibility = VISIBLE
        notification_bell.setOnClickListener {
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

        val lm = context?.getSystemService(LOCATION_SERVICE) as LocationManager?
        lm!!.addGpsStatusListener { event ->
            when (event) {
                GPS_EVENT_STARTED -> {
                }
                GPS_EVENT_STOPPED -> {
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
        getGoogleClient()
        initNotifications()
        initRepository()
        initNotificationCountApi()
        initObservers()
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


    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationManager: LocationManager? = null


    override fun onStart() {
        super.onStart()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
    }

    private fun getGoogleClient() {
        mGoogleApiClient = activity?.applicationContext?.let {
            GoogleApiClient.Builder(it)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }

        mLocationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        getLocation()

    }


    override fun onConnected(p0: Bundle?) {
        getLocation()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.i(TAG, "Connection Suspended")
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i(TAG, "Connection failed. Error: ")
    }

    private fun getLocation() {
        Dexter.withActivity(this.activity)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(permissionListener).check()
    }


    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted(response: PermissionGrantedResponse) {
            if (activity?.let { ArghyamUtils().isLocationEnabled(it) }!!) {
                getFusedClient()
            } else {
                ArghyamUtils().turnOnLocation(activity!!)
            }
        }

        override fun onPermissionDenied(response: PermissionDeniedResponse) {
            if (response.isPermanentlyDenied) {
                activity?.applicationContext?.let {
                    ArghyamUtils().longToast(
                        it,
                        Constants.LOCATION_PERMISSION_NOT_GRANTED
                    )
                }
                activity?.parent?.let { ArghyamUtils().openSettings(it) }
            }
        }

        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
            token.continuePermissionRequest()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getFusedClient() {
        var fusedLocationProviderClient:
                FusedLocationProviderClient? =
            this.activity?.let { LocationServices.getFusedLocationProviderClient(it) }
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location ->
            // Got last known address. In some rare situations this can be null.
            if (location != null) {
                Log.e(
                    "Home Fragment location",
                    "   location  " + location.latitude + "                  " + location.longitude
                )
                mLocation = location
            } else {
                getFusedClient()
            }
        }
    }

    private fun getFavSpringsData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200" && null != responseModel.response.responseObject) {
            var responseData: FavSpringDetailsModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<FavSpringDetailsModel>() {}.type
            )
            if (null != responseData.FavouriteSpring) {
                favSpringsList.clear()
                favSpringsList.addAll(responseData.FavouriteSpring)
            } else
                Log.e("getFavSpringsData", "null")
        }
        Log.e("Home", "getFavSpringsData called" + favSpringsList.size)
        addFavToSpring()
    }

    private fun addFavToSpring() {
        Log.e("Home", "addFavToSpring called" + favSpringsList.size + "   " + springsList.size)

        for (i in 0 until favSpringsList.size) {
            for (j in 0 until springsList.size) {
                if (favSpringsList[i].springCode == springsList[j].springCode && !springsList[j].isFavSelected) {
                    springsList[j].isFavSelected = true
                    Log.e("Home", "called" + springsList[j].springCode)
                } else if (favSpringsList[i].springCode == springsList[j].springCode && springsList[j].isFavSelected) {
                    springsList[j].isFavSelected = false
                    Log.e("Home", "called" + springsList[j].springCode)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun initObservers() {
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
        favouritesViewModel?.favouritesResponse()?.observe(this, Observer {
            Log.e("success fav", it.toString())
        })
        favouritesViewModel?.favouritesError()?.observe(this, Observer {
            Log.e("error---", it)
            if (favouritesViewModel?.favouritesError()?.hasObservers()!!) {
                favouritesViewModel?.favouritesError()?.removeObservers(this)
            }
        })

        //FetchFavouritesObservers
        favouritesViewModel?.getFavSpringData?.observe(this, Observer {
            progressBar.visibility = GONE
            getFavSpringsData(it)
        })
        favouritesViewModel?.favouritesError()?.observe(this, Observer {
            Log.e("error", it)
            if (favouritesViewModel!!.favouritesError().hasObservers()) {
                favouritesViewModel!!.favouritesError().removeObservers(this)
            }
        })

        //Deduplication Observers
        deduplicationViewModel?.getDeduplicationData?.observe(this, Observer {
            Log.d("Deduplication Activity", "Success")
            saveDeduplicationData(it)
        })

        deduplicationViewModel?.getDeduplicationError?.observe(this, Observer {
            Log.d("Deduplication Activity", "Api Error")
        })
    }

    private fun saveDeduplicationData(responseModel: ResponseModel?) {
        if (flag == Constants.SPRINGS_NEAR_ME) {
            var responseData: AllSpringDetailsModel = Gson().fromJson(
                responseModel?.response?.responseObject?.let { ArghyamUtils().convertToString(it) },
                object : TypeToken<AllSpringDetailsModel>() {}.type
            )
            Log.e(TAG, responseData.springs.size.toString())
            springsList.clear()
            springsList.addAll(responseData.springs)
            adapter.notifyDataSetChanged()
            addFavToSpring()
        } else if (flag == Constants.DEDUPLICATION) {
            val responseData: DeduplicationModel = Gson().fromJson(
                responseModel?.response?.responseObject?.let { ArghyamUtils().convertToString(it) },
                object : TypeToken<DeduplicationModel>() {}.type
            )
            if (responseData.springs.isNotEmpty()) {
                val intent = Intent(activity, DeduplicationActivity::class.java)
                intent.putExtra("location", mLocation)
                startActivity(intent)
            } else {
                val intent = Intent(activity, NewSpringActivity::class.java)
                startActivity(intent)
            }
        }
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
            addFavToSpring()
        }
    }

    private fun reload() {
        springsLocation.setOnClickListener {
            Log.e(TAG, "clicked")
            flag = Constants.SPRINGS_NEAR_ME
            if (activity?.let { ArghyamUtils().isLocationEnabled(it) }!!) {
                Log.e(TAG, "LocationEnabled")
                springsList.clear()
                deduplicationRequestCall()
                adapter.notifyDataSetChanged()
                count = 1
                initApiCall()
            } else {
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
            } else {
                flag = Constants.DEDUPLICATION
                deduplicationRequestCall()
            }
        }
    }

    private fun deduplicationRequestCall() {
        Log.e(TAG, "Coming")
        getGoogleClient()
        var accuracy: Double? = 0.0
        if (flag == Constants.DEDUPLICATION)
            accuracy = mLocation?.accuracy?.toDouble()
        Log.e(TAG, "" + accuracy + "accuracy")
        val mRequestData = mLocation?.latitude?.let { it1 ->
            mLocation?.longitude?.let { it2 ->
                accuracy?.let {
                    DeduplicationRequest(
                        latitude = it1,
                        longitude = it2,
                        accuracy = it
                    )
                }
            }
        }?.let { it2 ->
            DeduplicationRequestModel(
                location = it2
            )
        }?.let { it3 ->
            RequestModel(
                id = Constants.CREATE_STATE,
                ver = BuildConfig.VER,
                ets = BuildConfig.ETS,
                params = Params(
                    did = "",
                    key = "",
                    msgid = ""
                ),
                request = it3
            )
        }
        if (mRequestData != null) {
            deduplicationApiCall(mRequestData)
        }
    }

    private fun deduplicationApiCall(mRequestData: RequestModel) {
        activity?.applicationContext?.let { deduplicationViewModel?.deduplicationSpringsApi(it, mRequestData) }
    }

    private fun initRecyclerView() {
        springsLocation.visibility = VISIBLE
        springRecyclerView.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager?
        adapter = activity?.let { LandingAdapter(springsList, it, favouritesInterface, favSpringsList) }!!
        adapter.notifyDataSetChanged()
        springRecyclerView.adapter = adapter
        springRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && maxItem > count && flag == 0) {
                    count++
                    progressBar.visibility = VISIBLE
                    getAllSpringRequest()
                    addFavToSpring()
                }
            }
        })
        springRecyclerView.itemAnimator?.changeDuration = 0
    }

    private fun initRepository() {
        //GetAllSprings
        getAllSpringViewModel = ViewModelProviders.of(this).get(GetAllSpringViewModel::class.java)
        getAllSpringViewModel?.setGetAllSpringRepository(getAllSpringRepository)

        //Notifications
        notificationCountViewModel = ViewModelProviders.of(this).get(NotificationCountViewModel::class.java)
        notificationCountViewModel?.setNotificationCountRepository(notificationCountRepository)

        //Favourites
        favouritesViewModel = ViewModelProviders.of(this).get(FavouritesViewModel::class.java)
        favouritesViewModel?.setFavouritesRepository(favouritesRepository)

        //Deduplication
        deduplicationViewModel = ViewModelProviders.of(this).get(DeduplicationViewModel::class.java)
        deduplicationViewModel?.setDeduplicationRepository(deduplicationRepository)

    }

    private var favouritesInterface: FavouritesInterface = object : FavouritesInterface {
        override fun onFavouritesItemClickListener(
            springCode: String,
            userId: String,
            position: Int
        ) {
            sendRequest(springCode, userId)
            progressBar.visibility = VISIBLE
            var handler = Handler()
            handler.postDelayed({ getFavSpringRequest() },100)
//            getFavSpringRequest()
        }
    }
}


