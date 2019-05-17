package com.arghyam.geographySearch.ui.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.arghyam.R
import com.arghyam.geographySearch.adapter.StateAdapter
import com.arghyam.geographySearch.interfaces.GeographyInterface
import com.arghyam.geographySearch.interfaces.SearchInterface
import com.arghyam.geographySearch.model.StateModel
import com.bumptech.glide.Glide.init
import kotlinx.android.synthetic.main.content_state.*

class StateFragment : Fragment() {
    private var stateList = ArrayList<StateModel>()


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
        activity?.setTitle("Select State")
        initRecyclerView()
    }

    private fun initRecyclerView() {
        stateRecyclerView.layoutManager = LinearLayoutManager(activity)
            val adapter = activity?.let { StateAdapter(stateList, it,geographyInterface) }
            stateRecyclerView.adapter = adapter

        stateList.add(StateModel("Andra Pradesh"))
        stateList.add(StateModel("Andra Pradesh"))

        stateList.add(StateModel("Assam"))

        stateList.add(StateModel("Bihar"))

        stateList.add(StateModel("Chhattisgarh"))

        stateList.add(StateModel("Goa"))



    }

    private var geographyInterface = object : GeographyInterface {
        override fun onGeographyItemClickListener(position: Int) {
            Log.e("state", stateList[position].stateName)
            (activity as SearchInterface).getTitle("" + position, stateList[position].stateName, 1)
        }
    }

}
