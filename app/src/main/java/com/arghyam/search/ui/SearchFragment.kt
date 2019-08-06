package com.arghyam.search.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.*
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R

import com.arghyam.commons.utils.Constants
import com.arghyam.geographySearch.ui.activity.GeographySearchActivity

import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.landing.model.AllSpringDataModel
import com.arghyam.landing.model.AllSpringModel
import com.arghyam.landing.model.LandingModel

import com.arghyam.search.model.RequestSearchResultDataModel
import com.arghyam.search.model.SearchResultsModel
import com.arghyam.search.repository.SearchRepository
import com.arghyam.search.viewmodel.SearchViewModel
import com.arghyam.search.adapter.RecentSearchAdapter
import com.arghyam.search.interfaces.RecentSearchInterface
import com.arghyam.search.model.RecentSearchModel
import com.arghyam.search.adapter.SearchResultsAdapter
import kotlinx.android.synthetic.main.content_search.*
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

class SearchFragment : Fragment() {

   private var recentSearchList = ArrayList<RecentSearchModel>()
    private var springsList = ArrayList<AllSpringDataModel>()
    private var displayedList = ArrayList<RecentSearchModel>()
    private lateinit var mSearchViewModel: SearchViewModel
    private lateinit var springCode: String

    @Inject
    lateinit var mSearchRepository: SearchRepository

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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initComponent()
        initRepository()
        observeData()
        initRecyclerview()
        initClick()
        initSearch()
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initRepository() {
        mSearchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        mSearchViewModel.setAdditionalDataRepository(mSearchRepository)
    }

    private fun observeData() {
        mSearchViewModel.getSearchDataSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("Search", "searchApi Called")
        })

        mSearchViewModel.getSearchDataError().observe(this, androidx.lifecycle.Observer {
            Log.d("Search", "Api Error")
        })
    }


    private fun makeSearchRequest() {

        var mRequestData = RequestModel(
            id = Constants.ADDITIONAL_DATA_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = RequestSearchResultDataModel(
                searchResultsModel = SearchResultsModel(
                    sprindId = 1,
                    recentSearchName = "recentSearch",
                    villageName = "villageName"
                )
            )
        )
        makeApiCall(mRequestData)
    }

    private fun makeApiCall(mRequestData: RequestModel) {
        mSearchViewModel.searchApi(activity!!.applicationContext,springCode, mRequestData)

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
                    recentSearchRecyclerView.visibility = GONE
                    searchResultRecyclerView.visibility = VISIBLE
                    recent_search.text = "Search results"
                    initRecyclerview()

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
            recentSearchList.add(RecentSearchModel("Spring 2"))
            recentSearchList.add(RecentSearchModel("Spring 3"))

//                 displayedList.addAll(recentSearchList)
        }

        if (searchResultRecyclerView.visibility == VISIBLE){
            Log.d("search", searchResultRecyclerView.visibility.toString())
            searchResultRecyclerView.layoutManager = LinearLayoutManager(activity)

            val adapter = activity?.let { SearchResultsAdapter(springsList, it) }
            searchResultRecyclerView.adapter = adapter

//            springsList.add(AllSpringDataModel("Recent Spring 1", "Village 1", "https://picsum.photos/200/300"))
            Log.d("searchspringsList", springsList.toString())

        }

    }

    private fun initClick() {
        geo_layout.setOnClickListener {
            val intent = Intent(activity, GeographySearchActivity::class.java)
            startActivity(intent)
        }

        search_icon.setOnClickListener {
            if(search_input.text.isNotEmpty()){
                search_input.setText("")
                recentSearchRecyclerView.visibility = VISIBLE
                searchResultRecyclerView.visibility = GONE
                recent_search.text = "Recent search"
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
