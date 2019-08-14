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
import com.arghyam.geographySearch.adapter.PanchyatAdapter
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


class PanchayatFragment : Fragment() {
    private var panchayatList = ArrayList<PanchayatModel>()
    private lateinit var mVillagesViewModel: VillagesViewModel
    @Inject
    lateinit var villagesRepository: VillagesRepository
    private lateinit var osid:String

    companion object {
        fun newInstance(): PanchayatFragment {
            var fragmentPanchayat = PanchayatFragment()
            var args = Bundle()
            fragmentPanchayat.arguments = args
            return fragmentPanchayat
        }

    }

    override fun onCreate(savedInstancePanchayat: Bundle?) {
        super.onCreate(savedInstancePanchayat)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstancePanchayat: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_state, container, false)
    }

    override fun onActivityCreated(savedInstancePanchayat: Bundle?) {
        super.onActivityCreated(savedInstancePanchayat)

        init()
    }

    private fun init() {
        activity?.title = "Select Gram Panchayat"
        initComponent()
        initRepository()
        observeData()
        sendRequest()
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = state_toolbar as Toolbar
        toolbar.title = "Select Gram Panchayat"
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
        mVillagesViewModel = ViewModelProviders.of(this).get(VillagesViewModel::class.java)
        mVillagesViewModel.setVillagesRepository(villagesRepository)
    }

    private fun observeData() {
        mVillagesViewModel.getVillagesSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("Blocks Fragment", "Success")
            initData(it)
        })

        mVillagesViewModel.getVillagesError().observe(this, androidx.lifecycle.Observer {
            Log.d("Blocks Fragment", "Api Error")
        })
    }

    private fun initData(responseModel: ResponseModel?) {
        saveBlocks(responseModel)
    }

    private fun saveBlocks(responseModel: ResponseModel?) {

        var responseData: AllVillagesModel = Gson().fromJson(
            responseModel?.response?.responseObject?.let { ArghyamUtils().convertToString(it) },
            object : TypeToken<AllVillagesModel>() {}.type
        )
        initRecyclerView(responseData)
    }

    private fun initRecyclerView(responseData: AllVillagesModel) {
        stateRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = activity?.let { PanchyatAdapter(panchayatList, it,geographyInterface) }
        stateRecyclerView.adapter = adapter
        if(responseData.villages.size==0){
            no_districts.visibility = View.VISIBLE
        }
        Log.e("Anirudh", responseData.villages.size.toString())
        for(i in 0 until responseData.villages.size){
            panchayatList.add(PanchayatModel(responseData.villages[i].villages,responseData.villages[i].osid))
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
            request = GetVillagesModel(
                villages = VillagesRequest(
                    fKeySubDistricts = osid
                )
            )
        )
        makeApiCall(mRequestData)
    }

    private fun makeApiCall(mRequestData: RequestModel) {
        mVillagesViewModel.myVillagesApi(activity!!.applicationContext,mRequestData)
    }

    private var geographyInterface = object : GeographyInterface {
        override fun onGeographyItemClickListener(position: Int) {
            Log.e("panchayat", panchayatList[position].panchayatName)
            (activity as SearchInterface).getTitle("" + position, panchayatList[position].panchayatName, panchayatList[position].osid,5)
            activity!!.supportFragmentManager.popBackStack()
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.e("detached","called")
        (activity as SearchInterface).isClicked(false,5)
    }

}
