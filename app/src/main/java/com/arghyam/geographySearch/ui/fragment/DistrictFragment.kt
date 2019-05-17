package com.arghyam.geographySearch.ui.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.arghyam.R
import com.arghyam.geographySearch.adapter.DistrictAdapter
import com.arghyam.geographySearch.model.DistrictModel
import kotlinx.android.synthetic.main.content_state.*


class DistrictFragment : Fragment() {
    private var districtList = ArrayList<DistrictModel>()


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
        activity?.setTitle("Select District")
        initRecyclerView()
    }

    private fun initRecyclerView() {
        stateRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = activity?.let { DistrictAdapter(districtList, it) }
        stateRecyclerView.adapter = adapter

        districtList.add(DistrictModel("Ahmedabad"))
        districtList.add(DistrictModel("Amreli"))

        districtList.add(DistrictModel("Anand"))

        districtList.add(DistrictModel("Aravalli"))

        districtList.add(DistrictModel("Banaskantha"))

        districtList.add(DistrictModel("Bharuch"))



    }

}
