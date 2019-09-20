package com.arghyam.search.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.geographySearch.ui.activity.GeographySearchActivity
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.model.AllSpringDataModel
import com.arghyam.landing.model.SearchModel
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.landing.ui.fragment.HomeFragment
import com.arghyam.search.adapter.RecentSearchAdapter
import com.arghyam.search.adapter.SearchResultsAdapter
import com.arghyam.search.interfaces.RecentSearchInterface
import com.arghyam.search.model.*
import com.arghyam.search.repository.SearchRepository
import com.arghyam.search.viewmodel.SearchViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_search.*
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject








class SearchFragment : Fragment() {

    private var recentSearchList = ArrayList<String>()
    private var springsList = ArrayList<AllSpringDataModel>()
    private lateinit var mSearchViewModel: SearchViewModel
    private lateinit var mRecentSearchViewModel: RecentSearchViewModel

    private lateinit var searchText: String
    private var savedInstanceState: Bundle? = null

    @Inject
    lateinit var mSearchRepository: SearchRepository
    @Inject
    lateinit var mRecentSearchRepository: RecentSearchRepository

    companion object {
        fun newInstance(): SearchFragment {
            val fragmentSearch = SearchFragment()
            val args = Bundle()
            fragmentSearch.arguments = args
            return fragmentSearch
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (savedInstanceState != null) {
            val hello = savedInstanceState.getString("hello")
            Log.e("Searchsaveinstance", hello)
        }
        return inflater.inflate(R.layout.fragment_search, container, false)

    }

    private fun hideKeyboard(v: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initComponent()
        initRecyclerView()
        initRepository()
        observeData()
        initClick()
        initSearch()
        recentSearchesRequest()
    }

    private fun recentSearchesRequest() {
        val mRequestData = SharedPreferenceFactory(activity!!.applicationContext).getString(Constants.USER_ID)?.let {
            RecentSearchRequest(
                userId = it
            )
        }?.let {
            GetAllRecentSearchRequest(
                recentSearches = it
            )
        }?.let {
            RequestModel(
                id = Constants.SEARCH_SPRINGS,
                ver = BuildConfig.VER,
                ets = BuildConfig.ETS,
                params = Params(
                    did = "",
                    key = "",
                    msgid = ""
                ),
                request = it
            )
        }
        mRequestData?.let { makeApiCallRecentSearch(it) }
        Log.e("userid", SharedPreferenceFactory(activity!!.applicationContext).getString(Constants.USER_ID))
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
        recentSearchRecyclerView.visibility = VISIBLE
        if (Intent.getIntent("searchText") != null)
            searchText = Intent.getIntent("searchText").toString()
    }

    private fun initRepository() {
        mSearchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        mSearchViewModel.setSearchDataRepository(mSearchRepository)

        mRecentSearchViewModel = ViewModelProviders.of(this).get(RecentSearchViewModel::class.java)
        mRecentSearchViewModel.setRecentSearchRepository(mRecentSearchRepository)
    }

    private fun observeData() {
        mSearchViewModel.getSearchDataSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("Search", "searchApi Called")
            saveFreeTextSearchData(it)
        })

        mSearchViewModel.getSearchDataError().observe(this, androidx.lifecycle.Observer {
            Log.d("Search", "Api Error")
        })

