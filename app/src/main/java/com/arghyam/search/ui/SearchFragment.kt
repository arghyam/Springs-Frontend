package com.arghyam.search.ui


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.*
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.solver.GoalRow
import androidx.recyclerview.widget.LinearLayoutManager

import com.arghyam.R
import com.arghyam.geographySearch.ui.activity.GeographySearchActivity
import com.arghyam.landing.adapters.LandingAdapter
import com.arghyam.landing.model.LandingModel
import com.arghyam.search.adapter.RecentSearchAdapter
import com.arghyam.search.interfaces.RecentSearchInterface
import com.arghyam.search.model.RecentSearchModel
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import kotlinx.android.synthetic.main.content_new_spring.*
import kotlinx.android.synthetic.main.content_search.*
import androidx.core.view.MenuItemCompat.getActionView
import kotlinx.android.synthetic.main.fragment_search.*


/**
 * A simple [Fragment] subclass.
 *
 */
class SearchFragment : Fragment() {

   private var recentSearchList = ArrayList<RecentSearchModel>()
    private var springsList = ArrayList<LandingModel>()
    private var displayedList = ArrayList<RecentSearchModel>()


    companion object {
        fun newInstance(): SearchFragment {
            var fragmentSearch = SearchFragment()
            var args = Bundle()
            fragmentSearch.arguments = args
            return fragmentSearch
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_search, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initRecyclerview()
        initClick()
        initSearch()
    }

    private fun initSearch() {
        search_input.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length!! > 0){
                    search_icon.setImageResource(R.drawable.ic_close)

                } else {
                    search_icon.setImageResource(R.drawable.ic_search)
                }

            }

        })
    }



    private fun initRecyclerview() {
        if(recentSearchRecyclerView.visibility == VISIBLE) {
                recentSearchRecyclerView.layoutManager = LinearLayoutManager(activity)
                val adapter = activity?.let { RecentSearchAdapter(recentSearchList, it, recentSearchInterface) }
                recentSearchRecyclerView.adapter = adapter
                recentSearchList.add(RecentSearchModel("Spring 1"))
                recentSearchList.add(RecentSearchModel("Abc"))

                recentSearchList.add(RecentSearchModel("Thana"))

                recentSearchList.add(RecentSearchModel("Darmaram"))

                recentSearchList.add(RecentSearchModel("Spring 5"))

                recentSearchList.add(RecentSearchModel("Spring 6"))

                recentSearchList.add(RecentSearchModel("Spring 7"))

                recentSearchList.add(RecentSearchModel("Spring 8"))

                recentSearchList.add(RecentSearchModel("Spring 9"))

                recentSearchList.add(RecentSearchModel("Spring 10"))

//                 displayedList.addAll(recentSearchList)
        }

        if (searchRsultRecyclerView.visibility == VISIBLE){
            Log.d("search", searchRsultRecyclerView.visibility.toString())
            searchRsultRecyclerView.layoutManager = LinearLayoutManager(activity)

            val adapter = activity?.let { LandingAdapter(springsList, it) }
            searchRsultRecyclerView.adapter = adapter

            springsList.add(LandingModel("Recent Spring 1", "Village 1", "https://picsum.photos/200/300"))
            springsList.add(LandingModel("Recent Spring 2", "Village 2", "https://picsum.photos/200/300"))
            springsList.add(LandingModel("Recent Spring 3", "Village 3", "https://picsum.photos/200/300"))
            springsList.add(LandingModel("Recent Spring 4", "Village 4", "https://picsum.photos/200/300"))
            springsList.add(LandingModel("Recent Spring 5", "Village 5", "https://picsum.photos/200/300"))
            Log.d("searchspringsList", springsList.toString())

        }

    }

    private fun initClick() {
        geo_layout.setOnClickListener {
            val intent = Intent(activity, GeographySearchActivity::class.java)
            startActivity(intent)
        }
        search_spring.setOnClickListener(){
            recentSearchRecyclerView.visibility = GONE
            searchRsultRecyclerView.visibility = VISIBLE
            recent_search.text = "Search results"
            initRecyclerview()
        }

        search_icon.setOnClickListener {
            if(search_input.text.length > 0){
                search_input.setText("")
            }
        }

    }

    private var recentSearchInterface = object : RecentSearchInterface {
        override fun onRecentSearchItemClickListener(position: Int) {
           Log.e("recent search name",recentSearchList[position].recentSearchName)

            var searchResultsList = ArrayList<LandingModel>()

        }
    }



}
