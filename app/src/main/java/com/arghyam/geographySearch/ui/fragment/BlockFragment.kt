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
import com.arghyam.geographySearch.adapter.BlockAdapter
import com.arghyam.geographySearch.interfaces.GeographyInterface
import com.arghyam.geographySearch.interfaces.SearchInterface
import com.arghyam.geographySearch.model.*
import com.arghyam.geographySearch.repository.BlocksRepository
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_state.*
import javax.inject.Inject


class BlockFragment : Fragment() {
    private var blockList = ArrayList<BlockModel>()
    private lateinit var mBlocksViewModel: BlocksViewModel
    @Inject
    lateinit var blocksRepository: BlocksRepository
    private lateinit var osid:String


    companion object {
        fun newInstance(): BlockFragment {
            var fragmentBlock = BlockFragment()
            var args = Bundle()
            fragmentBlock.arguments = args
            return fragmentBlock
        }

    }


    override fun onCreate(savedInstanceBlock: Bundle?) {
        super.onCreate(savedInstanceBlock)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceBlock: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_state, container, false)
    }

    override fun onActivityCreated(savedInstanceBlock: Bundle?) {
        super.onActivityCreated(savedInstanceBlock)

        init()
    }

    private fun init() {
        activity?.title = "Select Block"
        initComponent()
        initRepository()
        observeData()
        sendRequest()
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = state_toolbar as Toolbar
        toolbar.title = "Select Block"
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
        mBlocksViewModel = ViewModelProviders.of(this).get(BlocksViewModel::class.java)
        mBlocksViewModel.setBlocksRepository(blocksRepository)
    }

    private fun observeData() {
        mBlocksViewModel.getBlocksSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("Blocks Fragment", "Success")
            initData(it)
        })

        mBlocksViewModel.getBlocksError().observe(this, androidx.lifecycle.Observer {
            Log.d("Blocks Fragment", "Api Error")
        })
    }

    private fun initData(responseModel: ResponseModel?) {
        saveBlocks(responseModel)
    }

    private fun saveBlocks(responseModel: ResponseModel?) {

        var responseData: AllBlocksModel = Gson().fromJson(
            responseModel?.response?.responseObject?.let { ArghyamUtils().convertToString(it) },
            object : TypeToken<AllBlocksModel>() {}.type
        )
        initRecyclerView(responseData)
    }



    private fun initRecyclerView(responseData: AllBlocksModel) {
        stateRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = activity?.let { BlockAdapter(blockList, it, geographyInterface) }
        stateRecyclerView.adapter = adapter
        if(responseData.blocks.size==0){
            no_districts.visibility = View.VISIBLE
        }
        for(i in 0 until responseData.blocks.size){
            blockList.add(BlockModel(responseData.blocks[i].blocks,responseData.blocks[i].osid))
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
            request = GetBlocksModel(
                subDistricts = BlocksRequest(
                    fKeyDistricts = osid
                )
            )
        )
        makeApiCall(mRequestData)
    }

    private fun makeApiCall(mRequestData: RequestModel) {
        mBlocksViewModel.myBlocksApi(activity!!.applicationContext,mRequestData)
    }

    private var geographyInterface = object : GeographyInterface {
        override fun onGeographyItemClickListener(position: Int) {
            Log.e("block", blockList[position].blockName)
            (activity as SearchInterface).getTitle("" + position, blockList[position].blockName,blockList[position].blockOsid ,3)
            activity!!.supportFragmentManager.popBackStack()
        }
    }

}
