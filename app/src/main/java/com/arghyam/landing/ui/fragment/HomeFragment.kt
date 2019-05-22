package com.arghyam.landing.ui.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.addspring.ui.NewSpringActivity
import com.arghyam.landing.adapters.LandingAdapter
import com.arghyam.landing.model.LandingModel
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {
    private var springsList = ArrayList<LandingModel>()
    private var count: Int = 1

    private var isLocationPermission: Boolean = true
    private var itemsAvailable: Boolean = true

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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {

        if (isLocationPermission) {
            initRecyclerView()
        } else {
            initLocationPermission()
        }

        initFab()
    }

    private fun initLocationPermission() {
        springs.visibility = GONE
        errorScreen.visibility = VISIBLE
        locationPermission.visibility = VISIBLE
        springSearch.visibility = GONE
    }

    private fun initNoSpringsFound() {
        springs.visibility = GONE
        errorScreen.visibility = VISIBLE
        locationPermission.visibility = GONE
        springSearch.visibility = VISIBLE
    }

    private fun initFab() {
        floatingActionButton.setOnClickListener {
            var intent = Intent(activity, NewSpringActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        springs.visibility = VISIBLE
        errorScreen.visibility = GONE
        springRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = activity?.let { LandingAdapter(springsList, it) }
        springRecyclerView.adapter = adapter
        springRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    count++
                    pagination()
                }
            }
        })
        if(itemsAvailable) {
            pagination()
        } else {
            initNoSpringsFound()
        }
    }

    fun pagination() {
        when (count) {
            1 -> {
                springsList.add(LandingModel("Spring 1", "Village 1", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 2", "Village 2", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 3", "Village 3", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 4", "Village 4", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 5", "Village 5", "https://picsum.photos/200/300"))
            }
            2 -> {
                springsList.add(LandingModel("Spring 6", "Village 6", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 7", "Village 7", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 8", "Village 8", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 9", "Village 9", "https://picsum.photos/200/300"))
                springsList.add(LandingModel("Spring 10", "Village 10", "https://picsum.photos/200/300"))
            }
        }
        springRecyclerView.adapter?.notifyDataSetChanged()
    }


}
