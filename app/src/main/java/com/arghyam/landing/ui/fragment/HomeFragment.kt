package com.arghyam.landing.ui.fragment


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.addspring.ui.NewSpringActivity
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.adapters.LandingAdapter
import com.arghyam.landing.model.LandingModel
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.landing.viewmodel.LandingViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    private var springsList = ArrayList<LandingModel>()
    private var count: Int = 1
    private var itemsAvailable: Boolean = true
    private lateinit var adapter: LandingAdapter
    private lateinit var landingViewModel: LandingViewModel


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
        getViewModel()
        setObserver()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun getViewModel() {
        landingViewModel = ViewModelProviders.of(activity!!).get(LandingViewModel::class.java)
    }

    private fun setObserver() {
        landingViewModel.getIsGpsEnabled().observe(this, Observer {
            Log.e("Api", "Called")
            initApiCall()
        })
        Log.e("abc", "Called")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        if (ArghyamUtils().permissionGranted(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initRecyclerView()
            if (activity?.let { ArghyamUtils().isLocationEnabled(it) }!!) {
                initApiCall()
            } else {
                activity?.let { ArghyamUtils().turnOnLocation(it) }!!
                errorItems.visibility = VISIBLE
                errorDesc.text = activity!!.resources.getText(R.string.turn_on_location_desc)
                springsLocation.visibility = GONE
            }
        }
        initFab()
    }


    private fun initApiCall() {
        if (itemsAvailable) {
            errorItems?.visibility = GONE
            springsLocation?.visibility = VISIBLE
            pagination()
        } else {
            errorItems.visibility = VISIBLE
            errorDesc.text = activity!!.resources.getText(R.string.turn_on_location_desc)
            springsLocation.visibility = GONE
        }
    }

    private fun initFab() {
        floatingActionButton.setOnClickListener {
                activity?.startActivity(Intent(activity, NewSpringActivity::class.java))
        }
    }


    private fun initRecyclerView() {
        springsLocation.visibility = VISIBLE
        springRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = activity?.let { LandingAdapter(springsList, it) }!!
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
        adapter.notifyDataSetChanged()
    }

}
