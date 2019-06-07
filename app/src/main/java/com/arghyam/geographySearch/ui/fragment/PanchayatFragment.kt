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
import com.arghyam.geographySearch.adapter.PanchyatAdapter
import com.arghyam.geographySearch.interfaces.GeographyInterface
import com.arghyam.geographySearch.interfaces.SearchInterface
import com.arghyam.geographySearch.model.PanchayatModel
import kotlinx.android.synthetic.main.content_state.*


class PanchayatFragment : Fragment() {
    private var panchayatList = ArrayList<PanchayatModel>()


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
        activity?.setTitle("Select Gram Panchayat")
        initRecyclerView()
    }

    private fun initRecyclerView() {
        stateRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = activity?.let { PanchyatAdapter(panchayatList, it,geographyInterface) }
        stateRecyclerView.adapter = adapter

        panchayatList.add(PanchayatModel("Gram Panchayat 1"))
        panchayatList.add(PanchayatModel("Gram Panchayat 2"))

        panchayatList.add(PanchayatModel("Gram Panchayat 3"))

        panchayatList.add(PanchayatModel("Gram Panchayat 4"))

        panchayatList.add(PanchayatModel("Gram Panchayat 5"))

        panchayatList.add(PanchayatModel("Gram Panchayat 6"))

        panchayatList.add(PanchayatModel("Gram Panchayat 7"))

        panchayatList.add(PanchayatModel("Gram Panchayat 8"))


    }

    private var geographyInterface = object : GeographyInterface {
        override fun onGeographyItemClickListener(position: Int) {
            Log.e("panchayat", panchayatList[position].panchayatName)
            (activity as SearchInterface).getTitle("" + position, panchayatList[position].panchayatName, 5)
            activity!!.supportFragmentManager.popBackStack()
        }
    }
}
