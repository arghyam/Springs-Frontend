package com.arghyam.geographySearch.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig

import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.geographySearch.adapter.DistrictAdapter
import com.arghyam.geographySearch.interfaces.GeographyInterface
import com.arghyam.geographySearch.interfaces.SearchInterface
import com.arghyam.geographySearch.model.*
import com.arghyam.geographySearch.repository.DistrictsRepository
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_state.*
import javax.inject.Inject




class DistrictFragment : Fragment() {
    private var districtList = ArrayList<DistrictModel>()
    private lateinit var mDistrictsViewModel: DistrictsViewModel
    @Inject
    lateinit var districtsRepository: DistrictsRepository
    private lateinit var osid:String

    companion object {
        fun newInstance(): DistrictFragment {
            var fragmentDistrict = DistrictFragment()
            var args = Bundle()
            fragmentDistrict.arguments = args
            return fragmentDistrict
        }

    }

    override fun onCreate(savedInstanceDistrict: Bundle?) {
        super.onCreate(savedInstanceDistrict)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceDistrict: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_state, container, false)
    }

    override fun onActivityCreated(savedInstanceDistrict: Bundle?) {
        super.onActivityCreated(savedInstanceDistrict)

        init()
    }

    private fun init() {
        activity?.title = "Select District"
        initComponent()
        initRepository()
        observeData()
        sendRequest()
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = state_toolbar as Toolbar
        toolbar.title = "Select District"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener { activity!!.onBackPressed() }
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
            request = GetDistrictsModel(
                districts = DistrictsRequest(
                    fKeyState = osid
                )
            )
        )
        makeApiCall(mRequestData)    }

    private fun makeApiCall(mRequestData: RequestModel) {
        mDistrictsViewModel.myDistrictsApi(activity!!.applicationContext,mRequestData)
    }

    private fun observeData() {
        mDistrictsViewModel.getDistrictsSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("District Fragment", "Success")
            initData(it)
        })

        mDistrictsViewModel.getDistrictsError().observe(this, androidx.lifecycle.Observer {
            Log.d("District Fragment", "Api Error")
        })
    }

    private fun initData(responseModel: ResponseModel?) {
        saveDistricts(responseModel)
    }

    private fun saveDistricts(responseModel: ResponseModel?) {
        Log.e("States Fragment", ArghyamUtils().convertToString(responseModel!!.response.responseObject))

        var responseData: AllDistrictModel = Gson().fromJson(
            responseModel.response.responseObject.let { ArghyamUtils().convertToString(it) },
            object : TypeToken<AllDistrictModel>() {}.type
        )
        initRecyclerView(responseData)
    }

    private fun initRepository() {
        mDistrictsViewModel = ViewModelProviders.of(this).get(DistrictsViewModel::class.java)
        mDistrictsViewModel.setDistrictsRepository(districtsRepository)
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

    private fun initRecyclerView(responseData: AllDistrictModel) {
        stateRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = activity?.let { DistrictAdapter(districtList, it, geographyInterface) }
        stateRecyclerView.adapter = adapter
        if(responseData.districts.size==0){
            no_districts.visibility = VISIBLE
        }
        for(i in 0 until responseData.districts.size){
            districtList.add(DistrictModel(responseData.districts[i].districts,responseData.districts[i].osid))
        }


    }

    private var geographyInterface = object : GeographyInterface {
        override fun onGeographyItemClickListener(position: Int) {
            Log.e("block", districtList[position].districtName)
            (activity as SearchInterface).getTitle("" + position, districtList[position].districtName, districtList[position].districtOsid ,2)
            activity!!.supportFragmentManager.popBackStack()
        }
    }

}
