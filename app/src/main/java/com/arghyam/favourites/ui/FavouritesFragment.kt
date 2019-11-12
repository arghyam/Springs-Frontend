package com.arghyam.favourites.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.SEARCH_SPRINGS
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.favourites.adapter.FavouritesAdapter
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
import com.arghyam.landing.interfaces.HomeFragmentInterface
import com.arghyam.landing.model.AllFavouritesRequest
import com.arghyam.landing.model.FavouritesModel
import com.arghyam.landing.model.NotificationCountResponseModel
import com.arghyam.landing.repository.NotificationCountRepository
import com.arghyam.landing.viewmodel.NotificationCountViewModel
import com.arghyam.notification.model.NotificationModel
import com.arghyam.notification.model.notificationCountModel
import com.arghyam.notification.model.notificationSpringModel
import com.arghyam.notification.ui.activity.NotificationActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_favourites.*
import javax.inject.Inject


class FavouritesFragment : Fragment() {


    @Inject
    lateinit var getUserFavSpringRepository: GetFavSpringsRepository
    private lateinit var favouritesViewModel: FavouritesViewModel
    private lateinit var adapter: FavouritesAdapter
    private var springsList = ArrayList<FavSpringDataModel>()

    @Inject
    lateinit var notificationCountRepository: NotificationCountRepository
    private var notificationCountViewModel: NotificationCountViewModel? = null

    companion object {
        fun newInstance(): FavouritesFragment {
            var fragmentFavourites = FavouritesFragment()
            var args = Bundle()
            fragmentFavourites.arguments = args
            return fragmentFavourites
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    private fun getViewModel() {
        favouritesViewModel = ViewModelProviders.of(activity!!).get(FavouritesViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun initbell(notificationCount: Int) {
        if (notificationCount > 0) {
            badge.visibility = VISIBLE
            notification_count.visibility = VISIBLE
            notification_count.text = notificationCount.toString()
        }
    }

    private fun init() {
        initComponent()
        initNotifications()
        initRepository()
        initNotificationCountApi()
        initNotificationCountResponse()
        if (ArghyamUtils().permissionGranted(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initRecyclerView()
            if (activity?.let { ArghyamUtils().isLocationEnabled(it) }!!) {
                initApiCall()
            } else {
                activity?.let { ArghyamUtils().turnOnLocation(it) }!!
            }
        }
    }

    private fun initNotificationCountApi() {

        var userId = SharedPreferenceFactory(activity!!.applicationContext).getString(Constants.USER_ID)!!

        var notificationCountObject = RequestModel(
            id = SEARCH_SPRINGS,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = notificationCountModel(
                userId = userId
            )
        )

        notificationCountViewModel?.notificationCountApi(context!!, userId, notificationCountObject)
    }

    private fun initNotificationCountResponse() {
        notificationCountViewModel?.getNotificationCountResponse()?.observe(this, Observer {
            notificationCountViewModel?.getNotificationCountResponse()?.removeObservers(this)
            saveNotificationCountData(it)
        })
        notificationCountViewModel?.notificationCountError()?.observe(this, Observer {
            Log.e("error---", it)
            if (notificationCountViewModel?.notificationCountError()?.hasObservers()!!) {
                notificationCountViewModel?.notificationCountError()?.removeObservers(this)
            }
        })
    }

    private fun saveNotificationCountData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            Log.d("success_notication", "yes")

            var notificationCountResponseModel: NotificationCountResponseModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<NotificationCountResponseModel>() {}.type
            )

            Log.d("notificationCount--fav", notificationCountResponseModel.notificationCount.toString())
            var notificationCountBell = notificationCountResponseModel.notificationCount

            initbell(notificationCountBell)

        }
    }

    private fun initNotifications() {
        if (context?.let { SharedPreferenceFactory(it).getString(Constants.ACCESS_TOKEN) } == "") {
            notauser.visibility = VISIBLE
            scrollView.visibility = GONE
            bell.visibility = GONE
            notification_bell.visibility = GONE
            initsigninbutton()
        } else {
            notauser.visibility = GONE
            bell.visibility = VISIBLE
        }
        bell.setOnClickListener {
            Log.e("Anirudh", "bell clicked")
            this.startActivity(Intent(activity!!, NotificationActivity::class.java))
        }
    }

    private fun initsigninbutton() {
        sign_in_button_fav.setOnClickListener {
            startActivity(Intent(activity!!, LoginActivity::class.java))
            activity!!.finish()
        }
    }

    private fun initGetAllFavSpring() {
        favouritesViewModel.getFavSpringData.observe(this, Observer {
            progressBar.visibility = GONE
            getFavSpringsData(it)
        })
        favouritesViewModel.favouritesError().observe(this, Observer {
            Log.e("error", it)
            if (favouritesViewModel.favouritesError().hasObservers()) {
                favouritesViewModel.favouritesError().removeObservers(this)
            }
        })
    }

    private fun getFavSpringsData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            var responseData: FavSpringDetailsModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<FavSpringDetailsModel>() {}.type
            )
            if (null != responseData.FavouriteSpring && context?.let { SharedPreferenceFactory(it).getString(Constants.ACCESS_TOKEN) } != "") {
                springsList.clear()
                springsList.addAll(responseData.FavouriteSpring)
                if (springsList.isEmpty()) {
                    notauser.visibility = VISIBLE
                    sign_in_text.visibility = GONE
                    sign_in_button_fav.visibility = GONE
                }
                progressBar.visibility = GONE
                adapter.notifyDataSetChanged()
            }
            else if (context?.let { SharedPreferenceFactory(it).getString(Constants.ACCESS_TOKEN) } == ""){
                sign_in_text.visibility = VISIBLE
                sign_in_button_fav.visibility = VISIBLE
            }
        }
    }

    private fun initApiCall() {
        progressBar.visibility = VISIBLE
        initGetAllFavSpring()
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
        val toolbar = toolbar as Toolbar
        toolbar.title = "Favourites"
    }

    private fun getFavSpringRequest() {
        var getFavSpringObject = context?.let {
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
        getFavSpringObject?.let { favouritesViewModel.getFavouriteSpringsApi(context!!, it) }
    }


    private fun initRecyclerView() {
        favrecyclerview.layoutManager = LinearLayoutManager(activity)
        adapter = activity?.let { FavouritesAdapter(springsList, it, favFragmentInterface) }!!
        favrecyclerview.adapter = adapter
        getFavSpringRequest()

    }

    private fun initRepository() {
        favouritesViewModel = ViewModelProviders.of(this).get(FavouritesViewModel::class.java)
        favouritesViewModel.setFavouritesRepository(getUserFavSpringRepository)


        notificationCountViewModel = ViewModelProviders.of(this).get(NotificationCountViewModel::class.java)
        notificationCountViewModel?.setNotificationCountRepository(notificationCountRepository)

    }

    private var favFragmentInterface: HomeFragmentInterface = object : HomeFragmentInterface {
        override fun onRequestAccess(springCode: String, userId: String) {

        }

        override fun onFavouritesItemClickListener(
            springCode: String,
            userId: String,
            position: Int
        ) {
            sendRequest(springCode, userId)
            progressBar.visibility = VISIBLE
            val handler = Handler()
            handler.postDelayed({ getFavSpringRequest() }, 200)
        }
    }

    private fun sendRequest(springCode: String, userId: String) {
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
        mRequestData?.let { favouritesViewModel?.storeFavouriteSpringsApi(context!!, it) }
    }
}