        mRecentSearchViewModel.getRecentSearchesSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("States Fragment", "Success")
            saveRecentSearches(it)
        })

        mRecentSearchViewModel.getRecentSearchesError().observe(this, androidx.lifecycle.Observer {
            Log.d("States Fragment", "Api Error")
        })
    }

    private fun saveRecentSearches(responseModel: ResponseModel?) {
        Log.e("search", responseModel!!.response.responseObject.toString())

        val responseData: AllRecentSearchModel = Gson().fromJson(
            responseModel.response.responseObject.let { ArghyamUtils().convertToString(it) },
            object : TypeToken<AllRecentSearchModel>() {}.type
        )
        recentSearchList.clear()
        recentSearchList.addAll(responseData.recentSearch)
        initRecyclerView()
    }

    private fun saveFreeTextSearchData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            val responseData: SearchModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<SearchModel>() {}.type
            )
            springsList.clear()
            if (responseData.springs.isEmpty())
                ArghyamUtils().longToast(activity as AppCompatActivity, "Please try search by geography")
            else
                springsList.addAll(responseData.springs)
            initRecyclerView()
        }
    }

    private fun makeSearchRequest(text: String) {
        Log.e("Search", text)
        val mRequestData = SharedPreferenceFactory(activity!!.applicationContext).getString(Constants.USER_ID)?.let {
            SearchResultsModel(
                userId = it,
                searchString = text
            )
        }?.let {
            RequestSearchResultDataModel(
                springs = it
            )
        }?.let {
            RequestModel(
                id = Constants.SEARCH_SPRINGS,
                ver = BuildConfig.VER,
                ets = BuildConfig.ETS,
                params = Params(
                    did = "",
                    key = "",
                    msgid = ""
                ),
                request = it
            )
        }
        mRequestData?.let { makeApiCall(it) }
    }

    private fun makeApiCallRecentSearch(mRequestData: RequestModel) {
        mRecentSearchViewModel.myRecentSearchesApi(activity!!.applicationContext, mRequestData)
    }

    private fun makeApiCall(mRequestData: RequestModel) {
        mSearchViewModel.searchApi(activity!!.applicationContext, mRequestData)

    }

    private fun initSearch() {
        search_input.setOnEditorActionListener { _, actionId, _ ->
            Log.e("Search", search_input.text.toString())
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.e("Search", search_input.text.toString())
                makeSearchRequest(search_input.text.toString())
            }
            true
        }
        search_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //Nothing Here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //Nothing Here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! > 0) {
                    search_icon.setImageResource(R.drawable.ic_close)
                    recentSearchRecyclerView.visibility = GONE
                    searchResultRecyclerView.visibility = VISIBLE
                    recent_search.text = "Search results"

                } else {
                    search_icon.setImageResource(R.drawable.ic_search)
                    recentSearchRecyclerView.visibility = VISIBLE
                    searchResultRecyclerView.visibility = GONE
                    springsList.clear()
                    recent_search.text = "Recent results"
                    recentSearchesRequest()
                }

            }

        })
    }

    private fun initRecyclerView() {
        if (recentSearchRecyclerView.visibility == VISIBLE) {
            if (recentSearchList.isNotEmpty()) {
                recentSearchRecyclerView.layoutManager = activity?.let { LinearLayoutManager(it) }
                val adapter = activity?.let { RecentSearchAdapter(recentSearchList, it, recentSearchInterface) }
                recentSearchRecyclerView.adapter = adapter
                no_recent_searches.visibility = GONE
                Log.d("RecentSearchList", recentSearchList.toString())
            } else
                no_recent_searches.visibility = VISIBLE
        }

        if (searchResultRecyclerView.visibility == VISIBLE) {
            searchResultRecyclerView.layoutManager = LinearLayoutManager(activity)

            val adapter = activity?.let { SearchResultsAdapter(springsList, it) }
            searchResultRecyclerView.adapter = adapter
            Log.d("searchSpringsList", springsList.toString())

        }

    }

    private fun initClick() {
        geo_layout.setOnClickListener {
            val intent = Intent(activity, GeographySearchActivity::class.java)
            startActivityForResult(intent, 1)
        }

        search_icon.setOnClickListener {
            if (search_input.text.isNotEmpty()) {
                search_input.setText("")
                springsList.clear()
                recentSearchRecyclerView.visibility = VISIBLE
                searchResultRecyclerView.visibility = GONE
                recent_search.text = "Recent search"
                recentSearchesRequest()
            }
        }

        back_icon.setOnClickListener {
            showHome()
            (this.activity as LandingActivity?)?.switchHome()
        }
    }

    /**
     * replaces the current fragment with home fragment
     */
    private fun showHome() {
        val fragment = HomeFragment.newInstance()
        this.fragmentManager?.beginTransaction()?.replace(R.id.content, fragment, fragment.javaClass.simpleName)
            ?.addToBackStack(fragment.javaClass.simpleName)?.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.getStringExtra("searchText")?.let { makeSearchRequest(it) }
            recentSearchRecyclerView.visibility = GONE
            searchResultRecyclerView.visibility = VISIBLE
            recent_search.text = "Search results"
        }
    }

    private var recentSearchInterface = object : RecentSearchInterface {
        override fun onRecentSearchItemClickListener(position: Int) {
            Log.e("recent search name", recentSearchList[position])
            makeSearchRequest(recentSearchList[position])
            recentSearchRecyclerView.visibility = GONE
            searchResultRecyclerView.visibility = VISIBLE
            recent_search.text = "Search results"
        }
    }


}
