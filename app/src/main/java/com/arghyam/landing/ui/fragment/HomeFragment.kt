package com.arghyam.landing.ui.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.arghyam.R
import com.arghyam.landing.adapters.LandingAdapter
import com.arghyam.landing.model.LandingModel
import kotlinx.android.synthetic.main.activity_landing.*
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {
    private var springs = ArrayList<LandingModel>()
    private var count : Int = 0
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
        return rootView

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        springRecyclerView.layoutManager = LinearLayoutManager(activity)




        springs.add(LandingModel("Spring 1","Village 1","https://picsum.photos/200/300"))
        springs.add(LandingModel("Spring 2","Village 2","https://picsum.photos/200/300"))

        springs.add(LandingModel("Spring 3","Village 3","https://picsum.photos/200/300"))
        springs.add(LandingModel("Spring 4","Village 4","https://picsum.photos/200/300"))

        springs.add(LandingModel("Spring 5","Village 5","https://picsum.photos/200/300"))

        val adapter = activity?.let { LandingAdapter(springs, it) }
        springRecyclerView.adapter = adapter
        springRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    Log.d(""+ count,"count")
                    count++
                    pagination()
                }
            }
        })
    }

    fun pagination() {
        when (count){
            2 -> {
                springs.add(LandingModel("Spring 6","Village 6","https://picsum.photos/200/300"))
                springs.add(LandingModel("Spring 7","Village 7","https://picsum.photos/200/300"))

                springs.add(LandingModel("Spring 8","Village 8","https://picsum.photos/200/300"))
                springs.add(LandingModel("Spring 9","Village 9","https://picsum.photos/200/300"))

                springs.add(LandingModel("Spring 10","Village 10","https://picsum.photos/200/300"))

            }
        }
        springRecyclerView.adapter?.notifyDataSetChanged()
    }



}
