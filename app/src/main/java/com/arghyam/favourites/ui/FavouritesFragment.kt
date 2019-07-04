package com.arghyam.favourites.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.favourites.adapter.FavouritesAdapter
import com.arghyam.favourites.model.FavSpringDataModel
import com.arghyam.favourites.model.FavSpringDetailsModel
import com.arghyam.favourites.viewmodel.FavouritesViewModel
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.model.AllSpringModel
import com.arghyam.landing.model.GetAllSpringsModel
import com.arghyam.landing.repository.GetAllSpringRepository
import com.arghyam.landing.viewmodel.GetAllSpringViewModel
import com.arghyam.notification.ui.activity.NotificationActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_favourites.toolbar
import kotlinx.android.synthetic.main.custom_toolbar.*
import javax.inject.Inject


class FavouritesFragment : Fragment() {


    @Inject
    lateinit var getAllSpringRepository: GetAllSpringRepository
    private var getAllSpringViewModel: GetAllSpringViewModel? = null
    private lateinit var favouritesViewModel: FavouritesViewModel
    private lateinit var adapter: FavouritesAdapter
    private var springsList = ArrayList<FavSpringDataModel>()
    private var count: Int = 1
    private var maxItem: Int = 0

    private var notification: Boolean = true

    companion object {
        fun newInstance(): FavouritesFragment {
            var fragmentFavourites = FavouritesFragment()
            var args = Bundle()
            fragmentFavourites.arguments = args
            return fragmentFavourites
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun initbell(notificationCount:Int) {
//        if(notificationCount>0){
//            badge.visibility = VISIBLE
//            notification_count.visibility = VISIBLE
//
//            notification_count.text = notificationCount.toString()
//        }
//        bell.setOnClickListener{
//            Log.e("Anirudh", "bell clicked")
//            this.startActivity(Intent(activity!!, NotificationActivity::class.java))
//        }
    }

    private fun init() {
        initComponent()
        initNotifications()
        if (ArghyamUtils().permissionGranted(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initRecyclerView()
            if (activity?.let { ArghyamUtils().isLocationEnabled(it) }!!) {
                initRepository()
                initApiCall()
            } else {
                activity?.let { ArghyamUtils().turnOnLocation(it) }!!
            }
        }
    }

    private fun initNotifications() {
        if (context?.let { SharedPreferenceFactory(it).getString(Constants.ACCESS_TOKEN) } == ""){
            notauser.visibility = VISIBLE
            scrollView.visibility = GONE
//            bell.visibility = GONE
            initsigninbutton()
        }
        else{
            notauser.visibility = GONE
//            bell.visibility = VISIBLE
        }
    }

    private fun initsigninbutton() {
        sign_in_button_fav.setOnClickListener {
            startActivity(Intent(activity!!, LoginActivity::class.java))
            activity!!.finish()
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
            var responseData: FavSpringDetailsModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<FavSpringDetailsModel>() {}.type
            )
            Log.e(
                "Total Springs", ArghyamUtils().convertToString(responseModel.response.responseObject)
            )

            Log.e("Total Springs", responseData.totalFavSprings.toString() + "springs")
            springsList.addAll(responseData.springs)
            for (spring in springsList)
                Log.e("SpringList", spring.springCode)
            maxItem = responseData.totalFavSprings / 5
            if (responseData.totalFavSprings % 5 != 0) {
                maxItem++
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun initApiCall() {
        progressBar.visibility = VISIBLE
        getAllSpringRequest()
        initGetAllSpring()
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
        val toolbar = toolbar as Toolbar
        toolbar.title = "Favourites"
        if (SharedPreferenceFactory(activity!!.applicationContext).getInt(Constants.NOTIFICATION_COUNT)!! > 0){
            SharedPreferenceFactory(activity!!.applicationContext).getInt(Constants.NOTIFICATION_COUNT)?.let { initbell(it) }
        }
    }

    private fun getAllSpringRequest() {
        var getAllSpringObject = RequestModel(
            id = Constants.GET_ALL_SPRINGS_ID,
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


    private fun initRecyclerView() {
        favrecyclerview.layoutManager = LinearLayoutManager(activity)
        adapter = activity?.let { FavouritesAdapter(springsList, it) }!!
        favrecyclerview.adapter = adapter
        favrecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if (maxItem > count) {
                        count++
                        Log.e("karthik", "$count")
                        progressBar.visibility = VISIBLE
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


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }


}
