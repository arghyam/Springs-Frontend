package com.arghyam.geographySearch.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig

import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.geographySearch.adapter.TownAdapter
import com.arghyam.geographySearch.interfaces.GeographyInterface
import com.arghyam.geographySearch.interfaces.SearchInterface
import com.arghyam.geographySearch.model.*
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_state.*
import javax.inject.Inject


class TownFragment : Fragment() {
    private var townList = ArrayList<TownModel>()
    private lateinit var mTownViewModel: TownViewModel
    @Inject
    lateinit var townRepository: TownRepository
    private lateinit var osid:String

    companion object {
        fun newInstance(): TownFragment {
            var fragmentTown = TownFragment()
            var args = Bundle()
            fragmentTown.arguments = args
            return fragmentTown
        }

    }

    override fun onCreate(savedInstanceTown: Bundle?) {
        super.onCreate(savedInstanceTown)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceTown: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_state, container, false)
    }

    override fun onActivityCreated(savedInstanceTown: Bundle?) {
        super.onActivityCreated(savedInstanceTown)

        init()
    }

    private fun init() {
        activity?.title = "Select Town"
        initComponent()
        initRepository()
        observeData()
        sendRequest()
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = state_toolbar as Toolbar
        toolbar.title = "Select Town"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener { activity!!.onBackPressed() }
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
        readBundle(arguments)
    }

    private fun readBundle(bundle: Bundle?) {
        if (bundle != null) {
            osid = bundle.getString("osid")!!
        }
    }

    private fun initRepository() {
        mTownViewModel = ViewModelProviders.of(this).get(TownViewModel::class.java)
        mTownViewModel.setTownRepository(townRepository)
    }

    private fun observeData() {
        mTownViewModel.getTownSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("Blocks Fragment", "Success")
            initData(it)
        })

        mTownViewModel.getTownError().observe(this, androidx.lifecycle.Observer {
            Log.d("Blocks Fragment", "Api Error")
        })
    }

    private fun initData(responseModel: ResponseModel?) {
        saveTown(responseModel)
    }

    private fun saveTown(responseModel: ResponseModel?) {

        var responseData: AllTownsModel = Gson().fromJson(
            responseModel?.response?.responseObject?.let { ArghyamUtils().convertToString(it) },
            object : TypeToken<AllTownsModel>() {}.type
        )
        initRecyclerView(responseData)
    }


    private fun initRecyclerView(responseData: AllTownsModel) {
        stateRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = activity?.let { TownAdapter(townList, it,geographyInterface) }
        stateRecyclerView.adapter = adapter
        if(responseData.town.size==0){
            no_districts.visibility = View.VISIBLE
        }
        for(i in 0 until responseData.town.size){
            townList.add(TownModel(responseData.town[i].town,responseData.town[i].osid))
        }

    }

    private fun sendRequest() {
        var mRequestData = RequestModel(
            id = Constants.CREATE_STATE,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = GetTownModel(
                cities = TownRequest(
                    fKeySubDistricts = osid
                )
            )
        )
        makeApiCall(mRequestData)
    }

    private fun makeApiCall(mRequestData: RequestModel) {
        mTownViewModel.myTownApi(activity!!.applicationContext,mRequestData)
    }


    private var geographyInterface = object : GeographyInterface {
        override fun onGeographyItemClickListener(position: Int) {
            Log.e("town", townList[position].townName)
            (activity as SearchInterface).getTitle("" + position, townList[position].townName, townList[position].osid,4)
            activity!!.supportFragmentManager.popBackStack()
        }
    }

}
