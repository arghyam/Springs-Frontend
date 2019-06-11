package com.arghyam.landing.ui.fragment


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
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.commons.utils.Constants.GET_ALL_SPRINGS_ID
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.adapters.LandingAdapter
import com.arghyam.landing.model.AllSpringModel
import com.arghyam.landing.model.GetAllSpringsModel
import com.arghyam.landing.model.LandingModel
import com.arghyam.landing.repository.GetAllSpringRepository
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.landing.viewmodel.GetAllSpringViewModel
import com.arghyam.landing.viewmodel.LandingViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    @Inject
    lateinit var getAllSpringRepository: GetAllSpringRepository

    private var getAllSpringViewModel: GetAllSpringViewModel? = null


    private var springsList = ArrayList<LandingModel>()
    private var count: Int = 1
    private var itemsAvailable: Boolean = true
    private lateinit var adapter: LandingAdapter
    private lateinit var landingViewModel: LandingViewModel


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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getViewModel()
        setObserver()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun getViewModel() {
        landingViewModel = ViewModelProviders.of(activity!!).get(LandingViewModel::class.java)
    }

    private fun setObserver() {
        landingViewModel.getIsGpsEnabled().observe(this, Observer {
            Log.e("Api", "Called")
            initApiCall()
        })
        Log.e("abc", "Called")
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
                initRepository()
                initApiCall()
            } else {
                activity?.let { ArghyamUtils().turnOnLocation(it) }!!
                errorItems.visibility = VISIBLE
                errorDesc.text = activity!!.resources.getText(R.string.turn_on_location_desc)
                springsLocation.visibility = GONE
            }
        }
        initFab()
//        initComponent()
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initApiCall() {
        if (itemsAvailable) {
            errorItems?.visibility = GONE
            springsLocation?.visibility = VISIBLE
            initGetAllSpring()
            getAllSpringRequest()
            pagination()
        } else {
            errorItems.visibility = VISIBLE
            errorDesc.text = activity!!.resources.getText(R.string.turn_on_location_desc)
            springsLocation.visibility = GONE
        }
    }

    private fun initGetAllSpring() {
        getAllSpringViewModel?.getAllSpringResponse()?.observe(this, Observer {
            saveGetAllSpringsData(it)
            if (getAllSpringViewModel?.getAllSpringResponse()?.hasObservers()!!) {
                getAllSpringViewModel?.getAllSpringResponse()?.removeObservers(this)
            }
        })
        getAllSpringViewModel?.getAllSpringError()?.observe(this, Observer {
            Log.e("error", it)
        })
    }

    private fun saveGetAllSpringsData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode.equals("200")) {
//            allSpring = responseModel.response.responseObject.
            Log.d("stefysuccess", responseModel.response.responseCode)
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
        getAllSpringViewModel?.getAllSpringApi(context!!, getAllSpringObject)
    }

    private fun initFab() {
        floatingActionButton.setOnClickListener {
            if (SharedPreferenceFactory(activity!!.applicationContext).getString(Constants.ACCESS_TOKEN) == "") {
                view?.let { it1 ->
                    ArghyamUtils().makeSnackbar(
                        it1,
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
                    count++
                    pagination()
                }
            }
        })
    }

    fun pagination() {
        when (count) {
            1 -> {
                springsList.add(LandingModel("Spring 1", "Village 1", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 2", "Village 2", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 3", "Village 3", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 4", "Village 4", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 5", "Village 5", "https://picsum.photos/200/300"))
            }
            2 -> {
                springsList.add(LandingModel("Spring 6", "Village 6", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 7", "Village 7", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 8", "Village 8", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 9", "Village 9", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 10", "Village 10", "https://picsum.photos/200/300"))
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun initRepository() {
        getAllSpringViewModel = ViewModelProviders.of(this).get(GetAllSpringViewModel::class.java!!)
        getAllSpringViewModel?.setGetAllSpringRepository(getAllSpringRepository)
    }

}
