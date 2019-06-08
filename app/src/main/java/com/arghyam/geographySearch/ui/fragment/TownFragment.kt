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
import com.arghyam.geographySearch.adapter.TownAdapter
import com.arghyam.geographySearch.interfaces.GeographyInterface
import com.arghyam.geographySearch.interfaces.SearchInterface
import com.arghyam.geographySearch.model.TownModel
import kotlinx.android.synthetic.main.content_state.*


class TownFragment : Fragment() {
    private var townList = ArrayList<TownModel>()


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
        activity?.setTitle("Select Town")
        initRecyclerView()
    }

    private fun initRecyclerView() {
        stateRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = activity?.let { TownAdapter(townList, it,geographyInterface) }
        stateRecyclerView.adapter = adapter

        townList.add(TownModel("Town 1"))
        townList.add(TownModel("Town 2"))

        townList.add(TownModel("Town 3"))

        townList.add(TownModel("Town 4"))

        townList.add(TownModel("Town 5"))

        townList.add(TownModel("Town 6"))

        townList.add(TownModel("Town 7"))

        townList.add(TownModel("Town 8"))


    }

    private var geographyInterface = object : GeographyInterface {
        override fun onGeographyItemClickListener(position: Int) {
            Log.e("town", townList[position].townName)
            (activity as SearchInterface).getTitle("" + position, townList[position].townName, 4)
            activity!!.supportFragmentManager.popBackStack()
        }
    }

}
