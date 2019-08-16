package com.arghyam.geographySearch.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.geographySearch.adapter.StateAdapter
import com.arghyam.geographySearch.interfaces.GeographyInterface
import com.arghyam.geographySearch.interfaces.SearchInterface
import com.arghyam.geographySearch.model.*
import com.arghyam.geographySearch.repository.StatesRepository
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_state.*
import javax.inject.Inject
import androidx.appcompat.app.AppCompatActivity




class StateFragment : Fragment() {
    private var stateList = ArrayList<StateModel>()
    private lateinit var mStatesViewModel: StatesViewModel
    @Inject
    lateinit var statesRepository: StatesRepository

    companion object {
        fun newInstance(): StateFragment {
            var fragmentState = StateFragment()
            var args = Bundle()
            fragmentState.arguments = args
            return fragmentState
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_state, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()
    }

    private fun init() {
        activity?.title = "Select State"
        initComponent()
        initRepository()
        observeData()
        sendRequest()
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = state_toolbar as Toolbar
        toolbar.title = "Select State"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener { activity!!.onBackPressed() }
    }

    private fun observeData() {
        mStatesViewModel.getStatesSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("States Fragment", "Success")
            initData(it)
        })

        mStatesViewModel.getStatesError().observe(this, androidx.lifecycle.Observer {
            Log.d("States Fragment", "Api Error")
        })
    }

    private fun initData(responseModel: ResponseModel?) {
        saveStates(responseModel)
    }

    private fun saveStates(responseModel: ResponseModel?) {

        var responseData: AllStatesModel = Gson().fromJson(
            responseModel?.response?.responseObject?.let { ArghyamUtils().convertToString(it) },
            object : TypeToken<AllStatesModel>() {}.type
        )
        initRecyclerView(responseData)
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
            request = GetAllStatesModel(
                springs = AllStatesRequest(
                    type = "states"
                )
            )
        )
        makeApiCall(mRequestData)
    }

    private fun makeApiCall(mRequestData: RequestModel) {
        mStatesViewModel.myStatesApi(activity!!.applicationContext, mRequestData)
    }

    private fun initRepository() {
        mStatesViewModel = ViewModelProviders.of(this).get(StatesViewModel::class.java)
        mStatesViewModel.setStatesRepository(statesRepository)
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initRecyclerView(responseData: AllStatesModel) {
        stateRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = activity?.let { StateAdapter(stateList, it, geographyInterface) }
        stateRecyclerView.adapter = adapter

//        stateList.add(StateModel("Andra Pradesh"))
        if (responseData.states.size == 0) {
            no_districts.visibility = View.VISIBLE
        }
        for (i in 0 until responseData.states.size) {
            stateList.add(StateModel(responseData.states[i].states, responseData.states[i].osid,responseData.states[i].count))
        }
        Log.e("StateFragment", stateList.toString())
    }


    private var geographyInterface = object : GeographyInterface {
        override fun onGeographyItemClickListener(position: Int) {
            Log.e("state", stateList[position].stateName)
            (activity as SearchInterface).getTitle(
                "" + position,
                stateList[position].stateName,
                stateList[position].stateOsid,
                1
            )
            activity!!.supportFragmentManager.popBackStack()
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.e("detached","called")
        (activity as SearchInterface).isClicked(false,1)
    }

}
